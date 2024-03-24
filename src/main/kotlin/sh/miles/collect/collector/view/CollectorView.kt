package sh.miles.collect.collector.view

import org.bukkit.block.TileState
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.collect.collector.CollectorTemplate
import sh.miles.collect.collector.container.InfStackContainer
import sh.miles.collect.collector.view.menu.CollectorMenuListener
import sh.miles.collect.util.CollectorMenuSpec
import sh.miles.collect.util.PDC_CONTENT_KEY
import sh.miles.collect.util.PDC_SIZE_KEY
import sh.miles.collect.util.PDC_TEMPLATE_KEY
import sh.miles.collect.util.PluginHooks
import sh.miles.collect.util.Position
import sh.miles.collect.util.item.InfStack
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.nms.api.menu.scene.MenuScene

class CollectorView(viewer: Player, private val container: InfStackContainer, private val size: Int, private val templateName: String, private val position: Position) : PlayerGui<MenuScene>(
    {
        PineappleLib.getNmsProvider()
            .createMenuCustom(
                viewer,
                CollectorMenuListener(container, container.size + 9),
                (container.size / 9) + 1,
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
                    .item(CollectorMenuSpec.backgroundItem)
                    .click {
                        it.isCancelled = true
                        // TODO: Open Upgrade Menu
                    }
                    .index(index)
                    .build()
            }
        }

        val sellItemLoc = container.size + CollectorMenuSpec.sellItemLoc
        slot(sellItemLoc) {
            GuiSlotBuilder()
                .inventory(it)
                .item(CollectorMenuSpec.sellItem)
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
                .index(sellItemLoc)
                .build()
        }

        val upgradeItemLoc = container.size + CollectorMenuSpec.upgradeItemLoc
        slot(upgradeItemLoc) {
            GuiSlotBuilder()
                .inventory(it)
                .item(CollectorMenuSpec.upgradeItem)
                .click { event ->
                    event.isCancelled = true


                    //close inv -> copy current content -> change block -> update state -> add contents -> save
                    viewer().closeInventory()
                    val newTemplate: CollectorTemplate
                    when (val option = CollectorManager.getUpgradeTemplate(templateName)) {
                        is Some -> newTemplate = option.some()
                        is None -> return@click
                    }

                    val collectorLocation = position.toLocation()
                    val collectorBlock = collectorLocation.block
                    val beforeContent = CollectorManager.obtain(position.chunkpos()).orThrow().inventory.contents
                    val chunkLocation = position.chunkpos()
                    val chunk = viewer().world.getChunkAt(chunkLocation.x, chunkLocation.z)

                    Collector.delete(chunk)
                    collectorBlock.type = newTemplate.blockEntity

                    val updatedState = collectorBlock.state as TileState
                    val statePdc = updatedState.persistentDataContainer

                    statePdc.set(PDC_TEMPLATE_KEY, PersistentDataType.STRING, newTemplate.key)
                    statePdc.set(PDC_SIZE_KEY, PersistentDataType.INTEGER, newTemplate.size)
                    statePdc.set(PDC_CONTENT_KEY, PersistentDataType.BYTE_ARRAY, PineappleLib.getNmsProvider().itemsToBytes(beforeContent))

                    updatedState.update()

                    val newCollector = Collector(newTemplate.key, newTemplate.size, position)
                    CollectorManager.load(newCollector)

                    for ((index, itemStack) in beforeContent.withIndex()) {
                        newCollector.inventory.setInfStackAt(index, InfStack.createStack(itemStack))
                    }
                }
                .index(upgradeItemLoc)
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
