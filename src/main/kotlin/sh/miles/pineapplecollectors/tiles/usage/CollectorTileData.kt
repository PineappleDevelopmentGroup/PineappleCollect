package sh.miles.pineapplecollectors.tiles.usage

import org.bukkit.NamespacedKey
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import sh.miles.pineapplecollectors.tiles.api.TileData

object CollectorTileData : TileData<CollectorTile> {
    override fun onInteract(event: PlayerInteractEvent, tile: CollectorTile) {
        TODO("Not yet implemented")
    }

    override fun onBlockBreak(event: BlockBreakEvent, tile: CollectorTile) {
        TODO("Not yet implemented")
    }

    override fun onBlockPlace(event: BlockPlaceEvent, tile: CollectorTile) {
        TODO("Not yet implemented")
    }

    override fun onBlockDrop(event: BlockDropItemEvent, tile: CollectorTile) {
        TODO("Not yet implemented")
    }

    override fun createItem(): ItemStack {
        TODO("Not yet implemented")
    }

    override fun fromItem(): CollectorTile {
        TODO("Not yet implemented")
    }

    override fun getKey(): NamespacedKey {
        TODO("Not yet implemented")
    }
}