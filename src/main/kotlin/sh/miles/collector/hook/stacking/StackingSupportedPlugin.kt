package sh.miles.collector.hook.stacking

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import sh.miles.collector.hook.SupportedPlugin

interface StackingSupportedPlugin : SupportedPlugin {
    fun getDropsOrDefault(entity: LivingEntity, default: Collection<ItemStack>): Collection<ItemStack>
}
