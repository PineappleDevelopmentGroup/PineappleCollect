package sh.miles.pineapplecollectors.tiles.api

import org.bukkit.NamespacedKey
import sh.miles.pineapple.collection.registry.WriteableRegistry

class TilesRegistry : WriteableRegistry<TileData<*>, NamespacedKey>() {
}