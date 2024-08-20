package sh.miles.pineapplecollectors.upgrades

import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapplecollectors.upgrades.type.Upgrade

data class CollectorUpgrade(private val id: String, private val upgrades: MutableList<Upgrade> = mutableListOf()) : RegistryKey<String> {
    override fun getKey(): String {
        return id
    }

    fun removeUpgrade(upgrade: Upgrade) {
        upgrades.remove(upgrade)
    }

    fun addUpgrade(upgrade: Upgrade) {
        upgrades.add(upgrade)
    }

    fun getUpgrades(): List<Upgrade> {
        return ArrayList(upgrades)
    }
}