package sh.miles.collect.collector.view.menu.slot

import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import sh.miles.collect.collector.container.InfStackContainer
import sh.miles.collect.util.item.InfStack
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuContext
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuSlot
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomSlotListener

class InfStackStorageSlot(private val container: InfStackContainer, private val slot: Int) : CustomSlotListener {

    override fun onTakeItem(
        menuContext: CustomMenuContext, slotContext: CustomMenuSlot, player: HumanEntity, item: ItemStack
    ): Boolean {
        val stack = container.getInfStackAt(slot).orThrow()
        if (stack.isEmpty()) return false

        val export = stack.extract(64)
        player.setItemOnCursor(export)

        if (stack.isEmpty()) {
            container.setInfStackAt(slot, InfStack())
            container.condense()
        } else container.changeListener.invoke(slot, stack)
        return false
    }

    override fun dictateMayPlaceItem(
        menuContext: CustomMenuContext, slotContext: CustomMenuSlot, item: ItemStack
    ): Boolean {
        return false
    }

}
