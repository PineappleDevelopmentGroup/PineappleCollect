package sh.miles.collector

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import sh.miles.collector.command.CollectorCommand
import sh.miles.collector.configuration.adapter.CollectorConfigurationAdapter
import sh.miles.collector.configuration.adapter.MainMenuConfigurationAdapter
import sh.miles.collector.configuration.adapter.SellMenuConfigurationAdapter
import sh.miles.collector.hook.EconomyShopHook
import sh.miles.collector.hook.VaultHook
import sh.miles.collector.tile.CollectorTileType
import sh.miles.collector.util.spec.adapter.GuiItemSpecAdapter
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

        PineappleLib.getCommandRegistry().register(CollectorCommand)

        VaultHook
        EconomyShopHook
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
        registry.register(MainMenuConfigurationAdapter)
        // spec
        registry.register(GuiItemSpecAdapter)
        registry.register(SoundSpecAdapter)
        registry.register(VectorSpecAdapter)
        registry.register(HologramSpecAdapter)
        registry.register(InfStackSettingsAdapter)

        jsonHelper = JsonHelper {
            registry.registerBridge(GsonSerializedBridge(it))
        }
    }
}
