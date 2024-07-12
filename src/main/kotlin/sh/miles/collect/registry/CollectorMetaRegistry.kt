package sh.miles.collect.registry

import sh.miles.collect.meta.CollectorMeta
import sh.miles.pineapple.collection.registry.WriteableRegistry

object CollectorMetaRegistry : WriteableRegistry<CollectorMeta, String>() {
}