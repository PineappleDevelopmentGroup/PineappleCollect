package sh.miles.collector.configuration.registry

import sh.miles.collector.configuration.CollectorConfiguration
import sh.miles.pineapple.collection.registry.WriteableRegistry

object CollectorConfigurationRegistry : WriteableRegistry<CollectorConfiguration, String>()
