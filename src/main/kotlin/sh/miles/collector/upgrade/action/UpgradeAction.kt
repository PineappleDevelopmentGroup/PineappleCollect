package sh.miles.collector.upgrade.action

import sh.miles.collector.configuration.UpgradeConfiguration
import sh.miles.collector.tile.CollectorTile
import sh.miles.collector.tile.event.SellActionEvent
import sh.miles.collector.upgrade.level.UpgradeLevel
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.util.serialization.SerializedObject

interface UpgradeAction : RegistryKey<String> {
    val internalName: String

    fun onTick(tile: CollectorTile, upgrade: UpgradeConfiguration, level: Int) {
    }

    fun onSell(event: SellActionEvent, upgrade: UpgradeConfiguration, level: Int) {
    }

    fun createLevel(parent: SerializedObject): UpgradeLevel {
        return createLevel(
            parent,
            parent.getPrimitive("level").orThrow().asInt,
            parent.getPrimitive("price").orThrow().asDouble
        )
    }

    fun createLevel(parent: SerializedObject, level: Int, price: Double): UpgradeLevel

    fun zeroLevel(): UpgradeLevel
}
