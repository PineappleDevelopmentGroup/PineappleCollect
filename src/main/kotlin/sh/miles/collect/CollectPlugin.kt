package sh.miles.collect

import org.bukkit.plugin.java.JavaPlugin
import sh.miles.collect.registry.CollectorTemplateRegistry
import sh.miles.collect.registry.json.CollectorTemplateAdapter
import sh.miles.collect.registry.json.PineappleComponentAdapter
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.json.JsonHelper

class CollectPlugin : JavaPlugin() {

    companion object {
        lateinit var plugin: CollectPlugin;
    }

    val json: JsonHelper = JsonHelper(
        PineappleComponentAdapter,
        CollectorTemplateAdapter
    )

    override fun onEnable() {
        plugin = this
        PineappleLib.initialize(this)

        CollectorTemplateRegistry.run { }
    }

    override fun onDisable() {
        PineappleLib.cleanup()
    }

}
