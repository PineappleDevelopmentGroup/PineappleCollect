package sh.miles.collector

import org.bukkit.plugin.Plugin
import sh.miles.collector.configuration.loader.CollectorConfigurationLoader
import sh.miles.collector.configuration.loader.MenuConfigurationLoader
import sh.miles.collector.configuration.registry.CollectorConfigurationRegistry
import sh.miles.collector.configuration.registry.MenuConfigurationRegistry
import sh.miles.collector.upgrade.CollectorUpgradeRegistry
import sh.miles.pineapple.json.JsonHelper

object Registries {
    val COLLECTOR = CollectorConfigurationRegistry
    val MENU = MenuConfigurationRegistry
    val UPGRADE = CollectorUpgradeRegistry

    fun load(plugin: Plugin, jsonHelper: JsonHelper) {
        CollectorConfigurationLoader(jsonHelper, plugin).load()
        MenuConfigurationLoader(jsonHelper, plugin).load()
    }
}
