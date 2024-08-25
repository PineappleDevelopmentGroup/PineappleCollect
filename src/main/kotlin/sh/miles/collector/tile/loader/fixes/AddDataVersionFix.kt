package sh.miles.collector.tile.loader.fixes

import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import sh.miles.collector.tile.COLLECTOR_DATA_VERSION
import sh.miles.collector.tile.loader.CollectorDataFixer
import sh.miles.collector.tile.loader.CollectorFixing

object AddDataVersionFix : CollectorDataFixer {
    override val dataVersionTarget: Long = CollectorFixing.NO_DATA_VERSION
    override val fixName: String = "Data Version Fix"

    override fun loadFix(container: PersistentDataContainer) {
        container.set(COLLECTOR_DATA_VERSION, PersistentDataType.LONG, CollectorFixing.CURRENT_DATA_VERSION)
    }
}
