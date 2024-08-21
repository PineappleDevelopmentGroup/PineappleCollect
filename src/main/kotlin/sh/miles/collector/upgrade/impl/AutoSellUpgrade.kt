package sh.miles.collector.upgrade.impl

import org.bukkit.NamespacedKey
import org.bukkit.event.entity.EntityDeathEvent
import sh.miles.collector.tile.action.CollectorLoadAction
import sh.miles.collector.tile.action.CollectorSaveAction
import sh.miles.collector.tile.action.CollectorSellAction
import sh.miles.collector.upgrade.CollectorUpgrade

object AutoSellUpgrade : CollectorUpgrade {
    override val maxLevel: Int
        get() = 1

    override fun onEntityDeath(event: EntityDeathEvent, level: Int) {
        TODO("Not yet implemented")
    }

    override fun onCollectorSell(event: CollectorSellAction, level: Int) {
        TODO("Not yet implemented")
    }

    override fun onCollectorLoad(event: CollectorLoadAction, level: Int) {
        TODO("Not yet implemented")
    }

    override fun onCollectorSave(event: CollectorSaveAction, level: Int) {
        TODO("Not yet implemented")
    }

    override fun getKey(): NamespacedKey {
        return NamespacedKey.fromString("pineapple-collect:upgrade_autosell")!!
    }
}