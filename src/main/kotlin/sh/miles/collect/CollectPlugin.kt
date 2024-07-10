package sh.miles.collect

import com.google.gson.GsonBuilder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import sh.miles.collect.hook.VaultHook
import sh.miles.collect.meta.CollectorMetaAdapter
import sh.miles.collect.test.TestTileType
import sh.miles.collect.upgrades.CollectorUpgradeAdapter
import sh.miles.crown.tiles.Tiles
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapterRegistry
import sh.miles.pineapple.util.serialization.bridges.gson.GsonSerializedBridge

class CollectPlugin : JavaPlugin(), Listener {

    override fun onEnable() {
        PineappleLib.initialize(this)
        Tiles.setup(this)
        Tiles.getTileTypeRegistry().register(TestTileType)
        SerializedAdapterRegistry.INSTANCE.registerBridge(GsonSerializedBridge(GsonBuilder().setPrettyPrinting()))
        SerializedAdapterRegistry.INSTANCE.register(CollectorMetaAdapter)
        SerializedAdapterRegistry.INSTANCE.register(CollectorUpgradeAdapter)

        // Initialize Plugin Hooks
        VaultHook

        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        Tiles.cleanup()
        println("Disabled Collect")
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.player.inventory.addItem(TestTileType.TestTileItemFactory.create())
    }
}
