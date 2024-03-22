package sh.miles.collect.collector.view.menu

import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import sh.miles.collect.collector.container.InfStackContainer
import sh.miles.collect.collector.view.menu.slot.InfStackStorageSlot
import sh.miles.pineapple.nms.annotations.NMS
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuContext
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuListener
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuListener.QuickMoveResult
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomSlotListener

@NMS
class CollectorMenuListener(private val container: InfStackContainer, size: Int) : CustomMenuListener {

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
        return QuickMoveResult.cancel()
    }

    override fun getSlotListener(rawSlot: Int): CustomSlotListener {
        return slots.getOrDefault(rawSlot, CustomSlotListener.EMPTY)
    }

}
