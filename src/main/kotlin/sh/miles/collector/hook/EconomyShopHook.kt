package sh.miles.collector.hook

import me.gypopo.economyshopgui.api.EconomyShopGUIHook
import me.gypopo.economyshopgui.objects.ShopItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object EconomyShopHook {

    fun getItemPrice(stack: ItemStack, player: Player): Double {
        return EconomyShopGUIHook.getItemSellPrice(getShopItem(stack, player), stack, player)
    }

    fun getItemPrice(stack: ItemStack): Double {
        return EconomyShopGUIHook.getItemSellPrice(getShopItem(stack), stack)
    }

    fun getShopItem(stack: ItemStack, player: Player): ShopItem? {
        return EconomyShopGUIHook.getShopItem(player, stack)
    }

    fun getShopItem(stack: ItemStack): ShopItem? {
        return EconomyShopGUIHook.getShopItem(stack)
    }

    fun canSell(stack: ItemStack, player: Player): Boolean {
        val item = getShopItem(stack, player)
        return item != null && EconomyShopGUIHook.isSellAble(item)
    }

    fun canSell(stack: ItemStack): Boolean {
        val item = getShopItem(stack)
        return item != null && EconomyShopGUIHook.isSellAble(item)
    }

    /**
     * @return -1 if it cant be so ld, 0 if no set limit
     */
    fun maxSell(stack: ItemStack, player: Player): Int {
        if (!canSell(stack, player)) return -1

        return getShopItem(stack, player)!!.maxSell
    }

    /**
     * @return false 0 if unable to sell true and amount to give the player
     */
    fun sellItem(stack: ItemStack, player: Player, amount: Int): Pair<Boolean, Double> {
        if (!canSell(stack, player)) return Pair(false, 0.0)
        EconomyShopGUIHook.sellItem(getShopItem(stack, player), amount)
        return Pair(true, EconomyShopGUIHook.getItemSellPrice(getShopItem(stack, player), stack, player, amount, 0))
    }
}
