package sh.miles.collect.collector.inventory

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.collection.NonNullList

class CollectorInventory(val size: Int) {

    private val contents = NonNullList { ItemStack(Material.AIR) }

    fun getItemAt(index: Int): ItemStack {
        verifyIndex(index)
        return contents[index]
    }

    fun removeItemAt(index: Int): ItemStack {
        verifyIndex(index)
        return contents.removeAt(index)
    }

    fun getItems(): List<ItemStack> {
        return contents.stream().toList()
    }

    fun addContentsList(toAdd: List<ItemStack>) {
        this.contents.addAll(toAdd)
    }

    private fun verifyIndex(index: Int) {
        if (index >= size || index < 0) {
            throw IndexOutOfBoundsException("$index is not in bounds of inventory of size $size")
        }
    }

}
