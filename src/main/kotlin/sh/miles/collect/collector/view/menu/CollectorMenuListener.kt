package sh.miles.collect.collector.view.menu

import org.bukkit.entity.HumanEntity
import sh.miles.collect.collector.container.InfStackContainer
import sh.miles.collect.collector.view.menu.slot.InfStackStorageSlot
import sh.miles.collect.collector.view.menu.slot.NonIntractableSlot
import sh.miles.collect.util.item.InfStack
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some
import sh.miles.pineapple.nms.annotations.NMS
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuContext
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuListener
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuListener.QuickMoveResult
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomSlotListener

@NMS
class CollectorMenuListener(private val container: InfStackContainer, private val size: Int) : CustomMenuListener {

    companion object {
        private val NON_INTRACTABLE = NonIntractableSlot()
    }

    private val slots: Map<Int, CustomSlotListener>

    init {
        val temp = mutableMapOf<Int, CustomSlotListener>()
        for (index in 0 until size - 9) {
            temp[index] = InfStackStorageSlot(container, index)
        }
        slots = temp.toMap()
    }

    override fun quickMoveItem(
        context: CustomMenuContext, player: HumanEntity, rawSlot: Int
    ): QuickMoveResult {
        println(rawSlot)
        if (rawSlot >= size) {
            return QuickMoveResult.cancel()
        }

        val option = container.getInfStackAt(rawSlot)
        val stack: InfStack
        when (option) {
            is Some -> stack = option.some();
            is None -> return QuickMoveResult.cancel()
        }

        var extraction = stack.extract(64)

        val mergeResult = context.mergeItemStackBetween(extraction, size, size + (9 * 4), true)
        if (!mergeResult.result) {
            stack.grow(extraction)
            return QuickMoveResult.cancel()
        }

        extraction = mergeResult.item
        if (extraction.amount > 0) {
            stack.grow(extraction)
        }

        if (stack.isEmpty()) {
            this.container.setInfStackAt(rawSlot, InfStack())
            this.container.condense()
        } else {
            this.container.changeListener.invoke(rawSlot, stack)
        }

        return QuickMoveResult.cancel()
    }

    override fun getSlotListener(rawSlot: Int): CustomSlotListener {
        if (rawSlot >= size) {
            return CustomSlotListener.EMPTY
        }

        return slots.getOrDefault(rawSlot, NON_INTRACTABLE)
    }

}
