package sh.miles.collect.collector.view.menu.slot

import org.bukkit.entity.HumanEntity
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuContext
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomMenuSlot
import sh.miles.pineapple.nms.api.menu.scene.custom.CustomSlotListener

class NonIntractableSlot : CustomSlotListener {

    override fun dictateMayPlaceItem(
        menuContext: CustomMenuContext,
        slotContext: CustomMenuSlot,
        item: ItemStack
    ): Boolean {
        return false
    }

    override fun dictateMayPickupItem(
        menuContext: CustomMenuContext,
        slotContext: CustomMenuSlot,
        player: HumanEntity
    ): Boolean {
        return false
    }
}
