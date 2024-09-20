package sh.miles.collector.configuration

import org.bukkit.NamespacedKey
import sh.miles.collector.upgrade.action.UpgradeAction
import sh.miles.collector.upgrade.level.UpgradeLevel
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.item.ItemSpec
import kotlin.math.max

data class UpgradeConfiguration(
    val action: UpgradeAction,
    val level: List<UpgradeLevel>,
    val icon: ItemSpec,
    val disabledIcon: ItemSpec,
    val maxIcon: ItemSpec
) : RegistryKey<NamespacedKey> {
    val maxLevel = level.size

    fun <T> mapLevelOrDefault(currentLevel: Int, default: T, map: (UpgradeLevel) -> T?): T {
        val upgradeLevel: UpgradeLevel
        if (currentLevel == 0) {
            return default
        } else if (currentLevel >= maxLevel) {
            upgradeLevel = level[maxLevel - 1]
        } else {
            upgradeLevel = level[currentLevel - 1]
        }
        return map.invoke(upgradeLevel) ?: default
    }

    override fun getKey(): NamespacedKey {
        return NamespacedKey.fromString("pineapple-collect:${action.key}")!!
    }
}
