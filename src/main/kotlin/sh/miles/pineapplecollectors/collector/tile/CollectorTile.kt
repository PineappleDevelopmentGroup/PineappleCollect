package sh.miles.pineapplecollectors.collector.tile

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import sh.miles.crown.tiles.Tile
import sh.miles.pineapplecollectors.util.TilePDC

class CollectorTile(var metaId: String = "") : Tile {
    var count = 0

    override fun save(pdc: PersistentDataContainer) {
        pdc.set(TilePDC.COUNT_KEY, PersistentDataType.INTEGER, count)
        pdc.set(TilePDC.META_KEY, PersistentDataType.STRING, metaId)
    }

    override fun load(pdc: PersistentDataContainer): Boolean {
        if (!pdc.has(TilePDC.META_KEY)) return false
        val count = pdc.getOrDefault(TilePDC.COUNT_KEY, PersistentDataType.INTEGER, 0)
        this.metaId = pdc.get(TilePDC.META_KEY, PersistentDataType.STRING)!!
        this.count = count
        return true
    }

    override fun delete(pdc: PersistentDataContainer) {
        pdc.remove(TilePDC.COUNT_KEY)
    }

    override fun getTileTypeKey(): NamespacedKey {
        return CollectorTileType.key
    }
}