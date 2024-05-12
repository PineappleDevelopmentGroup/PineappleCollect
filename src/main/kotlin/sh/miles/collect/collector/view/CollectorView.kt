package sh.miles.collect.collector.view

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.TileState
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.collect.collector.template.CollectorTemplate
import sh.miles.collect.collector.container.InfStackContainer
import sh.miles.collect.collector.view.menu.CollectorMenuListener
import sh.miles.collect.registry.CollectorTemplateRegistry
import sh.miles.collect.util.CollectorMenuSpec
import sh.miles.collect.util.MessageConfig
import sh.miles.collect.util.PDC_CONTENT_KEY
import sh.miles.collect.util.PDC_POSITION_DATA_TYPE
import sh.miles.collect.util.PDC_POSITION_KEY
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
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapple.nms.api.menu.scene.MenuScene
import java.lang.IllegalStateException
import java.math.BigDecimal
import java.math.RoundingMode

class CollectorView(
    viewer: Player,
    private val container: InfStackContainer,
    private val templateName: String,
    private val position: Position
) : PlayerGui<MenuScene>(
    {
        PineappleLib.getNmsProvider()
            .createMenuCustom(
                viewer,
                CollectorMenuListener(container, container.size + 9),
                (container.size / 9) + 1,
                CollectorTemplateRegistry.getOrNull(templateName)!!.title.component()
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
                    var amount = BigDecimal.ZERO
                    for (index in 0 until size() - 9) {
                        when (val option = container.getInfStackAt(index)) {
                            is Some -> {
                                val stack = option.some()
                                amount = amount.add(PluginHooks.sellItem(player, stack.comparator(), stack.size()))
                                container.setInfStackAt(index, InfStack())
                            }

                            is None -> break
                        }
                    }
                    viewer().spigot().sendMessage(
                        MessageConfig.COLLECTOR_SOLD_ALL.component(
                            mapOf(
                                "amount" to amount.setScale(
                                    2,
                                    RoundingMode.HALF_UP
                                )
                            )
                        )
                    )
                    cleanEmpties()
                }
                .index(sellItemLoc)
                .build()
        }

        val upgradeItemLoc = container.size + CollectorMenuSpec.upgradeItemLoc
        val upgradeItemLore = ArrayList(PineappleLib.getNmsProvider().getItemLore(CollectorMenuSpec.upgradeItem))
        when (val upgradeSpec = CollectorTemplateRegistry.get(templateName).orThrow().upgradeSpec) {
            is Some -> {
                upgradeItemLore.add(
                    CollectorMenuSpec.priceLore.component(
                        mutableMapOf(
                            "price" to upgradeSpec.some().sizeUpgradeCost as Any
                        )
                    )
                )
            }

            is None -> {
                upgradeItemLore.add(
                    CollectorMenuSpec.priceLore.component(
                        mutableMapOf(
                            "price" to "N/A" as Any
                        )
                    )
                )
            }
        }
        val upgradeItem = PineappleLib.getNmsProvider().setItemLore(CollectorMenuSpec.upgradeItem, upgradeItemLore)
        slot(upgradeItemLoc) {
            GuiSlotBuilder()
                .inventory(it)
                .item(upgradeItem)
                .click { event ->
                    event.isCancelled = true


                    //close inv -> copy current content -> change block -> update state -> add contents -> save
                    val newTemplate: CollectorTemplate
                    val newTemplateCost: Double
                    when (val option = CollectorTemplateRegistry.get(templateName)) {
                        is Some -> {
                            when (val upgradeOption = option.some().upgradeSpec) {
                                is Some -> {
                                    val some = upgradeOption.some()
                                    newTemplate = CollectorTemplateRegistry.get(some.sizeUpgradeKey).orThrow()
                                    newTemplateCost = some.sizeUpgradeCost
                                }

                                is None -> return@click // If is none it cannot be upgraded as nothing was specified in config on load
                            }
                        }

                        is None -> return@click
                    }

                    viewer().closeInventory()

                    if (!PluginHooks.canAfford(viewer(), newTemplateCost)) {
                        viewer().spigot()
                            .sendMessage(MessageConfig.UPGRADE_NOT_ENOUGH_MONEY.component(mapOf("amount" to newTemplateCost)))
                        return@click
                    }

                    val collectorLocation = position.toLocation()
                    val collectorBlock = collectorLocation.block

                    val beforeContent = CollectorManager.obtain(position.chunkpos()).orThrow().inventory.contents

                    val chunkLocation = position.chunkpos()
                    val chunk = viewer().world.getChunkAt(chunkLocation.x, chunkLocation.z)

                    Collector.delete(chunk)
                    collectorBlock.type = newTemplate.blockEntity

                    PluginHooks.removeBalance(
                        viewer(),
                        newTemplateCost
                    ) // This is done after the collector is deleted and the entity is changed so there isnt any loss where it would take money and not do anything else
                    viewer().spigot().sendMessage(
                        MessageConfig.COLLECTOR_UPGRADED.component(
                            mapOf(
                                "id" to newTemplate.key,
                                "title" to PineappleChat.parseLegacy(newTemplate.title.source)
                            )
                        )
                    )

                    val updatedState = collectorBlock.state as TileState
                    val statePdc = updatedState.persistentDataContainer

                    statePdc.set(PDC_TEMPLATE_KEY, PersistentDataType.STRING, newTemplate.key)
                    statePdc.set(PDC_SIZE_KEY, PersistentDataType.INTEGER, newTemplate.size)
                    statePdc.set(
                        PDC_CONTENT_KEY,
                        PersistentDataType.BYTE_ARRAY,
                        PineappleLib.getNmsProvider().itemsToBytes(beforeContent)
                    )
                    chunk.persistentDataContainer.set(PDC_POSITION_KEY, PDC_POSITION_DATA_TYPE, position)

                    updatedState.update()

                    val newCollector = Collector(newTemplate.key, newTemplate.size, position, viewer().uniqueId)
                    CollectorManager.load(newCollector)

                    for ((index, itemStack) in beforeContent.withIndex()) {
                        newCollector.inventory.setInfStackAt(index, InfStack.createStack(itemStack))
                    }
                }
                .index(upgradeItemLoc)
                .build()
        }
        val pickupItemLoc = container.size + CollectorMenuSpec.pickupItemLoc
        slot(pickupItemLoc) {
            GuiSlotBuilder()
                .inventory(it)
                .item(CollectorMenuSpec.pickupItem)
                .click { event ->
                    event.isCancelled = true
                    val world = event.whoClicked.world
                    val chunk = event.whoClicked.location.chunk
                    when (val collector = CollectorManager.unload(chunk)) {
                        is Some -> {
                            val collectorSome = collector.some()
                            val location = collectorSome.position.toLocation()
                            val template =
                                CollectorTemplateRegistry.getOrNull(collectorSome.templateKey)!! // this should never be null
                            var item = template.item()
                            val itemMeta = item.itemMeta!!

                            if (!collectorSome.inventory.isOnlyAir()) {
                                itemMeta.persistentDataContainer.set(
                                    PDC_CONTENT_KEY,
                                    PersistentDataType.BYTE_ARRAY,
                                    PineappleLib.getNmsProvider().itemsToBytes(collectorSome.inventory.contents)
                                )
                                itemMeta.persistentDataContainer.set(
                                    PDC_SIZE_KEY,
                                    PersistentDataType.INTEGER,
                                    template.size
                                )
                                itemMeta.persistentDataContainer.set(
                                    PDC_TEMPLATE_KEY,
                                    PersistentDataType.STRING,
                                    template.key
                                )
                            }
                            item.itemMeta = itemMeta

                            item = ItemBuilder.modifyStack(item).lore(listOf(template.hasContentLore.component()))
                                .build()

                            val unableToAdd = event.whoClicked.inventory.addItem(item)
                            if (unableToAdd.isNotEmpty()) {
                                world.dropItemNaturally(location, unableToAdd[0]!!)

                            }
                            Collector.delete(chunk)
                            world.setType(location, Material.AIR)
                            close()
                        }

                        is None -> {
                            event.whoClicked.sendMessage("An internal error occurred")
                            throw IllegalStateException("No Collector Found Even Though All Checks Passed") // DEBUG ONLY
                        }
                    }
                }
                .index(pickupItemLoc)
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
