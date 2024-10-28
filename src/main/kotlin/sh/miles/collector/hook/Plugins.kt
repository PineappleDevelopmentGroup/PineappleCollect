package sh.miles.collector.hook

import sh.miles.collector.CollectorPlugin
import sh.miles.collector.hook.economy.EconomySupportedPlugin
import sh.miles.collector.hook.economy.VaultPluginHook
import sh.miles.collector.hook.shop.EconomyShopPluginHook
import sh.miles.collector.hook.shop.ShopSupportedPlugin
import sh.miles.collector.hook.stacking.RoseStackerPluginSupport
import sh.miles.collector.hook.stacking.StackingSupportedPlugin
import sh.miles.pineapple.collection.registry.WriteableRegistry
import sh.miles.pineapple.function.Option

object Plugins : WriteableRegistry<SupportedPlugin, String>() {
    const val SHOP = "shop"
    const val ECONOMY = "economy"
    const val STACKING = "stacking"

    init {
        // Shop
        support(EconomyShopPluginHook)

        // Economy
        support(VaultPluginHook)

        // Stacking
        support(RoseStackerPluginSupport)
    }

    fun shop(): Option<ShopSupportedPlugin> {
        return get(SHOP).map { it as ShopSupportedPlugin }
    }

    fun shopOrThrow(): ShopSupportedPlugin {
        return shop().orThrow("You must have a shop plugin installed to use Collectors")
    }

    fun economy(): Option<EconomySupportedPlugin> {
        return get(ECONOMY).map { it as EconomySupportedPlugin }
    }

    fun economyOrThrow(): EconomySupportedPlugin {
        return economy().orThrow("You must have a shop plugin installed to use Collectors")
    }

    fun stacking(): Option<StackingSupportedPlugin> {
        return get(STACKING).map { it as StackingSupportedPlugin }
    }

    private fun support(hook: SupportedPlugin) {
        if (hook.load(CollectorPlugin.plugin)) {
            if (!register(hook)) {
                CollectorPlugin.plugin.logger.warning(
                    "Overlap of hook type ${hook.key} between ${hook.name} and ${
                        getOrNull(
                            hook.key
                        ) ?: "N/A"
                    } this should be avoided!"
                )
            } else {
                CollectorPlugin.plugin.logger.info(
                    "Registered plugin hook from plugin ${hook.name} for hook type ${hook.key}"
                )
            }
        }
    }

}
