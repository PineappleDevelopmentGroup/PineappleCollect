package sh.miles.pineapplecollectors.tiles.api

import org.bukkit.persistence.PersistentDataContainer

interface Tile {

    fun save(container: PersistentDataContainer)

    fun load(container: PersistentDataContainer)

    fun delete(container: PersistentDataContainer)

}