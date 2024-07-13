package sh.miles.pineapplecollectors.collector.tile

import org.bukkit.NamespacedKey
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import sh.miles.crown.tiles.Tile
import sh.miles.crown.tiles.TileType
import sh.miles.crown.tiles.factory.item.TileItemFactory
import sh.miles.crown.tiles.factory.tile.TileFactory

object CollectorTileType : TileType<Tile> {

    override fun getKey(): NamespacedKey {
        return NamespacedKey.fromString("pineapplecollectors:collector")!!
    }

    override fun onInteract(event: PlayerInteractEvent, tile: Tile) {
        TODO("Not yet implemented")
    }

    override fun onBlockBreak(event: BlockBreakEvent, tile: Tile) {
        TODO("Not yet implemented")
    }

    override fun onBlockPlace(event: BlockPlaceEvent, tile: Tile) {
        TODO("Not yet implemented")
    }

    override fun onBlockDrop(event: BlockDropItemEvent, tile: Tile) {
        TODO("Not yet implemented")
    }

    override fun getTileFactory(): TileFactory<Tile> {
        TODO("Not yet implemented")
    }

    override fun getTileItemFactory(): TileItemFactory<Tile> {
        TODO("Not yet implemented")
    }
}