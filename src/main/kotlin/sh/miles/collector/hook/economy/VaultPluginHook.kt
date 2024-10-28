package sh.miles.collector.hook.economy

import com.google.common.base.Suppliers
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import sh.miles.collector.hook.Plugins
import java.util.function.Supplier

internal object VaultPluginHook : EconomySupportedPlugin {
    override val name: String = "Vault"
    private lateinit var economy: Supplier<Economy>

    override fun load(root: Plugin): Boolean {
        val pluginManager = root.server.pluginManager

        val plugin = pluginManager.getPlugin("Vault") != null
        if (plugin != null) {
            economy = Suppliers.memoize {
                Bukkit.getServicesManager().getRegistration(Economy::class.java)!!.provider
            }
        }
        return plugin
    }

    override fun getBalance(player: Player): Double {
        return economy.get().getBalance(player)
    }

    override fun hasBalance(player: Player, balance: Double): Boolean {
        return getBalance(player) > balance
    }

    override fun removeBalance(player: Player, balance: Double): Boolean {
        return economy.get().withdrawPlayer(player, balance).transactionSuccess()
    }

    override fun giveBalance(player: OfflinePlayer, amount: Double): Boolean {
        return economy.get().depositPlayer(player, amount).transactionSuccess()
    }

    override fun getKey(): String {
        return Plugins.ECONOMY
    }
}
