package sh.miles.collector.tile.loader

import org.bukkit.persistence.PersistentDataContainer

interface CollectorDataFixer {
    val dataVersionTarget: Long
    val fixName: String

    fun loadFix(container: PersistentDataContainer)
}
