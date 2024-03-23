package sh.miles.collect.util

import net.brcdev.shopgui.ShopGuiPlusApi
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit.getServer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


object PluginHooks {

    private lateinit var economy: Economy

    init {
        setupEconomy()
    }

    fun giveBalance(player: Player, amount: Double) {
        this.economy.depositPlayer(player, amount)
    }

    /**
     * MAKE SURE THIS IS CALLED AFTER ITEMS HAVE BEEN REMOVED
     * IT DOES GIVE THE PLAYER THAT AMOUNT OF ITEMS
     */
    fun sellItem(player: Player, stack: ItemStack, amount: Int) {
        val shopItem = ShopGuiPlusApi.getItemStackShopItem(player, stack)
        val sellPrice = shopItem.getSellPriceForAmount(player, amount)
        giveBalance(player, sellPrice)
    }

    private fun setupEconomy(){
        val rsp = getServer().servicesManager.getRegistration(
            Economy::class.java
        ) ?: return
        economy = rsp.provider
    }
}