package sh.miles.collect.hook

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PlaceholderAPIHook {

    private var enabled: Boolean = false

    init {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            enabled = true
        }
    }

    fun replacePlaceholders(player: Player, message: String): String {
        if (enabled) return PlaceholderAPI.setPlaceholders(player, message)
        return message
    }
}