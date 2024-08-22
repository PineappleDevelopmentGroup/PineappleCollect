package sh.miles.collector.menu

import org.bukkit.entity.Player
import sh.miles.collector.Registries
import sh.miles.collector.configuration.MainMenuConfiguration
import sh.miles.collector.tile.CollectorTile
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.nms.api.menu.MenuType
import sh.miles.pineapple.nms.api.menu.scene.MenuScene

class CollectorMainMenu(
    private val player: Player, private val tile: CollectorTile, private val config: MainMenuConfiguration
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
                            Registries.MAIN_MENU_ACTION.get(guiItem.actionId).orThrow().action.invoke(
                                tile, config.sellMenuId, player, it
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

}
