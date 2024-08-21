package sh.miles.collector.menu

import org.bukkit.entity.Player
import sh.miles.collector.configuration.MenuConfiguration
import sh.miles.collector.tile.CollectorTile
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.nms.api.menu.MenuType
import sh.miles.pineapple.nms.api.menu.scene.MenuScene

class CollectorMenu(player: Player, private val tile: CollectorTile, private val config: MenuConfiguration) :
    PlayerGui<MenuScene>(
        { MenuType.fromRows(config.viewRows).create(it, config.title.component()) }, player
    ) {

    override fun decorate() {
        val storageSlots = config.storageSlots
        val backgroundItem = config.backgroundItem.buildSpec()
        val stackContainer = tile.stackContainer
        var index = 0

        for (slot in (0 until config.viewRows * 9)) {
            if (storageSlots.contains(slot)) {
                // Storage Slot
                slot(slot) { inventory ->
                    GuiSlotBuilder().inventory(inventory).index(slot).item(stackContainer[index++].display).build()
                }
            } else {
                // Background Item
                slot(slot) { inventory ->
                    GuiSlotBuilder().inventory(inventory).index(slot).item(backgroundItem)
                        .click { it.isCancelled = true }
                        .build()
                }
            }

        }
    }
}
