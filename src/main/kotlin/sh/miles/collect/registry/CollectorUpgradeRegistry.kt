package sh.miles.collect.registry

import sh.miles.collect.upgrades.CollectorUpgrade
import sh.miles.pineapple.collection.registry.WriteableRegistry

object CollectorUpgradeRegistry : WriteableRegistry<CollectorUpgrade, String>() {
}