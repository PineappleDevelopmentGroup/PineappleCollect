package sh.miles.pineapplecollectors.meta

import sh.miles.pineapple.collection.registry.RegistryKey

data class CollectorMeta(private val id: String) : RegistryKey<String> {

    override fun getKey(): String {
        return id
    }
}
