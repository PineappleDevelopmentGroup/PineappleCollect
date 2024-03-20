package sh.miles.collect

import org.bukkit.plugin.java.JavaPlugin
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.collect.command.CollectorCommand
import sh.miles.collect.listeners.ChunkStateListener
import sh.miles.collect.listeners.CollectorInteractListener
import sh.miles.collect.listeners.CollectorPickupListener
import sh.miles.collect.listeners.CollectorPlaceListener
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
        saveResources()
        plugin = this
        PineappleLib.initialize(this)

        CollectorTemplateRegistry.run { }

        PineappleLib.getCommandRegistry().register(CollectorCommand)

        server.pluginManager.registerEvents(ChunkStateListener, this)
        server.pluginManager.registerEvents(CollectorInteractListener, this)
        server.pluginManager.registerEvents(CollectorPlaceListener, this)
        server.pluginManager.registerEvents(CollectorPickupListener, this)
    }

    override fun onDisable() {
        PineappleLib.cleanup()
        CollectorManager.obtainAll().forEach { Collector.save(it.position.toLocation().chunk, it) }
    }

    fun saveResources() {
        saveResource("collector-templates.json", false)
    }

}
