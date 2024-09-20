package sh.miles.collector.tile.loader

import org.bukkit.persistence.PersistentDataContainer
import sh.miles.collector.tile.loader.fixes.AddDataVersionFix
import sh.miles.collector.tile.loader.fixes.CollectorUpgradeLevelToUpgradeStatusFix
import sh.miles.collector.tile.loader.fixes.LegacyUpgradeRenameDataFix

object CollectorFixing {
    val NO_DATA_VERSION = -1L
    val CURRENT_DATA_VERSION = 2L
    private val fixes = linkedMapOf(
        NO_DATA_VERSION to listOf(AddDataVersionFix),
        0L to listOf(LegacyUpgradeRenameDataFix),
        1L to listOf(CollectorUpgradeLevelToUpgradeStatusFix)
    )

    fun applyFixes(container: PersistentDataContainer, collectorDataVersion: Long) {
        fixes.forEach { (fixVersion, fixes) ->
            if (collectorDataVersion <= fixVersion) {
                fixes.forEach { it.loadFix(container) }
            }
        }
    }
}
