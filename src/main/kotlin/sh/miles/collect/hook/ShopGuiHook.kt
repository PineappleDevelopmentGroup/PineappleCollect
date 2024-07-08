package sh.miles.collect.hook

import net.brcdev.shopgui.ShopGuiPlusApi
import net.brcdev.shopgui.shop.item.ShopItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ShopGuiHook {

    // Interact with ShopGuiPlusApi (static class)

    fun getShopItem(player: Player, item: ItemStack): ShopItem {
        return ShopGuiPlusApi.getItemStackShopItem(player, item)
    }

    fun getItemSalePrice(player: Player, item: ItemStack): Double {
        return getShopItem(player, item).sellPrice
    }

    // TODO method to get price for an amount, will incluse some form of recursion and reutrn BigNumber as infstack is long so cost can be bigger
}