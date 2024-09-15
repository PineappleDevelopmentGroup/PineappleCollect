package sh.miles.collector.configuration.registry

import org.bukkit.NamespacedKey
import sh.miles.collector.GlobalConfig
import sh.miles.collector.Registries
import sh.miles.collector.hook.VaultHook
import sh.miles.collector.menu.CollectorMenu
import sh.miles.collector.menu.CollectorSellMenu
import sh.miles.collector.upgrade.level.UpgradeLevel
import sh.miles.collector.util.MenuAction
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.collection.registry.FrozenRegistry
import sh.miles.pineapple.item.ItemSpec

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
        val currentLevel = tile.upgrades.getOrDefault(upgrade, 0)
        if (currentLevel >= upgrade.maxLevel) {
            data.viewer.spigot().sendMessage(
                GlobalConfig.ALREADY_HAVE_UPGRADE.component(
                    mutableMapOf<String, Any>(
                        "upgrade" to upgrade.action.internalName, "level" to currentLevel
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

        VaultHook.removeBalance(data.viewer, cost)
        data.viewer.spigot().sendMessage(
            GlobalConfig.UPGRADE_PURCHASED.component(
                mutableMapOf<String, Any>(
                    "upgrade" to upgrade.action.internalName, "price" to cost
                )
            )
        )

        data.tile.upgrades[upgrade] = currentLevel + 1
    }, { data, event -> // open
        if (data.providedArgs.isEmpty()) throw IllegalStateException("When using action \"upgrade\" a args field must be provided")
        val upgrade = Registries.UPGRADE.get(NamespacedKey.fromString("pineapple-collect:${data.providedArgs[0]}")!!)
            .orThrow("A valid upgrade key must be provided, however ${data.providedArgs[0]} is not a supported upgrade key")
        val currentLevel = data.tile.upgrades.getOrDefault(upgrade, 1)
        val level: UpgradeLevel
        val icon: ItemSpec
        if (currentLevel >= upgrade.maxLevel) {
            level = upgrade.level[upgrade.maxLevel]
            icon = ItemSpec(upgrade.maxIcon)
        } else {
            icon = ItemSpec(upgrade.icon)
        }

        icon.setDefaultTextMutator { PineappleChat.parse(it) }
        icon.setLoreMutator { level.loreMutator(it, currentLevel) }
    }, { data, event -> // close
    })
    ).associateBy { it.id }
})
