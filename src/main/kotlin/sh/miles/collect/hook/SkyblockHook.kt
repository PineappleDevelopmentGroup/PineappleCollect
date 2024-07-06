package sh.miles.collect.hook

import org.bukkit.Bukkit
import sh.miles.collect.hook.SkyblockHook.getHook
import sh.miles.collect.hook.skyblock.AbstractSkyblockHook
import sh.miles.collect.hook.skyblock.EmptySkyblockHook
import sh.miles.collect.hook.skyblock.SuperiorSkyblockHook

object SkyblockHook : AbstractSkyblockHook by getHook() {

    private var modified: Boolean = false
    private var skyblockHook: AbstractSkyblockHook = EmptySkyblockHook

    private fun getHook(): AbstractSkyblockHook {
        if (modified) return skyblockHook

        if (Bukkit.getPluginManager().isPluginEnabled("SuperiorSkyblock2")) {
            modified = true
            skyblockHook = SuperiorSkyblockHook
        }
        // More skyblock plugin hooks

        return skyblockHook
    }

    // The methods in AbstractSkyblockHook are automatically delegated
}