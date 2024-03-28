package sh.miles.collect

import org.bukkit.plugin.java.JavaPlugin
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.collect.command.CollectorCommand
import sh.miles.collect.listeners.ChunkStateListener
import sh.miles.collect.listeners.CollectorCollectListener
import sh.miles.collect.listeners.CollectorInteractListener
import sh.miles.collect.listeners.CollectorPickupListener
import sh.miles.collect.listeners.CollectorPlaceListener
import sh.miles.collect.listeners.DeluxeSellWandsListener
import sh.miles.collect.registry.CollectorTemplateRegistry
import sh.miles.collect.registry.json.CollectorTemplateAdapter
import sh.miles.collect.registry.json.CollectorTemplateUpgradeDataAdapter
import sh.miles.collect.registry.json.PineappleComponentAdapter
import sh.miles.collect.util.CollectorMenuSpec
import sh.miles.collect.util.MessageConfig
import sh.miles.collect.util.json.CollectorMenuSpecAdapter
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.config.ConfigWrapper
import sh.miles.pineapple.json.JsonHelper
import java.io.File

class CollectPlugin : JavaPlugin() {

    private lateinit var messageConfig: ConfigWrapper

    companion object {
        lateinit var plugin: CollectPlugin;
    }

    val json: JsonHelper = JsonHelper(
        PineappleComponentAdapter,
        CollectorTemplateAdapter,
        CollectorMenuSpecAdapter,
        CollectorTemplateUpgradeDataAdapter
    )

    override fun onEnable() {
        saveResources()
        plugin = this
        PineappleLib.initialize(this)

        CollectorTemplateRegistry.run { }
        CollectorMenuSpec.run { }

        PineappleLib.getCommandRegistry().register(CollectorCommand)

        server.pluginManager.registerEvents(ChunkStateListener, this)
        server.pluginManager.registerEvents(CollectorInteractListener, this)
        server.pluginManager.registerEvents(CollectorPlaceListener, this)
        server.pluginManager.registerEvents(CollectorPickupListener, this)
        server.pluginManager.registerEvents(CollectorCollectListener, this)
        server.pluginManager.registerEvents(DeluxeSellWandsListener, this)

        this.messageConfig = PineappleLib.getConfigurationManager().createDefault(File(dataFolder, "messages.yml"), MessageConfig.javaClass)
    }

    override fun onDisable() {
        CollectorManager.obtainAll().forEach { Collector.save(it.position.toLocation().chunk, it) }
        PineappleLib.cleanup() // This must be last!
    }

    private fun saveResources() {
        saveResource("collector-templates.json", false)
        saveResource("collector-menu.json", false)
    }

    fun reloadMessages() {
        this.messageConfig.load()
    }

}
