package sh.miles.collect.util

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit.getServer
import org.bukkit.entity.Player


object PluginHooks {

    private lateinit var economy: Economy

    init {
        setupEconomy()
    }

    fun giveBalance(player: Player, amount: Double) {
        this.economy.depositPlayer(player, amount)
    }

    private fun setupEconomy(){
        val rsp = getServer().servicesManager.getRegistration(
            Economy::class.java
        ) ?: return
        economy = rsp.provider
    }
}