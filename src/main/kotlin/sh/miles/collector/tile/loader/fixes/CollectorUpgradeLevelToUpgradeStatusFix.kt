package sh.miles.collector.tile.loader.fixes

import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import sh.miles.collector.tile.COLLECTOR_UPGRADES
import sh.miles.collector.tile.loader.CollectorDataFixer

object CollectorUpgradeLevelToUpgradeStatusFix : CollectorDataFixer {
    override val dataVersionTarget: Long = 1L
    override val fixName: String = "Collector Upgrade Level To Upgrade Status Fix"
    private const val STATUS_ENABLED = 1

    override fun loadFix(container: PersistentDataContainer) {
        if (!container.has(COLLECTOR_UPGRADES)) {
            return
        }

        val upgradeContainer = container.get(COLLECTOR_UPGRADES, PersistentDataType.TAG_CONTAINER)!!
        for (key in upgradeContainer.keys) {
            val level = upgradeContainer.get(key, PersistentDataType.INTEGER)!!
            val status = listOf(level, STATUS_ENABLED)
            upgradeContainer.set(key, PersistentDataType.LIST.integers(), status)
        }

        container.set(COLLECTOR_UPGRADES, PersistentDataType.TAG_CONTAINER, upgradeContainer)
    }
}
