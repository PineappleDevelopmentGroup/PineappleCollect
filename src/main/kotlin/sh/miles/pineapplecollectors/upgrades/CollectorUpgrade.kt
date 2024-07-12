package sh.miles.pineapplecollectors.upgrades

import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapplecollectors.upgrades.type.Upgrade

data class CollectorUpgrade(val id: String, val upgrades: List<Upgrade>) : RegistryKey<String> {
    override fun getKey(): String {
        return id
    }
}