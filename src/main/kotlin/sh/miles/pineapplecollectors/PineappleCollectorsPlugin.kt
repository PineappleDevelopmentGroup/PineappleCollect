package sh.miles.pineapplecollectors

import org.bukkit.plugin.java.JavaPlugin
import sh.miles.pineapplecollectors.hook.PlaceholderAPIHook
import sh.miles.pineapplecollectors.hook.VaultHook
import sh.miles.pineapplecollectors.meta.CollectorMetaAdapter
import sh.miles.pineapplecollectors.registry.CollectorMetaRegistry
import sh.miles.pineapplecollectors.test.TestTileType
import sh.miles.pineapplecollectors.upgrades.CollectorUpgradeAdapter
import sh.miles.crown.tiles.Tiles
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.json.JsonHelper
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapterRegistry
import sh.miles.pineapple.util.serialization.bridges.gson.GsonSerializedBridge
import sh.miles.pineapplecollectors.registry.CollectorUpgradeRegistry

class PineappleCollectorsPlugin : JavaPlugin() {

    lateinit var json: JsonHelper

    override fun onEnable() {
        plugin = this
        PineappleLib.initialize(this)
        Tiles.setup(this)
        Tiles.getTileTypeRegistry().register(TestTileType)
        json = JsonHelper { builder ->
            SerializedAdapterRegistry.INSTANCE.registerBridge(GsonSerializedBridge(builder))
        }
        SerializedAdapterRegistry.INSTANCE.register(CollectorMetaAdapter)
        SerializedAdapterRegistry.INSTANCE.register(CollectorUpgradeAdapter)


        // Initialize Plugin Hooks
        VaultHook
        PlaceholderAPIHook

        // Initialize Registries
        CollectorMetaRegistry
        CollectorUpgradeRegistry
    }

    override fun onDisable() {
        Tiles.cleanup()
        println("Disabled Collect")
    }

    companion object {
        lateinit var plugin: PineappleCollectorsPlugin
    }

}
