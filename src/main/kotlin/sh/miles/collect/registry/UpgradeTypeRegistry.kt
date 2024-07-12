package sh.miles.collect.registry

import sh.miles.collect.upgrades.type.Upgrade
import sh.miles.pineapple.collection.registry.WriteableRegistry

object UpgradeTypeRegistry : WriteableRegistry<Upgrade, String>() {
}