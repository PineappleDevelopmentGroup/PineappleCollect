package sh.miles.collector.configuration

import org.bukkit.NamespacedKey
import sh.miles.collector.upgrade.action.UpgradeAction
import sh.miles.collector.upgrade.level.UpgradeLevel
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.item.ItemSpec

data class UpgradeConfiguration(
    val action: UpgradeAction,
    val level: List<UpgradeLevel>,
    val icon: ItemSpec,
    val maxIcon: ItemSpec
) : RegistryKey<NamespacedKey> {
    override fun getKey(): NamespacedKey {
        return NamespacedKey.fromString("pineapple-collect:${action.key}")!!
    }
}
