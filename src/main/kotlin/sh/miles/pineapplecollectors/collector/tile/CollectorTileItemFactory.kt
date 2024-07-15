package sh.miles.pineapplecollectors.collector.tile

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import sh.miles.crown.tiles.factory.item.TileItemFactory
import sh.miles.pineapple.function.Option
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some
import sh.miles.pineapple.function.Option.none
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapplecollectors.registry.CollectorMetaRegistry
import sh.miles.pineapplecollectors.util.TilePDC

object CollectorTileItemFactory : TileItemFactory<CollectorTile> {

    override fun create(): ItemStack {
        throw IllegalAccessException("create() without meta is disallowed")
    }

    fun create(metaKey: String): Option<ItemStack> {
        val retrievedMeta = CollectorMetaRegistry.get(metaKey)
        if (retrievedMeta is None) return none()

        val some = (retrievedMeta as Some).some()

        val modifiedItem = ItemBuilder.modifyStack(some.item)
            .persistentData(TileItemFactory.TILE_ITEM_KEY, PersistentDataType.STRING, CollectorTileType.key.toString())
            .persistentData(TilePDC.META_KEY, PersistentDataType.STRING, metaKey)
            .build()

        return Option.some(modifiedItem)
    }

    override fun isFactoryResultant(item: ItemStack): Boolean {
        val meta = item.itemMeta ?: return false
        if (!meta.persistentDataContainer.has(TileItemFactory.TILE_ITEM_KEY)) return false
        val key = meta.persistentDataContainer.get(TileItemFactory.TILE_ITEM_KEY, PersistentDataType.STRING)
        return key.equals(CollectorTileType.key.toString())
    }

    // Could use the output of create(metaKey) but would have to clone item for builder again
    override fun create(tile: CollectorTile): ItemStack {
        val retrievedMeta = CollectorMetaRegistry.get(tile.metaId)
        if (retrievedMeta is None) return ItemStack(Material.AIR)

        val some = (retrievedMeta as Some).some()

        val modifiedItem = ItemBuilder.modifyStack(some.item)
            .persistentData(TileItemFactory.TILE_ITEM_KEY, PersistentDataType.STRING, CollectorTileType.key.toString())
            .persistentData(TilePDC.META_KEY, PersistentDataType.STRING, some.key)
            .persistentData(TilePDC.COUNT_KEY, PersistentDataType.INTEGER, tile.count)
            .build()


        return modifiedItem
    }
}