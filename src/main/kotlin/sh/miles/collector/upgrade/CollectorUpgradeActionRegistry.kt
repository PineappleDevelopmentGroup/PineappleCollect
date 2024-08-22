package sh.miles.collector.upgrade

import org.bukkit.NamespacedKey
import org.bukkit.event.entity.EntityDeathEvent
import sh.miles.collector.tile.CollectorTile
import sh.miles.pineapple.collection.registry.WriteableRegistry

object CollectorUpgradeActionRegistry : WriteableRegistry<CollectorUpgradeAction, NamespacedKey>() {

    val AUTO_SELL = NamespacedKey.fromString("pineapple-collect:upgrade_autosell")!!

    init {
        register(AutoSellUpgrade)
    }

    private object AutoSellUpgrade : CollectorUpgradeAction {
        override val maxLevel: Int = 1

        override fun onEntityDeath(event: EntityDeathEvent, tile: CollectorTile, level: Int) {
            TODO("Implement AutoSellAction#onEntityDeath")
        }

        override fun getKey(): NamespacedKey {
            return AUTO_SELL
        }

    }
}
