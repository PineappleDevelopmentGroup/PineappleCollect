package sh.miles.pineapplecollectors.collector.tile

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import sh.miles.crown.tiles.factory.item.TileItemFactory
import sh.miles.pineapplecollectors.test.TestTileType.KEY

object CollectorTileItemFactory : TileItemFactory<CollectorTile> {

    override fun create(): ItemStack {
        return create("some-default-key or throw")
    }

    fun create(metaKey: String): ItemStack {
        TODO("Not yet implemented")
    }

    override fun isFactoryResultant(item: ItemStack): Boolean {
        val meta = item.itemMeta ?: return false
        if (!meta.persistentDataContainer.has(TileItemFactory.TILE_ITEM_KEY)) return false
        val key = meta.persistentDataContainer.get(TileItemFactory.TILE_ITEM_KEY, PersistentDataType.STRING)
        return key.equals(KEY.toString())
    }

    override fun create(tile: CollectorTile): ItemStack {
        TODO("Not yet implemented")
    }
}