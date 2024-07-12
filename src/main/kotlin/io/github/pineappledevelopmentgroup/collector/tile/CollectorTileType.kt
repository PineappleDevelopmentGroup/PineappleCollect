package io.github.pineappledevelopmentgroup.collector.tile

import org.bukkit.NamespacedKey
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import sh.miles.crown.tiles.Tile
import sh.miles.crown.tiles.TileType
import sh.miles.crown.tiles.factory.item.TileItemFactory
import sh.miles.crown.tiles.factory.tile.TileFactory

class CollectorTileType(private val tileKey: NamespacedKey) : TileType<CollectorTile> {

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

    override fun getTileFactory(): TileFactory<CollectorTile> {
        TODO("Not yet implemented")
    }

    override fun getTileItemFactory(): TileItemFactory<CollectorTile> {
        TODO("Not yet implemented")
    }

    override fun getKey(): NamespacedKey {
        return tileKey
    }
}
