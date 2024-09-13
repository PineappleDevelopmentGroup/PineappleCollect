package sh.miles.collector.tile.loader.fixes

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import sh.miles.collector.tile.COLLECTOR_UPGRADES
import sh.miles.collector.tile.loader.CollectorDataFixer
import sh.miles.collector.upgrade.action.UpgradeActionRegistry

object LegacyUpgradeRenameDataFix : CollectorDataFixer {
    override val dataVersionTarget: Long = 0L
    override val fixName: String = "Legacy Upgrade Rename Data Fix"

    private val remapper = mapOf(
        "upgrade_autosell" to NamespacedKey.fromString("pineapple-collect:${UpgradeActionRegistry.AUTO_SELL}")
    )

    override fun loadFix(container: PersistentDataContainer) {
        if (!container.has(COLLECTOR_UPGRADES)) return
        val upgrades = container.get(COLLECTOR_UPGRADES, PersistentDataType.TAG_CONTAINER)!!
        for (key in upgrades.keys) {
            val remapped = remapper[key.key] ?: continue
            val currentLevel = upgrades.get(key, PersistentDataType.INTEGER)!!
            upgrades.remove(key)
            upgrades.set(remapped, PersistentDataType.INTEGER, currentLevel)
        }
        container.set(COLLECTOR_UPGRADES, PersistentDataType.TAG_CONTAINER, upgrades)
    }
}
