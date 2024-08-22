package sh.miles.collector.tile

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Display
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import sh.miles.collector.Registries
import sh.miles.collector.configuration.CollectorConfiguration
import sh.miles.collector.menu.CollectorMainMenu
import sh.miles.collector.menu.CollectorSellMenu
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapple.tiles.api.TileType
import sh.miles.pineapple.tiles.internal.util.TileKeys

object CollectorTileType : TileType<CollectorTile>(false) {

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
            COLLECTOR_CONFIGURATION,
            PersistentDataType.STRING,
            configuration.id
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

        tile.textDisplayUUID = tile.configuration.hologram.spawn(location) {
            it.billboard = Display.Billboard.CENTER
        }.uniqueId
        tile.location = location

        tile.configuration.placeSound.playSound(player.location)
    }

    override fun onBreak(event: BlockBreakEvent, tile: CollectorTile) {
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
                // TODO send proper no access message
                player.sendMessage("No access")
                return
            }
            val menuConfiguration = Registries.MAIN_MENU.get(tile.configuration.menuId).orThrow()
            CollectorMainMenu(player, tile, menuConfiguration).open()
        }
    }
}
