package sh.miles.collect.collector.view

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import sh.miles.collect.collector.container.InfStackContainer
import sh.miles.collect.collector.view.menu.CollectorMenuListener
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.nms.api.menu.scene.MenuScene

class CollectorView(viewer: Player, private val container: InfStackContainer) : PlayerGui<MenuScene>(
    {
        PineappleLib.getNmsProvider()
            .createMenuCustom(viewer, CollectorMenuListener(36), 4, PineappleChat.parse("<gray>Collector Menu"))
    }, viewer
) {
    override fun decorate() {
        for ((index, itemStack) in container.withIndex()) {
            slot(index) { inventory ->
                GuiSlotBuilder()
                    .inventory(inventory)
                    .item(itemStack)
                    .click { it.isCancelled = true }
                    .index(index)
                    .build()
            }
        }

        for (index in size() - 9 until size()) {
            slot(index) { inventory ->
                GuiSlotBuilder()
                    .inventory(inventory)
                    .item(ItemStack(Material.BLACK_STAINED_GLASS_PANE))
                    .click { it.isCancelled = true }
                    .index(index)
                    .build()
            }
        }
    }

}
