package sh.miles.pineapplecollectors.collector.tile

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import sh.miles.crown.tiles.Tile

class CollectorTile(var metaId: String = "") : Tile {

    companion object {
        val COUNT_KEY = NamespacedKey.fromString("pineapplecollectors:count")!!
    }

    var count = 0

    override fun save(pdc: PersistentDataContainer) {
        pdc.set(COUNT_KEY, PersistentDataType.INTEGER, count)
        pdc.set(CollectorTileItemFactory.META_KEY, PersistentDataType.STRING, metaId)
    }

    override fun load(pdc: PersistentDataContainer): Boolean {
        if (!pdc.has(CollectorTileItemFactory.META_KEY)) return false
        val count = pdc.getOrDefault(COUNT_KEY, PersistentDataType.INTEGER, 0)
        this.metaId = pdc.get(CollectorTileItemFactory.META_KEY, PersistentDataType.STRING)!!
        this.count = count
        return true
    }

    override fun delete(pdc: PersistentDataContainer) {
        pdc.remove(COUNT_KEY)
    }

    override fun getTileTypeKey(): NamespacedKey {
        return CollectorTileType.key
    }
}