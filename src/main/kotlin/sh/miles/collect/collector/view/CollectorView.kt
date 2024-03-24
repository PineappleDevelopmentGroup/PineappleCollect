package sh.miles.collect.collector.view

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import sh.miles.collect.collector.container.InfStackContainer
import sh.miles.collect.collector.view.menu.CollectorMenuListener
import sh.miles.collect.util.PluginHooks
import sh.miles.collect.util.item.InfStack
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.nms.api.menu.scene.MenuScene

class CollectorView(viewer: Player, private val container: InfStackContainer) : PlayerGui<MenuScene>(
    {
        PineappleLib.getNmsProvider()
            .createMenuCustom(
                viewer,
                CollectorMenuListener(container, 36),
                4,
                PineappleChat.parse("<gray>Collector Menu")
            )
    }, viewer
) {
    override fun decorate() {
        for ((index, itemStack) in container.withIndex()) {
            slot(index) { inventory ->
                GuiSlotBuilder()
                    .inventory(inventory)
                    .item(itemStack)
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

        slot(size() - 1) { inventory ->
            GuiSlotBuilder()
                .inventory(inventory)
                .item(ItemStack(Material.BARRIER))
                .click { event ->
                    event.isCancelled = true
                    val player = event.whoClicked as Player
                    for (index in 0 until size() - 9) {
                        when (val option = container.getInfStackAt(index)) {
                            is Some -> {
                                val stack = option.some()
                                PluginHooks.sellItem(player, stack.comparator(), stack.size())
                                container.setInfStackAt(index, InfStack())
                            }
                            is None -> return@click
                        }
                    }
                    cleanEmpties()
                }
                .index(size() - 1)
                .build()
        }

        container.changeListener = ::changeListener
        cleanEmpties()
    }

    private fun cleanEmpties() {
        for (i in 0 until container.size) {
            when (val someStack = container.getInfStackAt(i)) {
                is Some -> {
                    val stack = someStack.some()
                    if (stack.isEmpty()) {
                        container.setInfStackAt(i, InfStack())
                    }
                }

                is None -> continue
            }
        }
        container.condense()
    }

    private fun changeListener(index: Int, infStack: InfStack) {
        slot(index).item = container.getItemAt(index)
    }
}
