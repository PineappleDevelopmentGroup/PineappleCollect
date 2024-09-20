package sh.miles.collector

import org.bukkit.plugin.Plugin
import sh.miles.collector.configuration.loader.CollectorConfigurationLoader
import sh.miles.collector.configuration.loader.MenuConfigurationLoader
import sh.miles.collector.configuration.loader.SellMenuConfigurationLoader
import sh.miles.collector.configuration.loader.UpgradeConfigurationLoader
import sh.miles.collector.configuration.registry.CollectorConfigurationRegistry
import sh.miles.collector.configuration.registry.GeneralMenuActionRegistry
import sh.miles.collector.configuration.registry.MenuConfigurationRegistry
import sh.miles.collector.configuration.registry.SellMenuConfigurationRegistry
import sh.miles.collector.configuration.registry.UpgradeConfigurationRegistry
import sh.miles.collector.upgrade.action.UpgradeActionRegistry
import sh.miles.pineapple.json.JsonHelper

object Registries {
    val COLLECTOR = CollectorConfigurationRegistry
    val MENU = MenuConfigurationRegistry
    val UPGRADE = UpgradeConfigurationRegistry
    val SELL_MENU = SellMenuConfigurationRegistry
    val UPGRADE_ACTION = UpgradeActionRegistry
    val GENERAL_MENU_ACTION = GeneralMenuActionRegistry

    fun load(plugin: Plugin, jsonHelper: JsonHelper) {
        CollectorConfigurationLoader(jsonHelper, plugin).load()
        SellMenuConfigurationLoader(jsonHelper, plugin).load()
        MenuConfigurationLoader(jsonHelper, plugin).load()
        UpgradeConfigurationLoader(jsonHelper, plugin).load()
    }
}
