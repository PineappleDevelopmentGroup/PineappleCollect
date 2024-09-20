package sh.miles.collector.upgrade.action.impl

import org.bukkit.NamespacedKey
import sh.miles.collector.configuration.UpgradeConfiguration
import sh.miles.collector.tile.event.SellActionEvent
import sh.miles.collector.upgrade.action.UpgradeAction
import sh.miles.collector.upgrade.level.SellMultiplierLevel
import sh.miles.collector.upgrade.level.UpgradeLevel
import sh.miles.pineapple.util.serialization.SerializedObject

object SellMultiplierAction : UpgradeAction {
    val KEY = NamespacedKey.fromString("pineapple-collect:sell_multiplier")!!

    override val internalName: String = "Sell Multiplier"

    override fun onSell(event: SellActionEvent, upgrade: UpgradeConfiguration, level: Int) {
        val levelObject = upgrade.level[level - 1] as SellMultiplierLevel
        event.sellPrice *= levelObject.multiplier
    }

    override fun createLevel(parent: SerializedObject, level: Int, price: Double): UpgradeLevel {
        return SellMultiplierLevel(level, price, parent.getPrimitive("multiplier").orThrow().asDouble)
    }

    override fun zeroLevel(): UpgradeLevel {
        return SellMultiplierLevel.ZERO
    }

    override fun getKey(): String {
        return KEY.key
    }
}
