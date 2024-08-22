package sh.miles.collector.util

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import sh.miles.collector.tile.CollectorTile
import sh.miles.pineapple.collection.registry.RegistryKey

data class ClickAction(private val id: String, val action: (CollectorTile, String, Player, InventoryClickEvent) -> Unit) :
    RegistryKey<String> {
    override fun getKey(): String {
        return id
    }
}
