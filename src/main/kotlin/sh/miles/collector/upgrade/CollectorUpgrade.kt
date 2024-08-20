package sh.miles.collector.upgrade

import org.bukkit.NamespacedKey
import org.bukkit.event.entity.EntityDeathEvent
import sh.miles.collector.tile.action.CollectorLoadAction
import sh.miles.collector.tile.action.CollectorSaveAction
import sh.miles.collector.tile.action.CollectorSellAction
import sh.miles.pineapple.collection.registry.RegistryKey

interface CollectorUpgrade : RegistryKey<NamespacedKey> {
    val maxLevel: Int

    fun onEntityDeath(event: EntityDeathEvent, level: Int)
    fun onCollectorSell(event: CollectorSellAction, level: Int)
    fun onCollectorLoad(event: CollectorLoadAction, level: Int)
    fun onCollectorSave(event: CollectorSaveAction, level: Int)
}
