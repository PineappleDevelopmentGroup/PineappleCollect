package sh.miles.collect.meta

import sh.miles.pineapple.collection.registry.RegistryKey

data class CollectorMeta(val id: String) : RegistryKey<String> {

    override fun getKey(): String {
        return id
    }
}
