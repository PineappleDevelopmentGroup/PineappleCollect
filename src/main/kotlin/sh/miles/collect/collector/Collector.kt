package sh.miles.collect.collector

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.collection.NonNullList

class Collector(val templateKey: String, size: Int) {

    private val contents = NonNullList { ItemStack(Material.AIR) }

    fun getItemAt(index: Int): ItemStack {
        return contents[index]
    }

    fun removeItemAt(index: Int): ItemStack {
        return contents.removeAt(index)
    }

    fun getContentsCopy(): List<ItemStack> {
        return contents.stream().toList()
    }

    companion object {

    }

}
