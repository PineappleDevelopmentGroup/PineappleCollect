package io.github.pineappledevelopmentgroup.registry

import io.github.pineappledevelopmentgroup.collector.CollectorMeta
import sh.miles.pineapple.collection.registry.WriteableRegistry

class CollectorMetaRegistry : WriteableRegistry<CollectorMeta, String>() {

    init {
        register(CollectorMeta.Singleton)
    }

}
