package sh.miles.collect.util.item

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import sh.miles.collect.util.PDC_SIZE_KEY
import java.lang.IllegalStateException

/**
 * A Wrapper for an ItemStack which manages stacking infinitely
 */
class InfStack {

    companion object {
        val AMOUNT_KEY = NamespacedKey.fromString("collect:stack_size")!!
    }

    private val source: ItemStack
    private val comparator: ItemStack
    private var size: Long = 0

    constructor(top: ItemStack) {
        if (top.type.isAir) {
            this.source = ItemStack(Material.AIR)
            this.comparator = ItemStack(Material.AIR)
            this.size = 0
        } else {
            this.comparator = setupComparator(top.clone())
            val insert = top.clone()
            val meta = top.itemMeta!!
            if (meta.persistentDataContainer.has(AMOUNT_KEY)) {
                this.source = setupFromInfStack(insert)
            } else {
                this.source = setupFromNonInfStack(insert)
            }
            setChanged()
        }
    }

    constructor() {
        this.source = ItemStack(Material.AIR)
        this.comparator = ItemStack(Material.AIR)
        this.size = 0
    }

    fun source(): ItemStack {
        return this.source.clone()
    }

    fun grow(itemStack: ItemStack): Boolean {
        if (comparator.type == Material.AIR) return false
        if (this.comparator.isSimilar(itemStack)) {
            this.size += itemStack.amount
            setChanged()
            return true
        }
        return false
    }

    fun extract(amount: Int): ItemStack {
        if (comparator.type == Material.AIR) return ItemStack(Material.AIR)

        if (amount > 64) {
            throw IllegalStateException("Can not shrink by more than 64 at one time!")
        }

        val endStackSize: Int
        if (this.size > 64) {
            this.size -= 64
            endStackSize = 64
        } else {
            endStackSize = this.size.toInt()
            this.size = 0
        }

        val result = source.clone()
        result.amount = endStackSize
        setChanged()
        return result
    }

    fun isEmpty(): Boolean {
        return this.size == 0L
    }

    fun size(): Long {
        return this.size
    }

    fun isSimilar(itemStack: ItemStack): Boolean {
        return this.comparator.isSimilar(itemStack)
    }

    private fun setChanged() {
        val meta = this.source.itemMeta!!
        meta.persistentDataContainer.set(PDC_SIZE_KEY, PersistentDataType.LONG, size)
        meta.lore = listOf("Amount: $size")
        source.itemMeta = meta
    }

    private fun setupComparator(insert: ItemStack): ItemStack {
        insert.amount = 1
        // Strip All Extra Data Away
        return insert
    }

    private fun setupFromInfStack(insert: ItemStack): ItemStack {
        val meta = insert.itemMeta!!
        val amount = meta.persistentDataContainer.get(AMOUNT_KEY, PersistentDataType.LONG)!!
        this.size = amount
        insert.amount = 1 // ensure size is 1
        return insert
    }

    private fun setupFromNonInfStack(insert: ItemStack): ItemStack {
        val size = insert.amount.toLong()
        this.size = size
        insert.amount = 1 // ensure size is 1
        val meta = insert.itemMeta!!
        meta.persistentDataContainer.set(AMOUNT_KEY, PersistentDataType.LONG, size)
        insert.itemMeta = meta
        return insert
    }
}
