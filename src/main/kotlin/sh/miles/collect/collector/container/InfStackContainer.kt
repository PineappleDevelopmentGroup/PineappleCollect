package sh.miles.collect.collector.container

import org.bukkit.inventory.ItemStack
import sh.miles.collect.util.item.InfStack
import sh.miles.pineapple.collection.NonNullArray
import sh.miles.pineapple.container.Container
import sh.miles.pineapple.container.ContainerSnapshot
import sh.miles.pineapple.function.Option

class InfStackContainer(private val size: Int) : Container {

    private val contents = NonNullArray(size) { InfStack() }

    fun getInfStackAt(index: Int): Option<InfStack> {
        val infStack = contents[index]
        if (infStack.isEmpty()) return Option.none()
        return Option.some(infStack)
    }

    override fun getItemAt(index: Int): ItemStack {
        return contents[index].source()
    }

    fun setInfStackAt(index: Int, item: InfStack) {
        contents[index] = item
    }

    override fun setItemAt(index: Int, item: ItemStack) {
        val infStack = contents[index]
        if (infStack.isEmpty()) {
            contents[index] = InfStack(item)
            return
        }

        if (!infStack.isSimilar(item)) {
            throw IllegalStateException("Can not set dissimilar item in occupied slot inside of ${javaClass.simpleName}")
        }

        infStack.grow(item)
    }

    override fun addItem(item: ItemStack): Boolean {
        var emptyQueue = ArrayDeque<Int>()
        for ((index, infStack) in this.contents.withIndex()) {
            if (infStack.isSimilar(item)) {
                infStack.grow(item)
                return true
            }

            if (infStack.isEmpty()) {
                emptyQueue.add(index)
            }
        }

        if (!emptyQueue.isEmpty()) {
            val index = emptyQueue.removeFirst()
            this.contents[index] = InfStack(item)
            return true
        }

        return false
    }

    override fun removeItem(index: Int): Option<ItemStack> {
        val stack = contents[index]
        if (stack.isEmpty()) return Option.none()
        return Option.some(stack.source())
    }

    override fun contains(item: ItemStack): Boolean {
        for (infStack in this.contents) {
            if (infStack.isSimilar(item)) {
                return true
            }
        }
        return false
    }

    override fun getContents(): List<ItemStack> {
        return this.contents.stream().map(InfStack::source).toList()
    }

    override fun getSize(): Int {
        return this.contents.size
    }

    override fun getSnapshot(): ContainerSnapshot {
        throw UnsupportedOperationException("Unable to take snapshots of InfStackContainers")
    }


}
