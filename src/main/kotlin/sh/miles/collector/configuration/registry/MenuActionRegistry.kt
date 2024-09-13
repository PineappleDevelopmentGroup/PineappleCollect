package sh.miles.collector.configuration.registry

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Display
import sh.miles.collector.GlobalConfig
import sh.miles.collector.Registries
import sh.miles.collector.hook.VaultHook
import sh.miles.collector.menu.AnvilTextMenu
import sh.miles.collector.menu.CollectorMenu
import sh.miles.collector.menu.CollectorSellMenu
import sh.miles.collector.util.ClickAction
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.collection.registry.FrozenRegistry
import java.util.concurrent.CompletableFuture

@Deprecated(
    message = "Replaced by GeneralMenuActionRegistry",
    replaceWith = ReplaceWith(expression = "GeneralMenuActionRegistry"),
    level = DeprecationLevel.WARNING
)
object MenuActionRegistry : FrozenRegistry<ClickAction, String>({
    mapOf("open_sub_menu" to ClickAction("open_sell_gui") { tile, lastOpenMenu, _, id, player, event ->
        event.isCancelled = true
        if (id == null) throw IllegalStateException("When using action \"open_sub_menu\" a link field must be provided")
        val sellConfiguration = Registries.SELL_MENU.getOrNull(id)
        if (sellConfiguration != null) {
            CollectorSellMenu(player, lastOpenMenu, tile, sellConfiguration).open()
        } else {
            CollectorMenu(player, lastOpenMenu, tile, Registries.MENU.get(id).orThrow()).open()
        }
    }, "add_autosell" to ClickAction("add_autosell") { tile, _, _, link, player, event ->
        event.isCancelled = true
        val upgrade = Registries.UPGRADE.get(Registries.UPGRADE_ACTION.AUTO_SELL).orThrow()
        val curUpgrades = tile.upgrades
        if (curUpgrades.containsKey(upgrade) && tile.upgrades[upgrade]!! == upgrade.level.size) {
            player.spigot().sendMessage(
                GlobalConfig.ALREADY_HAVE_UPGRADE.component(
                    mutableMapOf<String, Any>(
                        "upgrade" to upgrade.action.internalName, "level" to upgrade.level.size
                    )
                )
            )
            return@ClickAction
        }

        val cost = link!!.toDouble()
        if (!VaultHook.hasBalance(player, cost)) {
            player.spigot().sendMessage(
                GlobalConfig.NOT_ENOUGH_MONEY.component(
                    mutableMapOf<String, Any>(
                        "upgrade" to upgrade.action.internalName,
                        "player_balance" to VaultHook.getBalannce(player),
                        "upgrade_cost" to cost
                    )
                )
            )
            return@ClickAction
        }

        VaultHook.removeBalance(player, cost)
        player.spigot().sendMessage(
            GlobalConfig.UPGRADE_PURCHASED.component(
                mutableMapOf<String, Any>(
                    "upgrade" to upgrade.action.internalName, "price" to cost
                )
            )
        )
        curUpgrades[upgrade] = curUpgrades.getOrDefault(upgrade, 0) + 1
    }, "toggle_hologram" to ClickAction("toggle_hologram") { tile, _, _, _, _, event ->
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
    }, "add_accessor" to ClickAction("add_accessor") { tile, lastOpenMenu, _, _, player, event ->
        event.isCancelled = true
        AnvilTextMenu(player, lastOpenMenu) {
            CompletableFuture.runAsync {
                val maybePlayer = Bukkit.getOfflinePlayer(it)
                tile.accessWhitelist.add(maybePlayer.uniqueId)
            }.whenComplete { _, exception ->
                if (exception != null) {
                    player.spigot().sendMessage(
                        GlobalConfig.OFFLINE_PLAYER_WEB_REQUEST_FAILED.component(
                            mutableMapOf<String, Any>(
                                "name" to it
                            )
                        )
                    )
                }
            }
        }.open()
    }, "remove_accessor" to ClickAction("remove_accessor") { tile, lastOpenMenu, _, _, player, event ->
        event.isCancelled = true
        AnvilTextMenu(player, lastOpenMenu) {
            CompletableFuture.runAsync {
                val maybePlayer = Bukkit.getOfflinePlayer(it)
                tile.accessWhitelist.remove(maybePlayer.uniqueId)
            }.whenComplete { _, exception ->
                if (exception != null) {
                    player.spigot().sendMessage(
                        GlobalConfig.OFFLINE_PLAYER_WEB_REQUEST_FAILED.component(
                            mutableMapOf<String, Any>(
                                "name" to it
                            )
                        )
                    )
                }
            }
        }.open()
    }, "show_accessors" to ClickAction("show_accessors") { tile, _, slot, format, _, event ->
        event.isCancelled = true
        if (format == null) throw IllegalStateException("When using action \"open_sub_menu\" a link field must be provided")
        val inventory = event.clickedInventory!!
        val item = inventory.getItem(slot)!!
        val lore = PineappleLib.getNmsProvider().getItemLore(item)
        for (uuid in tile.accessWhitelist) {
            lore.add(
                PineappleChat.parse(
                    format, mutableMapOf<String, Any>(
                        "name" to (Bukkit.getOfflinePlayer(uuid).name ?: "$uuid")
                    )
                )
            )
        }
        inventory.setItem(slot, PineappleLib.getNmsProvider().setItemLore(item, lore))
    })
})
