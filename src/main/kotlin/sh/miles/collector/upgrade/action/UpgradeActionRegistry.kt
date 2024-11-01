package sh.miles.collector.upgrade.action

import org.bukkit.NamespacedKey
import sh.miles.collector.upgrade.action.impl.AutoSellAction
import sh.miles.collector.upgrade.action.impl.SellMultiplierAction
import sh.miles.pineapple.collection.registry.FrozenRegistry

object UpgradeActionRegistry : FrozenRegistry<UpgradeAction, String>({
    listOf(
        AutoSellAction,
        SellMultiplierAction
    ).associateBy { it.key }
}) {
    val AUTO_SELL = AutoSellAction.KEY
    val SELL_MULTIPLIER = SellMultiplierAction.KEY
}
