package sh.miles.collector.configuration.registry

import org.bukkit.Bukkit
import org.bukkit.entity.Display
import sh.miles.collector.Registries
import sh.miles.collector.menu.AnvilTextMenu
import sh.miles.collector.menu.CollectorSellMenu
import sh.miles.collector.util.ClickAction
import sh.miles.pineapple.collection.registry.FrozenRegistry
import java.util.concurrent.CompletableFuture

object MainMenuActionRegistry : FrozenRegistry<ClickAction, String>({
    mapOf("open_sell_gui" to ClickAction("open_sell_gui") { tile, id, player, event ->
        event.isCancelled = true
        CollectorSellMenu(player, tile, Registries.SELL_MENU.get(id).orThrow()).open()
    }, "toggle_hologram" to ClickAction("toggle_hologram") { tile, id, player, event ->
        event.isCancelled = true
        val display = tile.textDisplayUUID
        if (display == null) {
            tile.textDisplayUUID = tile.configuration.hologram.spawn(tile.location!!) {
                it.billboard = Display.Billboard.CENTER
            }.uniqueId
        } else {
            val entity = Bukkit.getEntity(display)
                ?: throw IllegalStateException("For some reason a hologram doesn't exist with uuid $display")
            entity.remove()
            tile.textDisplayUUID = null
        }
    }, "add_accessor" to ClickAction("add_accessor") { tile, _, player, event ->
        event.isCancelled = true
        AnvilTextMenu(player) {
            CompletableFuture.runAsync {
                val maybePlayer = Bukkit.getOfflinePlayer(it)
                tile.accessWhitelist.add(maybePlayer.uniqueId)
            }.whenComplete { _, exception ->
                if (exception != null) {
                    // TODO: Send message to player
                    println("We should send a message to the player here that makes sense")
                }
            }
        }.open()
    }, "remove_accessor" to ClickAction("remove_accessor") { tile, _, player, event ->
        event.isCancelled = true
        AnvilTextMenu(player) {
            CompletableFuture.runAsync {
                val maybePlayer = Bukkit.getOfflinePlayer(it)
                tile.accessWhitelist.remove(maybePlayer.uniqueId)
            }.whenComplete { _, exception ->
                if (exception != null) {
                    // TODO: Send message to player
                    println("We should send a message to the player here that makes sense")
                }
            }
        }.open()
    })
})
