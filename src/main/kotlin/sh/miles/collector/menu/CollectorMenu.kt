package sh.miles.collector.menu

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import sh.miles.collector.CollectorPlugin
import sh.miles.collector.Registries
import sh.miles.collector.configuration.MenuConfiguration
import sh.miles.collector.tile.CollectorTile
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
                    GuiSlotBuilder().inventory(inventory).index(slot).item(guiItem.item.buildSpec())
                        .drag { it.isCancelled = true }.click {
                            Registries.MENU_ACTION.get(guiItem.actionId).orThrow().action.invoke(
                                tile, this, slot, guiItem.link, player, it
                            )
                            guiItem.clickSound.playSound(player)
                        }.build()
                }
            } else {
                slot(slot) { inventory ->
                    GuiSlotBuilder().inventory(inventory).index(slot).item(config.backgroundItem.buildSpec())
                        .drag { it.isCancelled = true }.click { it.isCancelled = true }.build()
                }
            }
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        super.handleClose(event)
        if (this.lastOpenMenu != null) {
            Bukkit.getScheduler().runTask(CollectorPlugin.plugin, Runnable {
                if (player.openInventory.type == InventoryType.CRAFTING) {
                    lastOpenMenu.open()
                }
            })
        }
    }
}
