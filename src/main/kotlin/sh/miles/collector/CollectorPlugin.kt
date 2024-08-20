package sh.miles.collector

import org.bukkit.plugin.java.JavaPlugin
import sh.miles.collector.command.CollectorCommand
import sh.miles.collector.configuration.adapter.CollectorConfigurationAdapter
import sh.miles.collector.configuration.adapter.MenuConfigurationAdapter
import sh.miles.collector.tile.CollectorTileType
import sh.miles.collector.util.spec.adapter.HologramSpecAdapter
import sh.miles.collector.util.spec.adapter.InfStackSettingsAdapter
import sh.miles.collector.util.spec.adapter.SoundSpecAdapter
import sh.miles.collector.util.spec.adapter.VectorSpecAdapter
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.json.JsonHelper
import sh.miles.pineapple.tiles.api.Tiles
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapterRegistry
import sh.miles.pineapple.util.serialization.bridges.gson.GsonSerializedBridge

class CollectorPlugin : JavaPlugin() {

    companion object {
        lateinit var jsonHelper: JsonHelper
            private set
    }

    override fun onEnable() {
        PineappleLib.initialize(this)
        Tiles.setup(this)
        Tiles.getInstance().registerTileType(CollectorTileType)
        setupSerializer()
        Registries.load(this, jsonHelper)

        PineappleLib.getCommandRegistry().register(CollectorCommand)
    }

    override fun onDisable() {
        PineappleLib.cleanup()
        Tiles.cleanup()
    }

    private fun setupSerializer() {
        val registry = SerializedAdapterRegistry.INSTANCE
        // registry
        registry.register(CollectorConfigurationAdapter)
        registry.register(MenuConfigurationAdapter)
        // spec
        registry.register(SoundSpecAdapter)
        registry.register(VectorSpecAdapter)
        registry.register(HologramSpecAdapter)
        registry.register(InfStackSettingsAdapter)

        jsonHelper = JsonHelper {
            registry.registerBridge(GsonSerializedBridge(it))
        }
    }
}
