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

object CollectorTileType : TileType<CollectorTile> {

    private val key = NamespacedKey.fromString("pineapplecollectors:type")!!

    override fun getKey(): NamespacedKey {
        return key
    }

    override fun onInteract(event: PlayerInteractEvent, tile: Tile) {
        println("onInteract")
        val collectorTile = tile as CollectorTile
        collectorTile.count += 1
        event.player.sendMessage("You Clicked a Collector!!")
        event.player.sendMessage("You've clicked this tile ${collectorTile.count} times")
    }

    override fun onBlockBreak(event: BlockBreakEvent, tile: Tile) {
        println("onBlockBreak")

    }

    override fun onBlockPlace(event: BlockPlaceEvent, tile: Tile) {
        println("onBlockPlace")
    }

    override fun onBlockDrop(event: BlockDropItemEvent, tile: Tile) {
        println("onBlockDrop")
        val itemEntity = event.items[0]
        itemEntity.itemStack = tileItemFactory.create(tile as CollectorTile)
    }

    override fun getTileFactory(): TileFactory<CollectorTile> {
        return CollectorTileFactory
    }

    override fun getTileItemFactory(): TileItemFactory<CollectorTile> {
        return CollectorTileItemFactory
    }
}