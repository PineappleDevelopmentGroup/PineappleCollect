package sh.miles.collector.configuration.registry

import org.bukkit.NamespacedKey
import sh.miles.collector.configuration.UpgradeConfiguration
import sh.miles.pineapple.collection.registry.WriteableRegistry

object UpgradeConfigurationRegistry : WriteableRegistry<UpgradeConfiguration, NamespacedKey>()
