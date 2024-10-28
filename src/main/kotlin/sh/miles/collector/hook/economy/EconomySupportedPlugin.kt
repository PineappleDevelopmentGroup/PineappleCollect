package sh.miles.collector.hook.economy

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import sh.miles.collector.hook.SupportedPlugin

interface EconomySupportedPlugin : SupportedPlugin {
    fun getBalance(player: Player): Double
    fun hasBalance(player: Player, balance: Double): Boolean
    fun removeBalance(player: Player, balance: Double): Boolean
    fun giveBalance(player: OfflinePlayer, amount: Double): Boolean
}
