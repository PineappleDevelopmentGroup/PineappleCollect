package sh.miles.collector.tile

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import sh.miles.collector.GlobalConfig
import sh.miles.collector.Registries
import sh.miles.collector.configuration.CollectorConfiguration
import sh.miles.collector.configuration.UpgradeConfiguration
import sh.miles.collector.hook.Plugins
import sh.miles.collector.menu.CollectorMenu
import sh.miles.collector.tile.event.SellActionEvent
import sh.miles.collector.upgrade.level.SellMultiplierLevel
import sh.miles.collector.util.COLLECTOR_ACCESS_BYPASS
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapple.tiles.api.TileType
import sh.miles.pineapple.tiles.api.Tiles
import sh.miles.pineapple.tiles.internal.util.TileKeys
import java.text.DecimalFormat
import java.util.Locale

object CollectorTileType : TileType<CollectorTile>(true) {

    private val DECIMAL_FORMAT = DecimalFormat.getCurrencyInstance(Locale.US)
    private val excludeItemKeys = mutableSetOf(
        COLLECTOR_OWNER.toString(),
        COLLECTOR_LOCATION.toString(),
        COLLECTOR_DISPLAY_MSB.toString(),
        COLLECTOR_DISPLAY_LSB.toString()
    )

    override fun createTile(): CollectorTile {
        return CollectorTile()
    }

    override fun createTile(item: ItemStack): CollectorTile {
        val tile = createTile()
        tile.load(item.itemMeta!!.persistentDataContainer)
        return tile
    }

    fun createItem(configuration: CollectorConfiguration): ItemStack {
        return createItemShell(configuration).persistentData(
            COLLECTOR_CONFIGURATION, PersistentDataType.STRING, configuration.id
        ).build()
    }

    override fun createItem(tile: CollectorTile?): ItemStack {
        if (tile == null) throw UnsupportedOperationException("createItem(tile) is not supported for nullable tile")
        return createItemShell(tile).build()
    }

    override fun createItemShell(tile: CollectorTile?): ItemBuilder {
        if (tile == null) throw UnsupportedOperationException("createItem(tile) is not supported for nullable tile")
        val configuration = tile.configuration
        return createItemShell(configuration).persistentData { tile.save(it, excludeItemKeys) }
    }

    private fun createItemShell(configuration: CollectorConfiguration): ItemBuilder {
        return ItemBuilder.modifyStack(configuration.item.buildSpec())
            .persistentData(TileKeys.getTileTypeKey(), PersistentDataType.STRING, key.toString())
    }

    override fun getKey(): NamespacedKey {
        return COLLECTOR_TILE_KEY
    }

    override fun onPlace(event: BlockPlaceEvent, tile: CollectorTile) {
        val player = event.player
        tile.owner = player.uniqueId
        val location = event.block.location
        val currentCollectors = Tiles.getInstance().getTiles(location.chunk)
        if (currentCollectors.isNotEmpty()) {
            event.isCancelled = true
            player.spigot().sendMessage(GlobalConfig.COLLECTOR_ALREADY_PLACED.component())
            return
        }

        tile.textDisplayUUID = tile.configuration.hologram.spawn(
            location, mutableMapOf<String, Any>(
                "sell_price" to "$0.00"
            )
        ) {
            it.billboard = Display.Billboard.CENTER
            it.isShadowed = false
        }.uniqueId
        this.tickDisplay(tile)
        tile.location = location

        tile.configuration.placeSound.play(player.location)
    }

    override fun onBreak(event: BlockBreakEvent, tile: CollectorTile) {
        val player = event.player
        if (tile.owner != player.uniqueId && !tile.accessWhitelist.contains(player.uniqueId)) {
            event.isCancelled = true
            player.spigot().sendMessage(GlobalConfig.NOT_WHITELISTED.component())
            return
        }

        if (tile.textDisplayUUID != null) {
            val textDisplay = Bukkit.getEntity(tile.textDisplayUUID!!)
                ?: throw IllegalStateException("For some reason the specified text display does not exist couldn't find uuid ${tile.textDisplayUUID}")
            textDisplay.remove()
        }
        tile.location = null
        tile.configuration.breakSound.play(event.block.location)
    }

    override fun onInteract(event: PlayerInteractEvent, tile: CollectorTile) {
        if (event.hand == EquipmentSlot.OFF_HAND) return
        val player = event.player

        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            event.setUseInteractedBlock(Event.Result.DENY)
            if ((tile.owner != player.uniqueId && !tile.accessWhitelist.contains(player.uniqueId)) && !player.hasPermission(COLLECTOR_ACCESS_BYPASS) && !player.isOp) {
                event.isCancelled = true
                player.spigot().sendMessage(GlobalConfig.NOT_WHITELISTED.component())
                return
            }
            val menuConfiguration = Registries.MENU.get(tile.configuration.menuId).orThrow()
            CollectorMenu(player, null, tile, menuConfiguration).open()
        }
    }

    override fun tick(tile: CollectorTile) {
        if (tile.tickCount == Int.MAX_VALUE) {
            tile.tickCount = 0
        } else {
            tile.tickCount++
        }

        if (tile.tickCount % GlobalConfig.DISPLAY_REFRESH_TIME == 0) {
            tickDisplay(tile)
        }

        tile.upgrades.forEach { (upgrade, status) ->
            if (status.second != 1) return@forEach
            upgrade.action.onTick(tile, upgrade, status.first)
        }
    }

    fun tickDisplay(tile: CollectorTile) {
        if (tile.textDisplayUUID == null) return
        val textDisplay = Bukkit.getEntity(tile.textDisplayUUID!!)
        if (textDisplay !is TextDisplay) {
            return // This occurs when the server is first loading chunks because the entity isn't registered before the tick loop starts
        }

        val multiplier = Registries.UPGRADE[Registries.UPGRADE_ACTION.SELL_MULTIPLIER].map { upgrade ->
            upgrade.mapLevelOrDefault(
                tile.getUpgradeStatus(upgrade).first, 1.0
            ) { level -> (level as SellMultiplierLevel).multiplier }
        }.orElse(1.0)
        textDisplay.text = PineappleChat.parseLegacy(
            tile.configuration.hologram.hologramText.source, mutableMapOf<String, Any>(
                "sell_price" to (DECIMAL_FORMAT.format(tile.stackContainer.getTotalSellPrice() * multiplier) ?: "$0.00")
            )
        )
    }

    fun sellAllContents(tile: CollectorTile, seller: Player?) {
        val profiteer = seller ?: Bukkit.getOfflinePlayer(tile.owner!!)
        var sellPrice =
            if (profiteer.isOnline) tile.stackContainer.getTotalSellPrice(profiteer.player!!) else tile.stackContainer.getTotalSellPrice()
        val action = SellActionEvent(sellPrice, profiteer, tile)
        action.call()
        sellPrice = action.sellPrice

        tile.stackContainer.clearContents()
        tile.tileType.tickDisplay(tile)
        Plugins.economyOrThrow().giveBalance(profiteer, sellPrice)
    }

    fun sellSlot(tile: CollectorTile, slot: Int, seller: Player?, onSell: () -> Unit) {
        val profiteer = seller ?: Bukkit.getOfflinePlayer(tile.owner!!)
        tile.stackContainer.modify(slot) { stack ->
            if (!Plugins.shopOrThrow().canSell(stack.comparator, profiteer.player)) {
                stack.shrink(stack.stackSize)
                return@modify
            }
            val sellItem = Plugins.shopOrThrow().sellItem(stack.comparator, profiteer.player, stack.stackSize.toInt())
            if (sellItem.first) {
                var sellPrice = sellItem.second
                val action = SellActionEvent(sellPrice, profiteer, tile)
                action.call()
                sellPrice = action.sellPrice
                Plugins.economyOrThrow().giveBalance(profiteer, sellPrice)
                stack.shrink(stack.stackSize)
                onSell.invoke()
            }
        }
    }
}
