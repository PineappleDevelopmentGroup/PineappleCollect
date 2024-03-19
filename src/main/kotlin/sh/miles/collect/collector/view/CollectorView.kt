package sh.miles.collect.collector.view

import org.bukkit.entity.Player
import sh.miles.collect.collector.inventory.CollectorInventory
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.nms.api.menu.MenuType
import sh.miles.pineapple.nms.api.menu.scene.MenuScene

class CollectorView(viewer: Player, private val inventory: CollectorInventory) : PlayerGui<MenuScene>(
    {
        MenuType.fromRows(inventory.size / 9).create(viewer, PineappleChat.parse("Collector"))
    }, viewer
) {

    override fun decorate() {

    }
}
