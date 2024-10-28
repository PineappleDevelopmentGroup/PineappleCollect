package sh.miles.collector.hook.shop

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import sh.miles.collector.hook.SupportedPlugin

interface ShopSupportedPlugin : SupportedPlugin {
    fun getItemPrice(item: ItemStack, player: Player): Double
    fun getItemPrice(item: ItemStack): Double
    fun canSell(item: ItemStack): Boolean
    fun canSell(item: ItemStack, player: Player?): Boolean
    fun sellItem(stack: ItemStack, player: Player?, amount: Int): Pair<Boolean, Double>
    fun sellItem(stack: ItemStack, amount: Int): Pair<Boolean, Double>
}
