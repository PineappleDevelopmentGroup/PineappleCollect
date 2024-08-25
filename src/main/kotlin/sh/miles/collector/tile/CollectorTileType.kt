package sh.miles.collector.tile

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Display
import org.bukkit.entity.TextDisplay
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
import sh.miles.collector.hook.EconomyShopHook
import sh.miles.collector.menu.CollectorMenu
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapple.tiles.api.TileType
import sh.miles.pineapple.tiles.api.Tiles
import sh.miles.pineapple.tiles.internal.util.TileKeys
import java.text.DecimalFormat
import java.util.Locale

object CollectorTileType : TileType<CollectorTile>(true) {

    private val DECIMAL_FORMAT = DecimalFormat.getCurrencyInstance(Locale.US);
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
            .persistentData(TileKeys.TILE_TYPE_KEY, PersistentDataType.STRING, key.toString())
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
            location, mutableMapOf(
                "sell_price" to "$0.00"
            )
        ) {
            it.billboard = Display.Billboard.CENTER
            it.isShadowed = false
        }.uniqueId
        tile.location = location

        tile.configuration.placeSound.playSound(player.location)
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
        tile.configuration.breakSound.playSound(event.block.location)
    }

    override fun onInteract(event: PlayerInteractEvent, tile: CollectorTile) {
        if (event.hand == EquipmentSlot.OFF_HAND) return
        val player = event.player

        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            if (tile.owner != player.uniqueId && !tile.accessWhitelist.contains(player.uniqueId)) {
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

        tile.upgrades.forEach { (upgrade, level) ->
            upgrade.onCollectorTick(tile, level)
        }
    }

    private fun tickDisplay(tile: CollectorTile) {
        if (tile.textDisplayUUID == null) return
        val textDisplay = Bukkit.getEntity(tile.textDisplayUUID!!)
        if (textDisplay !is TextDisplay) {
            return // This occurs when the server is first loading chunks because the entity isn't registered before the tick loop starts
        }

        textDisplay.text = PineappleChat.parseLegacy(
            tile.configuration.hologram.hologramText.source, mutableMapOf<String, Any>(
                "sell_price" to (DECIMAL_FORMAT.format(tile.stackContainer.getTotalSellPrice()) ?: "$0.00")
            )
        )
    }
}
