package sh.miles.collect.util

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI
import net.brcdev.shopgui.ShopGuiPlusApi
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit.getServer
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal
import java.util.UUID


object PluginHooks {

    private lateinit var economy: Economy

    init {
        setupEconomy()
    }

    fun isSameIslandAsOwner(owner: UUID, player: Player): Boolean {
        val splayer = SuperiorSkyblockAPI.getPlayer(player)
        return splayer.hasIsland() && splayer.island.owner.uniqueId.equals(owner)
    }

    fun giveBalance(player: Player, amount: Double) {
        this.economy.depositPlayer(player, amount)
    }

    fun canAfford(player: Player, amount: Double): Boolean {
        return this.economy.has(player, amount)
    }

    fun removeBalance(player: Player, amount: Double) {
        this.economy.withdrawPlayer(player, amount)
    }

    /**
     * MAKE SURE THIS IS CALLED AFTER ITEMS HAVE BEEN REMOVED
     * IT DOES GIVE THE PLAYER THE MONEY VALUE OF ITEMS
     */
    fun sellItem(player: Player, stack: ItemStack, amount: Long): BigDecimal {
        var totalSold = BigDecimal.ZERO
        var toChange = amount
        while (toChange > Int.MAX_VALUE) {
            totalSold = totalSold.add(sellItem(player, stack, Int.MAX_VALUE, true))
            toChange -= Int.MAX_VALUE
        }
        totalSold = totalSold.add(sellItem(player, stack, toChange.toInt(), true))

        return totalSold
    }

    fun priceItem(player: Player, stack: ItemStack, amount: Long): BigDecimal {
        var totalSold = BigDecimal.ZERO
        var toChange = amount
        while (toChange > Int.MAX_VALUE) {
            totalSold = totalSold.add(sellItem(player, stack, Int.MAX_VALUE, false))
            toChange -= Int.MAX_VALUE
        }
        totalSold = totalSold.add(sellItem(player, stack, toChange.toInt(), false))

        return totalSold
    }

    fun isSellable(stack: ItemStack): Boolean {
        return ShopGuiPlusApi.getItemStackShopItem(stack) != null
    }

    fun canPlace(player: Player, location: Location): Boolean {
        val island = SuperiorSkyblockAPI.getIslandAt(location)
        val superiorPlayer = SuperiorSkyblockAPI.getPlayer(player)

        if (island.owner.uniqueId.equals(superiorPlayer.uniqueId)) return true

        if (island.isMember(superiorPlayer)) return true

        if (superiorPlayer.hasBypassModeEnabled()) return true

        return false
    }

    private fun sellItem(player: Player, stack: ItemStack, amount: Int, giveBalance: Boolean): BigDecimal {
        val shopItem = ShopGuiPlusApi.getItemStackShopItem(player, stack) ?: return BigDecimal.ZERO
        val sellPrice = shopItem.getSellPriceForAmount(player, amount)
        if (giveBalance) {
            giveBalance(player, sellPrice)
        }
        return BigDecimal.valueOf(sellPrice)
    }

    private fun setupEconomy() {
        val rsp = getServer().servicesManager.getRegistration(
            Economy::class.java
        ) ?: return
        economy = rsp.provider
    }
}
