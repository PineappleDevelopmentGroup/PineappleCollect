package sh.miles.collector.tile.event

import org.bukkit.OfflinePlayer
import sh.miles.collector.tile.CollectorTile

data class SellActionEvent(var sellPrice: Double, val seller: OfflinePlayer, val tile: CollectorTile) {
    fun call() {
        for (upgrade in tile.upgrades) {
            upgrade.key.action.onSell(this, upgrade.key, tile.getUpgradeStatus(upgrade.key).first)
        }
    }
}
