package sh.miles.collector.hook

import me.gypopo.economyshopgui.api.EconomyShopGUIHook
import me.gypopo.economyshopgui.objects.ShopItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object EconomyShopHook {

    fun getItemPrice(stack: ItemStack, player: Player) : Double {
        return EconomyShopGUIHook.getItemSellPrice(getShopItem(stack, player), stack, player)
    }

    fun getShopItem(stack: ItemStack, player: Player) : ShopItem? {
        return EconomyShopGUIHook.getShopItem(player, stack)!!
    }

    fun canSell(stack: ItemStack, player: Player) : Boolean {
        return getShopItem(stack, player) != null
    }
}