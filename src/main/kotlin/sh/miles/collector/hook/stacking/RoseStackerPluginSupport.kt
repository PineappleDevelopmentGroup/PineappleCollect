package sh.miles.collector.hook.stacking

import com.google.common.base.Suppliers
import dev.rosewood.rosestacker.api.RoseStackerAPI
import dev.rosewood.rosestacker.event.EntityStackMultipleDeathEvent
import dev.rosewood.rosestacker.stack.StackedEntity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import sh.miles.collector.GlobalConfig
import sh.miles.collector.hook.Plugins
import sh.miles.collector.tile.CollectorTile
import sh.miles.pineapple.tiles.api.Tiles
import java.util.function.Supplier

object RoseStackerPluginSupport : StackingSupportedPlugin {
    override val name: String = "RoseStacker"
    private lateinit var api: Supplier<RoseStackerAPI>

    override fun load(root: Plugin): Boolean {

        val status = root.server.pluginManager.getPlugin("RoseStacker") != null
        if (status) {
            api = Suppliers.memoize { RoseStackerAPI.getInstance() }
            root.server.pluginManager.registerEvents(MultiKillListener, root)
        }
        return status
    }

    override fun getDropsOrDefault(entity: LivingEntity, default: Collection<ItemStack>): Collection<ItemStack> {
        val stackedEntity = getStackedEntity(entity) ?: return default
        return api.get().getStackedEntityLoot(stackedEntity).drops
    }

    private fun getStackedEntity(entity: LivingEntity): StackedEntity? {
        return api.get().getStackedEntity(entity)
    }

    override fun getKey(): String {
        return Plugins.STACKING
    }

    private object MultiKillListener : Listener {

        @EventHandler
        fun onMultiKill(event: EntityStackMultipleDeathEvent) {
            val collector = Tiles.getInstance()
                .getTiles(event.stack.entity.location.chunk) { it is CollectorTile }.values.firstOrNull()
            if (collector == null) {
                return
            }
            println("onMultiKill")

            lateinit var drop: ItemStack
            event.entityDrops.forEach { _, stackDrops ->
                val iterator = stackDrops.drops.iterator()
                while (iterator.hasNext()) {
                    drop = iterator.next()
                    if (GlobalConfig.ALLOW_PICKUP_NON_SELLABLES) {
                        (collector as CollectorTile).addItem(drop)
                        iterator.remove()
                    } else if (Plugins.shopOrThrow().canSell(drop)) {
                        (collector as CollectorTile).addItem(drop)
                        iterator.remove()
                    }
                }
            }

        }
    }
}
