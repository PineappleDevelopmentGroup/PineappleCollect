package sh.miles.collector.upgrade.action.impl

import org.bukkit.NamespacedKey
import sh.miles.collector.configuration.UpgradeConfiguration
import sh.miles.collector.tile.CollectorTile
import sh.miles.collector.upgrade.action.UpgradeAction
import sh.miles.collector.upgrade.level.AutoSellLevel
import sh.miles.collector.upgrade.level.UpgradeLevel
import sh.miles.pineapple.util.serialization.SerializedObject

internal object AutoSellAction : UpgradeAction {
    val KEY = NamespacedKey.fromString("pineapple-collect:auto_sell")!!

    override val internalName: String = "Auto Sell"

    override fun onTick(tile: CollectorTile, upgrade: UpgradeConfiguration, level: Int) {
        val levelObject = upgrade.level[level - 1] as AutoSellLevel
        if (tile.tickCount % levelObject.ticks != 0) return

        tile.tileType.sellAllContents(tile, null)
    }

    override fun createLevel(parent: SerializedObject, level: Int, price: Double): UpgradeLevel {
        return AutoSellLevel(level, price, parent.getPrimitive("cooldown").orThrow().asInt)
    }

    override fun zeroLevel(): UpgradeLevel {
        return AutoSellLevel.ZERO
    }

    override fun getKey(): String {
        return KEY.key
    }

}
