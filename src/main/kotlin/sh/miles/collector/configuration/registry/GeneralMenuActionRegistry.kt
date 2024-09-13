package sh.miles.collector.configuration.registry

import sh.miles.collector.Registries
import sh.miles.collector.menu.CollectorMenu
import sh.miles.collector.menu.CollectorSellMenu
import sh.miles.collector.util.MenuAction
import sh.miles.pineapple.collection.registry.FrozenRegistry

object GeneralMenuActionRegistry : FrozenRegistry<MenuAction, String>({
    listOf(
        MenuAction("open_sub_menu") { data, event ->
            event.isCancelled = true
            if (data.providedArgs.isEmpty()) throw IllegalStateException("When using action \"open_sub_menu\" a args field must be provided")
            val id = data.providedArgs[0]
            val sellConfiguration = Registries.SELL_MENU.getOrNull(id)
            if (sellConfiguration != null) {
                CollectorSellMenu(data.viewer, data.lastOpenMenu, data.tile, sellConfiguration).open()
            } else {
                CollectorMenu(data.viewer, data.lastOpenMenu, data.tile, Registries.MENU.get(id).orThrow()).open()
            }
        },
        MenuAction("upgrade") { data, event ->

        }
    ).associateBy { it.id }
})
