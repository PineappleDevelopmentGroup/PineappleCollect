package sh.miles.collector

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import sh.miles.collector.command.CollectorCommand
import sh.miles.collector.configuration.adapter.CollectorConfigurationAdapter
import sh.miles.collector.configuration.adapter.MenuConfigurationAdapter
import sh.miles.collector.configuration.adapter.SellMenuConfigurationAdapter
import sh.miles.collector.configuration.adapter.UpgradeConfigurationAdapter
import sh.miles.collector.hook.Plugins
import sh.miles.collector.listener.EntityDeathListener
import sh.miles.collector.tile.CollectorTileType
import sh.miles.collector.util.spec.adapter.GuiItemSpecAdapter
import sh.miles.collector.util.spec.adapter.InfStackSettingsAdapter
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.json.JsonHelper
import sh.miles.pineapple.tiles.api.Tiles
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapterRegistry
import sh.miles.pineapple.util.serialization.bridges.gson.GsonSerializedBridge
import java.io.File

class CollectorPlugin : JavaPlugin() {

    companion object {
        lateinit var jsonHelper: JsonHelper
            private set
        lateinit var plugin: Plugin
            private set
    }

    override fun onEnable() {
        plugin = this;
        PineappleLib.initialize(this)
        Tiles.setup(this)
        Tiles.getInstance().registerTileType(CollectorTileType)
        setupSerializer()
        Registries.load(this, jsonHelper)
        PineappleLib.getConfigurationManager().createDefault(File(dataFolder, "config.yml"), GlobalConfig::class.java)

        PineappleLib.getCommandRegistry().register(CollectorCommand)

        Plugins
        server.pluginManager.registerEvents(EntityDeathListener(), this)
        Tiles.getInstance().loadSpawnChunks() // We need this since we load PostWorld
    }

    override fun onDisable() {
        Tiles.cleanup()
        PineappleLib.cleanup()
    }

    private fun setupSerializer() {
        val registry = SerializedAdapterRegistry.INSTANCE
        // registry
        registry.register(CollectorConfigurationAdapter)
        registry.register(SellMenuConfigurationAdapter)
        registry.register(MenuConfigurationAdapter)
        registry.register(UpgradeConfigurationAdapter)
        // spec
        registry.register(GuiItemSpecAdapter)
        registry.register(InfStackSettingsAdapter)

        jsonHelper = JsonHelper {
            registry.registerBridge(GsonSerializedBridge(it))
        }
    }
}
