package sh.miles.collector.upgrade

import org.bukkit.NamespacedKey
import org.bukkit.event.entity.EntityDeathEvent
import sh.miles.collector.tile.CollectorTile
import sh.miles.collector.tile.action.CollectorGainItemAction
import sh.miles.collector.tile.action.CollectorSellAction
import sh.miles.pineapple.collection.registry.RegistryKey

interface CollectorUpgradeAction : RegistryKey<NamespacedKey> {
    val maxLevel: Int
    val name: String

    fun onEntityDeath(event: EntityDeathEvent, tile: CollectorTile, level: Int) {
    }

    fun onCollectorSell(action: CollectorSellAction, tile: CollectorTile, level: Int) {
    }

    fun onCollectorLoad(tile: CollectorTile, level: Int) {
    }

    fun onCollectorSave(tile: CollectorTile, level: Int) {
    }

    fun onCollectAddItem(action: CollectorGainItemAction, tile: CollectorTile, level: Int) {
    }

    fun onCollectorTick(tile: CollectorTile, level: Int) {
    }
}
