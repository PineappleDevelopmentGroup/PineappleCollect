package sh.miles.collector.menu

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import sh.miles.collector.CollectorPlugin
import sh.miles.collector.GlobalConfig
import sh.miles.pineapple.gui.PlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapple.nms.api.menu.MenuType
import sh.miles.pineapple.nms.api.menu.scene.AnvilScene

class AnvilTextMenu(
    player: Player,
    private val viewItem: ItemStack,
    private val lastOpenMenu: PlayerGui<*>?,
    private val closeCallback: (String) -> Unit
) :
    PlayerGui<AnvilScene>({ MenuType.ANVIL.create(player, GlobalConfig.ANVIL_TITLE.component()) }, player) {

    var text: String = ""
        private set

    override fun decorate() {
        slot(0) { inventory ->
            GuiSlotBuilder().inventory(inventory).index(0).item(viewItem)
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
        for (index in (0 until size())) {
            slot(index).item = ItemStack(Material.AIR)
        }

        if (this.text.isNotEmpty()) {
            closeCallback.invoke(this.text)
        }


        if (this.lastOpenMenu != null) {
            Bukkit.getScheduler().runTask(CollectorPlugin.plugin, Runnable {
                lastOpenMenu.open()
            })
        }
    }

}
