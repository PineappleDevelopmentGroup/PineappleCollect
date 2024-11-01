package sh.miles.collector.util

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import sh.miles.collector.tile.CollectorTile
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.gui.PlayerGui

data class MenuAction(
    val id: String,
    val click: (ActionData, InventoryClickEvent) -> Unit,
    val open: (ActionData, InventoryOpenEvent) -> Unit,
    val close: (ActionData, InventoryCloseEvent) -> Unit,
    val decorate: (ActionData, Inventory) -> Unit
) : RegistryKey<String> {

    constructor(id: String, click: (ActionData, InventoryClickEvent) -> Unit) : this(id,
        click,
        { _, _ -> },
        { _, _ -> })

    constructor(
        id: String,
        click: (ActionData, InventoryClickEvent) -> Unit,
        open: (ActionData, InventoryOpenEvent) -> Unit,
        close: (ActionData, InventoryCloseEvent) -> Unit
    ) : this(id, click, open, close, { _, _ -> })

    override fun getKey(): String {
        return id
    }

    data class ActionData(
        val providedArgs: List<String>,
        val slot: Int,
        val tile: CollectorTile,
        val lastOpenMenu: PlayerGui<*>?,
        val viewer: Player
    )
}
