package sh.miles.pineapplecollectors.hook

import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit.getServer
import org.bukkit.entity.Player

object VaultHook {

    private val economy: Economy = getServer().servicesManager.getRegistration(Economy::class.java)?.provider ?: throw IllegalStateException("Unable to find economy plugin")

    fun getBalance(player: Player): Double {
        return economy.getBalance(player)
    }

    fun addBalance(player: Player, amount: Double): EconomyResponse {
        return economy.depositPlayer(player, amount)
    }

    fun hasBalance(player: Player, amount: Double): Boolean {
        return getBalance(player) >= amount
    }
}