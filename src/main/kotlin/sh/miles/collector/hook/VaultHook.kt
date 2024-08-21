package sh.miles.collector.hook

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object VaultHook {

    private val economy = Bukkit.getServicesManager().getRegistration(Economy::class.java)!!.provider

    fun getBalannce(player: Player) : Double {
        return economy.getBalance(player)
    }

    fun hasBalance(player: Player, balance: Double) : Boolean {
        return getBalannce(player) > balance
    }

    fun removeBalance(player: Player, balance: Double) : Boolean {
        return economy.withdrawPlayer(player, balance).transactionSuccess()
    }

    fun giveBalance(player: Player, amount: Double) : Boolean {
        return economy.depositPlayer(player, amount).transactionSuccess()
    }

}