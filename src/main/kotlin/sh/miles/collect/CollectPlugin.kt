package sh.miles.collect

import org.bukkit.plugin.java.JavaPlugin
import sh.miles.collect.hook.SkyblockHook
import sh.miles.collect.hook.VaultHook
import sh.miles.pineapple.PineappleLib

class CollectPlugin : JavaPlugin() {

    override fun onEnable() {
        PineappleLib.initialize(this)

        // Initialize Plugin Hooks
        VaultHook
    }
}