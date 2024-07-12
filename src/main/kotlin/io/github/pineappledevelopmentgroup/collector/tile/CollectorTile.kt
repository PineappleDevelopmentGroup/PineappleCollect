package io.github.pineappledevelopmentgroup.collector.tile

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import sh.miles.crown.tiles.Tile

class CollectorTile : Tile {

    override fun save(container: PersistentDataContainer) {

    }

    override fun load(container: PersistentDataContainer): Boolean {
        return true
    }

    override fun delete(container: PersistentDataContainer) {

    }

    override fun getTileTypeKey(): NamespacedKey {
        TODO("Not yet implemented")
    }
}
