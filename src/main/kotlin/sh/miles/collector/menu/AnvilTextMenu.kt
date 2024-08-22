package sh.miles.collector.menu

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import sh.miles.collector.CollectorPlugin
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapple.nms.api.menu.MenuType
import sh.miles.pineapple.nms.api.menu.scene.AnvilScene

class AnvilTextMenu(player: Player, private val closeCallback: (String) -> Unit) :
    PlayerGui<AnvilScene>({ MenuType.ANVIL.create(player, PineappleChat.parse("TODO customizable name")) }, player) {

    lateinit var text: String

    override fun decorate() {
        slot(0) { inventory ->
            GuiSlotBuilder().inventory(inventory).index(0).item(ItemBuilder.of(Material.PAPER).build())
                .drag { it.isCancelled = true }.click { it.isCancelled = true }.build()
        }
        slot(1) { inventory ->
            GuiSlotBuilder().inventory(inventory).index(1).drag { it.isCancelled = true }.click {
                    it.isCancelled = true
                    this.text = ""
                    for (index in (0 until size())) {
                        slot(index).item = ItemStack(Material.AIR)
                    }
                    close()
                }.drag {
                    it.isCancelled = true
                }.build()
        }
        slot(2) { inventory ->
            GuiSlotBuilder().inventory(inventory).index(2).drag { it.isCancelled = true }.click {
                it.isCancelled = true
                if (it.clickedInventory!!.getItem(it.slot) == null) {
                    return@click
                }
                val inventory = it.clickedInventory!! as AnvilInventory
                this.text = inventory.renameText ?: ""
                for (index in (0 until size())) {
                    slot(index).item = ItemStack(Material.AIR)
                }
                close()
            }.drag {
                it.isCancelled = true
            }.build()
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {
        super.handleClose(event)
        if (this.text.isNotEmpty()) {
            closeCallback.invoke(this.text)
        }
    }

}
