package sh.miles.pineapplecollectors

import org.bukkit.plugin.java.JavaPlugin
import sh.miles.crown.tiles.Tiles
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.json.JsonHelper
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapterRegistry
import sh.miles.pineapple.util.serialization.bridges.gson.GsonSerializedBridge
import sh.miles.pineapplecollectors.collector.tile.CollectorTileType
import sh.miles.pineapplecollectors.command.CollectCommandHandler
import sh.miles.pineapplecollectors.hook.PlaceholderAPIHook
import sh.miles.pineapplecollectors.hook.VaultHook
import sh.miles.pineapplecollectors.meta.CollectorMetaAdapter
import sh.miles.pineapplecollectors.registry.CollectorMetaRegistry
import sh.miles.pineapplecollectors.registry.CollectorUpgradeRegistry
import sh.miles.pineapplecollectors.upgrades.CollectorUpgradeAdapter

class PineappleCollectorsPlugin : JavaPlugin() {

    lateinit var json: JsonHelper

    override fun onEnable() {
        plugin = this
        PineappleLib.initialize(this)
        Tiles.setup(this)
        Tiles.getTileTypeRegistry().register(CollectorTileType)
        json = JsonHelper { builder ->
            SerializedAdapterRegistry.INSTANCE.registerBridge(GsonSerializedBridge(builder))
            SerializedAdapterRegistry.INSTANCE.register(CollectorMetaAdapter)
            SerializedAdapterRegistry.INSTANCE.register(CollectorUpgradeAdapter)
        }

        // Initialize Plugin Hooks
        VaultHook
        PlaceholderAPIHook

        // Initialize Registries
        CollectorMetaRegistry
        CollectorUpgradeRegistry

        // Initialize Commands
        PineappleLib.getCommandRegistry().register(CollectCommandHandler)
        PineappleLib.getCommandRegistry().registerInternalCommands()
    }

    override fun onDisable() {
        Tiles.cleanup()
        println("Disabled Collect")
    }

    companion object {
        lateinit var plugin: PineappleCollectorsPlugin
    }

}
