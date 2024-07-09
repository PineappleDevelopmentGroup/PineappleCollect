package sh.miles.collect

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import sh.miles.collect.hook.SkyblockHook
import sh.miles.collect.hook.VaultHook
import sh.miles.collect.test.TestTileType
import sh.miles.crown.infstacks.InfStackSettings
import sh.miles.crown.tiles.Tiles
import sh.miles.pineapple.PineappleLib

class CollectPlugin : JavaPlugin(), Listener {

    override fun onEnable() {
        PineappleLib.initialize(this)
        Tiles.setup(this)
        Tiles.getTileTypeRegistry().register(TestTileType)

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
