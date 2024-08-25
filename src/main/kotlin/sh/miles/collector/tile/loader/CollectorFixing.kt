package sh.miles.collector.tile.loader

import org.bukkit.persistence.PersistentDataContainer
import sh.miles.collector.tile.loader.fixes.AddDataVersionFix

object CollectorFixing {
    val NO_DATA_VERSION = -1L
    val CURRENT_DATA_VERSION = 0L
    private val fixes = linkedMapOf<Long, List<CollectorDataFixer>>(
        NO_DATA_VERSION to listOf(AddDataVersionFix)
    )

    fun applyFixes(container: PersistentDataContainer, collectorDataVersion: Long) {
        fixes.forEach { (fixVersion, fixes) ->
            if (collectorDataVersion <= fixVersion) {
                fixes.forEach { it.loadFix(container) }
            }
        }
    }
}
