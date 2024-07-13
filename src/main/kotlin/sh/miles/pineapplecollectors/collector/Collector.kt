package sh.miles.pineapplecollectors.collector

import sh.miles.pineapplecollectors.meta.CollectorMeta
import sh.miles.pineapplecollectors.upgrades.CollectorUpgrade

class Collector(private val meta: CollectorMeta, private val upgrades: MutableList<CollectorUpgrade>) {
}