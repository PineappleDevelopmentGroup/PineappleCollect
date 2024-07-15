package sh.miles.pineapplecollectors.collector.tile

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import sh.miles.crown.tiles.factory.tile.TileFactory

object CollectorTileFactory : TileFactory<CollectorTile> {
    override fun create(): CollectorTile {
        return CollectorTile()
    }

    override fun create(item: ItemStack): CollectorTile {
        val itemPdc = item.itemMeta!!.persistentDataContainer
        val tile = CollectorTile(itemPdc.get(CollectorTileItemFactory.META_KEY, PersistentDataType.STRING)!!)
        tile.count = itemPdc.getOrDefault(CollectorTile.COUNT_KEY, PersistentDataType.INTEGER, 0)
        return tile
    }
}