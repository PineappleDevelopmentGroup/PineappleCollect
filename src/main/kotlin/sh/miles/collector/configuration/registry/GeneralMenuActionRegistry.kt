package sh.miles.collector.configuration.registry

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Display
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import sh.miles.collector.GlobalConfig
import sh.miles.collector.Registries
import sh.miles.collector.hook.VaultHook
import sh.miles.collector.menu.AnvilTextMenu
import sh.miles.collector.menu.CollectorMenu
import sh.miles.collector.menu.CollectorSellMenu
import sh.miles.collector.upgrade.level.UpgradeLevel
import sh.miles.collector.util.MenuAction
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.collection.registry.FrozenRegistry
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapple.item.ItemSpec
import java.util.concurrent.CompletableFuture

object GeneralMenuActionRegistry : FrozenRegistry<MenuAction, String>({
    listOf(MenuAction("open_sub_menu") { data, event -> // click
        event.isCancelled = true
        if (data.providedArgs.isEmpty()) throw IllegalStateException("When using action \"open_sub_menu\" a args field must be provided")
        val id = data.providedArgs[0]
        val sellConfiguration = Registries.SELL_MENU.getOrNull(id)
        if (sellConfiguration != null) {
            CollectorSellMenu(data.viewer, data.lastOpenMenu, data.tile, sellConfiguration).open()
        } else {
            CollectorMenu(data.viewer, data.lastOpenMenu, data.tile, Registries.MENU.get(id).orThrow()).open()
        }
    }, MenuAction("upgrade", { data, event ->
        event.isCancelled = true
        if (data.providedArgs.isEmpty()) throw IllegalStateException("When using action \"upgrade\" a args field must be provided")
        val upgrade = Registries.UPGRADE.get(NamespacedKey.fromString("pineapple-collect:${data.providedArgs[0]}")!!)
            .orThrow("A valid upgrade key must be provided, however ${data.providedArgs[0]} is not a supported upgrade key")
        val tile = data.tile
        val status = tile.getUpgradeStatus(upgrade)
        val currentLevel = status.first
        var enabled = status.second

        if (event.click == ClickType.LEFT && (currentLevel == 0 || enabled)) {
            if (currentLevel >= upgrade.maxLevel) {
                data.viewer.spigot().sendMessage(
                    GlobalConfig.ALREADY_HAVE_UPGRADE.component(
                        mutableMapOf<String, Any>(
                            "upgrade" to upgrade.action.internalName, "level" to currentLevel + 1
                        )
                    )
                )
                return@MenuAction
            }

            val cost = upgrade.level[currentLevel].price
            if (!VaultHook.hasBalance(data.viewer, cost)) {
                data.viewer.spigot().sendMessage(
                    GlobalConfig.NOT_ENOUGH_MONEY.component(
                        mutableMapOf<String, Any>(
                            "upgrade" to upgrade.action.internalName,
                            "player_balance" to VaultHook.getBalannce(data.viewer),
                            "upgrade_cost" to cost
                        )
                    )
                )
                return@MenuAction
            }

            if (!enabled && currentLevel == 0) {
                enabled = true
            }

            VaultHook.removeBalance(data.viewer, cost)
            data.viewer.spigot().sendMessage(
                GlobalConfig.UPGRADE_PURCHASED.component(
                    mutableMapOf<String, Any>(
                        "upgrade" to upgrade.action.internalName, "price" to cost
                    )
                )
            )
            tile.setUpgradeStatus(upgrade, Pair(currentLevel + 1, enabled))
        } else if (event.click == ClickType.RIGHT) {
            if (currentLevel == 0) return@MenuAction
            tile.setUpgradeStatus(upgrade, Pair(currentLevel, !enabled))
        }

        data.lastOpenMenu?.decorate()
    }, { _, _ -> }, { _, _ -> }, { data, inventory ->
        if (data.providedArgs.isEmpty()) throw IllegalStateException("When using action \"upgrade\" a args field must be provided")
        val upgrade = Registries.UPGRADE.get(NamespacedKey.fromString("pineapple-collect:${data.providedArgs[0]}")!!)
            .orThrow("A valid upgrade key must be provided, however ${data.providedArgs[0]} is not a supported upgrade key")
        val status = data.tile.getUpgradeStatus(upgrade)
        val currentLevel = status.first
        val level: UpgradeLevel
        val nextLevel: UpgradeLevel?
        val icon: ItemSpec
        if (currentLevel == 0) {
            icon = ItemSpec(upgrade.icon)
            level = upgrade.action.zeroLevel()
            nextLevel = upgrade.level[0]
        } else if (!status.second) {
            icon = ItemSpec(upgrade.disabledIcon)
            level =
                if (currentLevel >= upgrade.maxLevel) upgrade.level[upgrade.maxLevel - 1] else upgrade.level[currentLevel - 1]
            nextLevel = if (currentLevel >= upgrade.maxLevel) null else upgrade.level[currentLevel]
        } else if (currentLevel >= upgrade.maxLevel) {
            level = upgrade.level[upgrade.maxLevel - 1]
            nextLevel = null
            icon = ItemSpec(upgrade.maxIcon)
        } else {
            level = upgrade.level[currentLevel - 1]
            nextLevel = upgrade.level[currentLevel]
            icon = ItemSpec(upgrade.icon)
        }

        icon.setDefaultTextMutator {
            level.textMutator(it, currentLevel) { map: MutableMap<String, Any> ->
                if (nextLevel != null) {
                    map.putAll(nextLevel.prefixReplacements("new", 0))
                    map["price"] = map.remove("new_price")!!
                }
                return@textMutator map
            }
        }
        inventory.setItem(data.slot, icon.buildSpec())
    }), MenuAction("toggle_hologram") { data, event ->
        event.isCancelled = true
        val tile = data.tile
        val display = tile.textDisplayUUID
        if (display == null) {
            tile.textDisplayUUID = tile.configuration.hologram.spawn(tile.location!!) {
                it.billboard = Display.Billboard.CENTER
            }.uniqueId
            tile.tileType.tickDisplay(tile)
        } else {
            val entity = Bukkit.getEntity(display)
                ?: throw IllegalStateException("For some reason a hologram doesn't exist with uuid $display")
            entity.remove()
            tile.textDisplayUUID = null
        }
    }, MenuAction("add_accessor") { data, event ->
        event.isCancelled = true
        AnvilTextMenu(data.viewer, ItemStack(Material.PAPER), data.lastOpenMenu) {
            CompletableFuture.runAsync {
                val maybePlayer = Bukkit.getOfflinePlayer(it)
                data.tile.accessWhitelist.add(maybePlayer.uniqueId)
            }.whenComplete { _, exception ->
                if (exception != null) {
                    data.viewer.spigot().sendMessage(
                        GlobalConfig.OFFLINE_PLAYER_WEB_REQUEST_FAILED.component(
                            mutableMapOf<String, Any>(
                                "name" to it
                            )
                        )
                    )
                }
            }
        }.open()
    }, MenuAction("remove_accessor") { data, event ->
        event.isCancelled = true
        if (data.providedArgs.isEmpty()) throw IllegalStateException("When using action \"remove_accessor\" a args field must be provided")
        val format = data.providedArgs[0]
        val item = ItemBuilder.of(Material.PAPER)
        val lore = mutableListOf<BaseComponent>()
        for (uuid in data.tile.accessWhitelist) {
            val player = Bukkit.getOfflinePlayer(uuid)
            lore.add(PineappleChat.parse(format, mutableMapOf<String, Any>("name" to (player.name ?: "Unknown"))))
        }
        AnvilTextMenu(data.viewer, item.build(), data.lastOpenMenu) {
            CompletableFuture.runAsync {
                val maybePlayer = Bukkit.getOfflinePlayer(it)
                data.tile.accessWhitelist.remove(maybePlayer.uniqueId)
            }.whenComplete { _, exception ->
                if (exception != null) {
                    data.viewer.spigot().sendMessage(
                        GlobalConfig.OFFLINE_PLAYER_WEB_REQUEST_FAILED.component(
                            mutableMapOf<String, Any>(
                                "name" to it
                            )
                        )
                    )
                }
            }
        }.open()
    }, MenuAction("show_accessors", { _, event ->
        event.isCancelled = true
    }, { _, _ -> }, { _, _ -> }, { data, inventory ->
        if (data.providedArgs.isEmpty()) throw IllegalStateException("When using action \"show_accessors\" a args field must be provided")
        val item = inventory.getItem(data.slot)!!
        val lore = PineappleLib.getNmsProvider().getItemLore(item)
        for (uuid in data.tile.accessWhitelist) {
            lore.add(
                PineappleChat.parse(
                    data.providedArgs[0], mutableMapOf<String, Any>(
                        "name" to (Bukkit.getOfflinePlayer(uuid).name ?: "$uuid")
                    )
                )
            )
        }
        inventory.setItem(data.slot, PineappleLib.getNmsProvider().setItemLore(item, lore))
    })
    ).associateBy { it.id }
})
