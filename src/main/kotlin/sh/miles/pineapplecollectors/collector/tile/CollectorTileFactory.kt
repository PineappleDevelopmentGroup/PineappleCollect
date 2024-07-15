package sh.miles.pineapplecollectors.collector.tile

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import sh.miles.crown.tiles.factory.tile.TileFactory
import sh.miles.pineapplecollectors.util.TilePDC

object CollectorTileFactory : TileFactory<CollectorTile> {
    override fun create(): CollectorTile {
        return CollectorTile()
    }

    override fun create(item: ItemStack): CollectorTile {
        val itemPdc = item.itemMeta!!.persistentDataContainer
        val tile = CollectorTile(itemPdc.get(TilePDC.META_KEY, PersistentDataType.STRING)!!)
        tile.count = itemPdc.getOrDefault(TilePDC.COUNT_KEY, PersistentDataType.INTEGER, 0)
        return tile
    }
}