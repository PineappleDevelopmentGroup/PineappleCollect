package sh.miles.collector.menu

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import sh.miles.collector.CollectorPlugin
import sh.miles.collector.Registries
import sh.miles.collector.configuration.MenuConfiguration
import sh.miles.collector.tile.CollectorTile
import sh.miles.collector.util.MenuAction
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.nms.api.menu.MenuType
import sh.miles.pineapple.nms.api.menu.scene.MenuScene

class CollectorMenu(
    private val player: Player,
    private val lastOpenMenu: PlayerGui<*>?,
    private val tile: CollectorTile,
    private val config: MenuConfiguration
) : PlayerGui<MenuScene>(
    { MenuType.fromRows(config.viewRows).create(it, config.title.component()) }, player
) {
    override fun decorate() {
        val map = config.itemMap
        for (slot in (0 until config.viewRows * 9)) {
            if (map.containsKey(slot)) {
                val guiItem = map[slot]!!
                slot(slot) { inventory ->
                    val guiSlot = GuiSlotBuilder().inventory(inventory).index(slot).item(guiItem.item.buildSpec())
                        .drag { it.isCancelled = true }.click {
                            Registries.GENERAL_MENU_ACTION.get(guiItem.actionId)
                                .orThrow("No such action id found ${guiItem.actionId}")
                                .click(MenuAction.ActionData(guiItem.args, slot, tile, this, viewer()), it)
                            guiItem.clickSound.play(player)
                        }.build()

                    Registries.GENERAL_MENU_ACTION.get(guiItem.actionId)
                        .orThrow("No such action id found ${guiItem.actionId}").decorate.invoke(
                            MenuAction.ActionData(
                                guiItem.args,
                                guiItem.slot,
                                tile,
                                this,
                                viewer()
                            ), inventory
                        )
                    return@slot guiSlot
                }
            } else {
                slot(slot) { inventory ->
                    GuiSlotBuilder().inventory(inventory).index(slot).item(config.backgroundItem.buildSpec())
                        .drag { it.isCancelled = true }.click { it.isCancelled = true }.build()
                }
            }
        }
    }

    override fun handleOpen(event: InventoryOpenEvent) {
        config.itemMap.forEach { (_, guiItem) ->
            Registries.GENERAL_MENU_ACTION.get(guiItem.actionId).orThrow("No such action id found ${guiItem.actionId}")
                .open(MenuAction.ActionData(guiItem.args, guiItem.slot, tile, this, viewer()), event)
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        super.handleClose(event)

        config.itemMap.forEach { (_, guiItem) ->
            Registries.GENERAL_MENU_ACTION.get(guiItem.actionId).orThrow("No such action id found ${guiItem.actionId}")
                .close(MenuAction.ActionData(guiItem.args, guiItem.slot, tile, this, viewer()), event)
        }

        if (this.lastOpenMenu != null) {
            Bukkit.getScheduler().runTask(CollectorPlugin.plugin, Runnable {
                if (player.openInventory.type == InventoryType.CRAFTING || player.openInventory.type == InventoryType.CREATIVE) {
                    lastOpenMenu.open()
                }
            })
        }
    }
}
