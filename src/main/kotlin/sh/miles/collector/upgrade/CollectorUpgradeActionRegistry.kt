package sh.miles.collector.upgrade

import org.bukkit.NamespacedKey
import sh.miles.pineapple.collection.registry.WriteableRegistry

object CollectorUpgradeActionRegistry : WriteableRegistry<CollectorUpgradeAction, NamespacedKey>() {

    val AUTO_SELL = NamespacedKey.fromString("pineapple-collect:upgrade_autosell")!!

    init {
    }


}
