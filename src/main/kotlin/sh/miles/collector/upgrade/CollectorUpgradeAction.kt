package sh.miles.collector.upgrade

import org.bukkit.NamespacedKey
import org.bukkit.event.entity.EntityDeathEvent
import sh.miles.collector.tile.CollectorTile
import sh.miles.collector.tile.action.CollectorLoadAction
import sh.miles.collector.tile.action.CollectorSaveAction
import sh.miles.collector.tile.action.CollectorSellAction
import sh.miles.pineapple.collection.registry.RegistryKey

interface CollectorUpgradeAction : RegistryKey<NamespacedKey> {
    val maxLevel: Int

    fun onEntityDeath(event: EntityDeathEvent, tile: CollectorTile, level: Int) {
    }

    fun onCollectorSell(event: CollectorSellAction, tile: CollectorTile, level: Int) {
    }

    fun onCollectorLoad(event: CollectorLoadAction, tile: CollectorTile, level: Int) {
    }

    fun onCollectorSave(event: CollectorSaveAction, tile: CollectorTile, level: Int) {
    }
}
