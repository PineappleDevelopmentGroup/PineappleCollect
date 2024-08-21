package sh.miles.collector.menu

import org.bukkit.inventory.ItemStack
import sh.miles.collector.configuration.CollectorConfiguration
import sh.miles.crown.infstacks.InfStack
import sh.miles.crown.infstacks.InfStackFactory
import sh.miles.pineapple.collection.NonNullArray
import java.util.UUID

class InfStackContainer {

    private val contents: NonNullArray<InfStack>
    private val factory: InfStackFactory
    val listener: ChangeListener = ChangeListener()

    constructor(configuration: CollectorConfiguration) {
        this.contents = NonNullArray(configuration.storageSlots) { configuration.infStackFactory.air() }
        this.factory = configuration.infStackFactory
    }

    constructor(configuration: CollectorConfiguration, items: List<ItemStack>) {
        if (configuration.storageSlots != items.size) throw IllegalStateException("InfStackContainer can not be initialized because the storageSlot amount does not line up with the stacks provided")
        this.contents = NonNullArray(configuration.storageSlots) { configuration.infStackFactory.air() }
        this.contents.addAll(items.map { configuration.infStackFactory.create(it) })
        this.factory = configuration.infStackFactory
    }

    fun modify(slot: Int, modification: (InfStack) -> Unit) {
        val item = contents[slot]
        modification.invoke(item)
        if (item.isEmpty) {
            this.contents.remove(item)
            listener.invoke(slot, item)
        }
    }

    fun modify(slots: IntArray, modification: (InfStack) -> Unit) {
        for (slot in slots) {
            modify(slot, modification)
        }
    }

    fun add(item: ItemStack): Boolean {
        val emptyQueue = ArrayDeque<Int>()
        for ((index, infStack) in this.contents.withIndex()) {
            if (infStack.isSimilar(item)) {
                infStack.grow(item)
                listener.invoke(index, contents[index])
                return true
            }

            if (infStack.isEmpty) {
                emptyQueue.add(index)
            }
        }

        if (!emptyQueue.isEmpty()) {
            val index = emptyQueue.removeFirst()
            this.contents[index] = this.factory.create(item)
            listener.invoke(index, contents[index])
            return true
        }

        return false
    }

    fun condense() {
        var condenseTo: Int = -999
        for ((index, infStack) in contents.withIndex()) {
            if (infStack.isEmpty()) {
                condenseTo = index
                break
            }
        }

        if (condenseTo == -999) {
            return
        }

        val holder = mutableListOf<InfStack>()
        for (i in condenseTo + 1 until this.contents.size) {
            val stack = contents.remove(i)
            if (stack.isAir) continue
            holder.add(stack)
        }

        var index = condenseTo
        for (infStack in holder) {
            contents[index] = infStack
            index++
        }

        // REVERSE Order is very important as if its done another way dupes are possible this removes the old stacks before re-adding the new ones
        for (i in contents.size - 1 downTo condenseTo) {
            listener.invoke(i, contents[i])
        }
    }

    fun getContents(): MutableList<ItemStack> {
        return ArrayList(contents.map { it.display })
    }

    class ChangeListener {
        private val listening = mutableMapOf<UUID, (Int, InfStack) -> Unit>()

        fun listen(uuid: UUID, listener: (Int, InfStack) -> Unit) {
            listening[uuid] = listener
        }

        fun invoke(slot: Int, infStack: InfStack) {
            for (entry in listening) {
                entry.value.invoke(slot, infStack)
            }
        }

        fun cancel(uuid: UUID) {
            listening.remove(uuid)
        }
    }
}