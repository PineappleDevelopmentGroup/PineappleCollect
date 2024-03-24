package sh.miles.collect.collector.container

import org.bukkit.inventory.ItemStack
import sh.miles.collect.util.item.InfStack
import sh.miles.pineapple.collection.NonNullArray
import sh.miles.pineapple.container.Container
import sh.miles.pineapple.container.ContainerSnapshot
import sh.miles.pineapple.function.Option
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some

class InfStackContainer(private val size: Int) : Container {

    private val contents = NonNullArray(size) { InfStack() }
    var changeListener: (Int, InfStack) -> Unit = { _, _ -> }

    fun getInfStackAt(index: Int): Option<InfStack> {
        val infStack = contents[index]
        if (infStack.sourceType().isAir) return Option.none()
        return Option.some(infStack)
    }

    override fun getItemAt(index: Int): ItemStack {
        return contents[index].source()
    }

    fun setInfStackAt(index: Int, item: InfStack) {
        contents[index] = item
        changeListener.invoke(index, item)
    }

    override fun setItemAt(index: Int, item: ItemStack) {
        val infStack = contents[index]
        if (infStack.isEmpty()) {
            contents[index] = InfStack(item)
            changeListener.invoke(index, contents[index])
            return
        }

        if (!infStack.isSimilar(item)) {
            throw IllegalStateException("Can not set dissimilar item in occupied slot inside of ${javaClass.simpleName}")
        }

        infStack.grow(item)
        changeListener.invoke(index, infStack)
    }

    override fun addItem(item: ItemStack): Boolean {
        val emptyQueue = ArrayDeque<Int>()
        for ((index, infStack) in this.contents.withIndex()) {
            if (infStack.isSimilar(item)) {
                infStack.grow(item)
                changeListener.invoke(index, contents[index])
                return true
            }

            if (infStack.isEmpty()) {
                emptyQueue.add(index)
            }
        }

        if (!emptyQueue.isEmpty()) {
            val index = emptyQueue.removeFirst()
            this.contents[index] = InfStack(item)
            changeListener.invoke(index, contents[index])
            return true
        }

        return false
    }

    override fun removeItem(index: Int): Option<ItemStack> {
        throw UnsupportedOperationException("This method can not be reliably implemented in this container")
    }

    fun removeInfStack(index: Int): Option<InfStack> {
        val stack = contents[index]
        if (stack.isEmpty()) return Option.none()
        contents[index] = InfStack()
        return Option.some(stack)
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

    fun condense() {
        var condenseTo: Int = -999
        for ((index, infStack) in contents.withIndex()) {
            if (infStack.isEmpty()) {
                println("condenseTo at $condenseTo")
                condenseTo = index
                break
            }
        }

        if (condenseTo == -999) {
            return
        }

        val holder = mutableListOf<InfStack>()
        for (i in condenseTo + 1 until this.contents.size) {
            when (val stack = removeInfStack(i)) {
                is Some -> holder.add(stack.some())
                is None -> continue
            }
        }

        var index = condenseTo
        for (infStack in holder) {
            contents[index] = infStack
            index++
        }

        // REVERSE Order is very important as if its done another way dupes are possible this removes the old stacks before re-adding the new ones
        for (i in contents.size - 1 downTo condenseTo) {
            changeListener.invoke(i, contents[i])
        }
    }


}
