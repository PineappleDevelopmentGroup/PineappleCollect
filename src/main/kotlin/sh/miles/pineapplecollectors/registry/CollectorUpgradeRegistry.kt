package sh.miles.pineapplecollectors.registry

import sh.miles.pineapplecollectors.upgrades.CollectorUpgrade
import sh.miles.pineapple.collection.registry.WriteableRegistry
import sh.miles.pineapplecollectors.upgrades.CollectorUpgradeLoader

object CollectorUpgradeRegistry : WriteableRegistry<CollectorUpgrade, String>() {

    init {
        CollectorUpgradeLoader().load()
        println(keys())
    }
}