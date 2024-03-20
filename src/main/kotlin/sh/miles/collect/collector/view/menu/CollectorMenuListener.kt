package sh.miles.collect.collector.view.menu

import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import sh.miles.collect.collector.view.menu.slot.NonIntractableSlot
import sh.miles.pineapple.nms.annotations.NMS
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuContext
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuListener
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuListener.QuickMoveResult
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomSlotListener

@NMS
class CollectorMenuListener(size: Int) : CustomMenuListener {

    private val slots: Map<Int, CustomSlotListener>

    init {
        val temp = mutableMapOf<Int, CustomSlotListener>()
        for (index in 0 until size - 9) {
            temp[index] = NonIntractableSlot()
        }
        slots = temp.toMap()
    }

    override fun quickMoveItem(
        context: CustomMenuContext, player: HumanEntity, rawSlot: Int
    ): QuickMoveResult {
        var result = ItemStack(Material.AIR)
        val slot = context.getMenuSlot(rawSlot)
        if (slot != null && slot.hasBukkitItem()) {
            var slotItem = slot.bukkitItem
            result = slotItem.clone()
            if (rawSlot < context.rowAmount * 9) {
                val mergeResult =
                    context.mergeItemStackBetween(slotItem, context.rowAmount * 9, context.slotAmount, true)
                if (!mergeResult.result()) {
                    return QuickMoveResult.cancel()
                }

                slotItem = mergeResult.item()
            } else {
                return QuickMoveResult.cancel()
            }

            if (!slotItem.type.isAir && slotItem.amount != 0) {
                slot.setSlotChanged()
            } else {
                slot.setItemByPlayer(ItemStack(Material.AIR))
            }
        }
        return QuickMoveResult.complete(result)
    }

    override fun getSlotListener(rawSlot: Int): CustomSlotListener {
        return slots.getOrDefault(rawSlot, CustomSlotListener.EMPTY)
    }

}
