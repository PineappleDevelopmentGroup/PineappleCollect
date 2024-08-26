package sh.miles.collector.upgrade

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.SoundCategory
import sh.miles.collector.GlobalConfig
import sh.miles.collector.hook.EconomyShopHook
import sh.miles.collector.hook.VaultHook
import sh.miles.collector.tile.CollectorTile
import sh.miles.pineapple.collection.registry.WriteableRegistry

object CollectorUpgradeActionRegistry : WriteableRegistry<CollectorUpgradeAction, NamespacedKey>() {

    val AUTO_SELL = NamespacedKey.fromString("pineapple-collect:upgrade_autosell")!!

    init {
        register(AutoSellUpgrade)
    }

    private object AutoSellUpgrade : CollectorUpgradeAction {
        override val maxLevel: Int
            get() = 1
        override val name: String
            get() = "Auto Sell"

        override fun getKey(): NamespacedKey {
            return AUTO_SELL
        }

        override fun onCollectorTick(tile: CollectorTile, level: Int) {
            if (tile.tickCount % GlobalConfig.AUTO_SELL_COOLDOWN == 0) {

                val player = Bukkit.getOfflinePlayer(tile.owner!!)

                val sellPrice = tile.stackContainer.getTotalSellPrice()
                tile.stackContainer.clearContents()
                tile.tileType.tickDisplay(tile)
                VaultHook.giveBalance(player, sellPrice)
            }
        }

        override fun toString(): String {
            return name
        }
    }
}
