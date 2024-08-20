package sh.miles.pineapplecollectors.hook

import org.bukkit.Bukkit
import sh.miles.pineapplecollectors.hook.SkyblockHook.getHook
import sh.miles.pineapplecollectors.hook.skyblock.AbstractSkyblockHook
import sh.miles.pineapplecollectors.hook.skyblock.EmptySkyblockHook
import sh.miles.pineapplecollectors.hook.skyblock.SuperiorSkyblockHook

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