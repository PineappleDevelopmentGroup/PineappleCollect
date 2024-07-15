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

        return CollectorTile(itemPdc.get(CollectorTileItemFactory.META_KEY, PersistentDataType.STRING)!!)
    }
}