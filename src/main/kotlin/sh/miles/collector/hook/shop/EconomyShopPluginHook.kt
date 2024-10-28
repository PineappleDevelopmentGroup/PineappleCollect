package sh.miles.collector.hook.shop

import me.gypopo.economyshopgui.api.EconomyShopGUIHook
import me.gypopo.economyshopgui.objects.ShopItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import sh.miles.collector.hook.Plugins

internal object EconomyShopPluginHook : ShopSupportedPlugin {

    override val name: String = "EconomyShopGUI"

    override fun load(root: Plugin): Boolean {
        val pluginManager = root.server.pluginManager

        return pluginManager.getPlugin("EconomyShopGUI") != null || pluginManager.getPlugin("EconomyShopGUI-Premium") != null
    }

    override fun getItemPrice(item: ItemStack, player: Player): Double {
        return EconomyShopGUIHook.getItemSellPrice(getShopItem(item, player), item, player)
    }

    override fun getItemPrice(item: ItemStack): Double {
        return EconomyShopGUIHook.getItemSellPrice(getShopItem(item), item)
    }

    override fun canSell(item: ItemStack): Boolean {
        val shopItem = getShopItem(item)
        return shopItem != null && EconomyShopGUIHook.isSellAble(shopItem)
    }

    override fun canSell(item: ItemStack, player: Player?): Boolean {
        if (player == null) return canSell(item)
        val shopItem = getShopItem(item, player)
        return shopItem != null && EconomyShopGUIHook.isSellAble(shopItem)
    }

    override fun sellItem(stack: ItemStack, player: Player?, amount: Int): Pair<Boolean, Double> {
        if (player == null) return sellItem(stack, amount)
        if (!canSell(stack, player)) return Pair(false, 0.0)
        EconomyShopGUIHook.sellItem(getShopItem(stack, player), amount)
        return Pair(true, EconomyShopGUIHook.getItemSellPrice(getShopItem(stack, player), stack, player, amount, 0))
    }

    override fun sellItem(stack: ItemStack, amount: Int): Pair<Boolean, Double> {
        if (!canSell(stack)) return Pair(false, 0.0)
        EconomyShopGUIHook.sellItem(getShopItem(stack), amount)
        return Pair(true, EconomyShopGUIHook.getItemSellPrice(getShopItem(stack), stack, amount, 0))
    }

    override fun getKey(): String {
        return Plugins.SHOP
    }

    private fun getShopItem(stack: ItemStack, player: Player): ShopItem? {
        return EconomyShopGUIHook.getShopItem(player, stack)
    }

    private fun getShopItem(stack: ItemStack): ShopItem? {
        return EconomyShopGUIHook.getShopItem(stack)
    }
}
