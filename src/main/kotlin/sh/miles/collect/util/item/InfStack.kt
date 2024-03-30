package sh.miles.collect.util.item

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import sh.miles.collect.util.MessageConfig
import sh.miles.collect.util.PDC_SIZE_KEY
import sh.miles.pineapple.PineappleLib
import java.lang.IllegalStateException

/**
 * A Wrapper for an ItemStack which manages stacking infinitely
 */
class InfStack {

    companion object {
        val AMOUNT_KEY = NamespacedKey.fromString("collector:stack_size")!!

        /**
         * @param stack The data stack that DOES have the modified data, stack size and size pdc keys
         *
         */
        fun createStack(stack: ItemStack): InfStack {
            val comparator = stack.clone()
            val source = stack.clone()

            if (comparator.type.isAir) return InfStack()

            val comparatorMeta = comparator.itemMeta!!
            val comparatorLore = comparatorMeta.lore!!
            comparatorLore.removeAt(comparatorLore.size - 1)
            comparatorMeta.lore = comparatorLore
            val comparatorPdc = comparatorMeta.persistentDataContainer
            comparatorPdc.remove(AMOUNT_KEY)
            comparatorPdc.remove(PDC_SIZE_KEY)
            comparator.itemMeta = comparatorMeta

            return InfStack(source, comparator)
        }
    }

    private var source: ItemStack // nms
    private val comparator: ItemStack
    private var size: Long = 0

    constructor(source: ItemStack, comparator: ItemStack) {
        this.source = source
        this.comparator = comparator
        this.size = this.source.itemMeta!!.persistentDataContainer.getOrDefault(AMOUNT_KEY, PersistentDataType.LONG, 0)
    }

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
                setChanged()
            } else {
                this.source = setupFromNonInfStack(insert)
                setChanged(false)
            }
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

    fun comparator(): ItemStack {
        return this.comparator.clone()
    }

    fun sourceType(): Material {
        return this.source.type
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
        if (this.size > amount) {
            this.size -= amount
            endStackSize = amount
        } else {
            endStackSize = this.size.toInt()
            this.size = 0
        }

        val result = comparator.clone()
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

    private fun setChanged(isNew: Boolean = false) {
        val meta = this.source.itemMeta!!
        meta.persistentDataContainer.set(AMOUNT_KEY, PersistentDataType.LONG, size)
        val lore: MutableList<BaseComponent> = PineappleLib.getNmsProvider().getItemLore(this.source)
        if (lore.size >= 1 && !isNew) lore.removeAt(lore.size - 1)
        source.itemMeta = meta

        lore.add(MessageConfig.INFSTACK_LORE.component(mapOf("amount" to size)))
        this.source = PineappleLib.getNmsProvider().setItemLore(source, lore)
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
