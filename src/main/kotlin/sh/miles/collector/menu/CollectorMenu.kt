package sh.miles.collector.menu

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import sh.miles.collector.configuration.MenuConfiguration
import sh.miles.collector.hook.EconomyShopHook
import sh.miles.collector.hook.VaultHook
import sh.miles.collector.tile.CollectorTile
import sh.miles.crown.infstacks.InfStack
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.nms.api.menu.MenuType
import sh.miles.pineapple.nms.api.menu.scene.MenuScene

class CollectorMenu(private val player: Player, private val tile: CollectorTile, private val config: MenuConfiguration) :
    PlayerGui<MenuScene>(
        { MenuType.fromRows(config.viewRows).create(it, config.title.component()) }, player
    ) {

    override fun decorate() {
        val storageSlots = config.storageSlots
        val backgroundItem = config.backgroundItem.buildSpec()
        val stackContainer = tile.stackContainer
        val slotMapping = HashBiMap.create<Int, Int>()
        var index = 0

        stackContainer.listener.listen(viewer().uniqueId) { slot, item ->
            slot(slotMapping.inverse()[slot]!!).item = item.display
        }

        for (slot in (0 until config.viewRows * 9)) {
            if (storageSlots.contains(slot)) {
                slotMapping[slot] = index
                // Storage Slot
                slot(slot) { inventory ->
                    GuiSlotBuilder()
                        .inventory(inventory)
                        .index(slot)
                        .item(stackContainer[index++].display)
                        .click {
                            val index = slotMapping[it.slot]!!
                            it.isCancelled = true
                            if (it.click == ClickType.LEFT) {
                                stackContainer.modify(index) { stack ->
                                    if (!EconomyShopHook.canSell(stack.comparator, player)) return@modify
                                    //TODO Convert to int max size check and call it once per max size if over
                                    println("${stack.comparator.serialize()} : ${stack.stackSize.toInt()}")
                                    val sellItem = EconomyShopHook.sellItem(stack.comparator, player, stack.stackSize.toInt())
                                    if (sellItem.first) {
                                        VaultHook.giveBalance(player, sellItem.second)
                                        stackContainer.modify(index) { modify -> modify.shrink(stack.stackSize)}
                                    }
                                }
                            } else if (it.click == ClickType.RIGHT) {
                                stackContainer.modify(index) { stack ->

                                }
                            }
                        }
                        .build()
                }
            } else {
                // Background Item
                slot(slot) { inventory ->
                    GuiSlotBuilder().inventory(inventory).index(slot).item(backgroundItem)
                        .click { it.isCancelled = true }.build()
                }
            }

        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        tile.stackContainer.listener.cancel(event.player.uniqueId)
        super.handleClose(event)
    }

}
