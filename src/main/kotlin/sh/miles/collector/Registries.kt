package sh.miles.collector

import org.bukkit.plugin.Plugin
import sh.miles.collector.configuration.loader.CollectorConfigurationLoader
import sh.miles.collector.configuration.loader.MainMenuConfigurationLoader
import sh.miles.collector.configuration.loader.SellMenuConfigurationLoader
import sh.miles.collector.configuration.registry.CollectorConfigurationRegistry
import sh.miles.collector.configuration.registry.MainMenuConfigurationRegistry
import sh.miles.collector.configuration.registry.SellMenuConfigurationRegistry
import sh.miles.collector.configuration.registry.MainMenuActionRegistry
import sh.miles.collector.upgrade.CollectorUpgradeActionRegistry
import sh.miles.pineapple.json.JsonHelper

object Registries {
    val COLLECTOR = CollectorConfigurationRegistry
    val MAIN_MENU = MainMenuConfigurationRegistry
    val MAIN_MENU_ACTION = MainMenuActionRegistry
    val SELL_MENU = SellMenuConfigurationRegistry
    val UPGRADE = CollectorUpgradeActionRegistry

    fun load(plugin: Plugin, jsonHelper: JsonHelper) {
        CollectorConfigurationLoader(jsonHelper, plugin).load()
        SellMenuConfigurationLoader(jsonHelper, plugin).load()
        MainMenuConfigurationLoader(jsonHelper, plugin).load()
    }
}
