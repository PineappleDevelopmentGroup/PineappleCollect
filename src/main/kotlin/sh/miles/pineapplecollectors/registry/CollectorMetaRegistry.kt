package sh.miles.pineapplecollectors.registry

import sh.miles.pineapplecollectors.meta.CollectorMeta
import sh.miles.pineapplecollectors.meta.CollectorMetaLoader
import sh.miles.pineapple.collection.registry.WriteableRegistry

object CollectorMetaRegistry : WriteableRegistry<CollectorMeta, String>() {

    init {
        CollectorMetaLoader().load()
        println(keys())
    }
}