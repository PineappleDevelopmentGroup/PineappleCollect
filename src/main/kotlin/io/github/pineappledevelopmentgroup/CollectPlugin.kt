package io.github.pineappledevelopmentgroup

import io.github.pineappledevelopmentgroup.registry.CollectRegistries
import org.bukkit.plugin.java.JavaPlugin
import sh.miles.crown.tiles.Tiles
import sh.miles.pineapple.PineappleLib

class CollectPlugin : JavaPlugin() {

    override fun onEnable() {
        PineappleLib.initialize(this)
        Tiles.setup(this)
        CollectRegistries // init registries
    }

    override fun onDisable() {
        PineappleLib.cleanup()
        Tiles.cleanup()
    }
}
