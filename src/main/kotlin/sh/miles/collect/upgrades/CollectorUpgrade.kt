package sh.miles.collect.upgrades

import com.bgsoftware.superiorskyblock.api.upgrades.Upgrade
import sh.miles.pineapple.collection.registry.RegistryKey

data class CollectorUpgrade(val id: String, val upgrades: List<Upgrade>) : RegistryKey<String> {
    override fun getKey(): String {
        return id
    }
}