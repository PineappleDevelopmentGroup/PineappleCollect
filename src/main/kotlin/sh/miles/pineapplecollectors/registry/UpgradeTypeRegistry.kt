package sh.miles.pineapplecollectors.registry

import sh.miles.pineapplecollectors.upgrades.type.Upgrade
import sh.miles.pineapple.collection.registry.WriteableRegistry

object UpgradeTypeRegistry : WriteableRegistry<Upgrade, String>() {
}