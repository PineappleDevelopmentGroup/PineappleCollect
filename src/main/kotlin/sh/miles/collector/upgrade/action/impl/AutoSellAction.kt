package sh.miles.collector.upgrade.action.impl

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import sh.miles.collector.configuration.UpgradeConfiguration
import sh.miles.collector.hook.VaultHook
import sh.miles.collector.tile.CollectorTile
import sh.miles.collector.upgrade.action.UpgradeAction
import sh.miles.collector.upgrade.action.UpgradeActionRegistry
import sh.miles.collector.upgrade.level.AutoSellLevel
import sh.miles.collector.upgrade.level.UpgradeLevel
import sh.miles.pineapple.util.serialization.SerializedObject

internal object AutoSellAction : UpgradeAction {
    val KEY = NamespacedKey.fromString("pineapple-collect:auto_sell")!!

    override val internalName: String = "Auto Sell"

    override fun onTick(tile: CollectorTile, upgrade: UpgradeConfiguration, level: Int) {
        val levelObject = upgrade.level[level - 1] as AutoSellLevel
        if (tile.tickCount % levelObject.ticks != 0) return

        val player = Bukkit.getOfflinePlayer(tile.owner!!)
        val sellPrice =
            if (player.isOnline) tile.stackContainer.getTotalSellPrice(player.player!!) else tile.stackContainer.getTotalSellPrice()
        tile.stackContainer.clearContents()
        tile.tileType.tickDisplay(tile)
        VaultHook.giveBalance(player, sellPrice)
    }

    override fun createLevel(parent: SerializedObject, level: Int, price: Int): UpgradeLevel {
        return AutoSellLevel(level, price, parent.getPrimitive("cooldown").orThrow().asInt)
    }

    override fun getKey(): String {
        return KEY.key
    }

}
