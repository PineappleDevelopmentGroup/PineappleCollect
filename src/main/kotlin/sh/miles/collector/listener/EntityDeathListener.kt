package sh.miles.collector.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import sh.miles.collector.GlobalConfig
import sh.miles.collector.hook.Plugins
import sh.miles.collector.tile.CollectorTile
import sh.miles.pineapple.tiles.api.Tiles

class EntityDeathListener : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val collector =
            Tiles.getInstance().getTiles(event.entity.location.chunk) { it is CollectorTile }.values.firstOrNull()
        if (collector == null) {
            return
        }
        println("onEntityDeath")

        lateinit var drop: ItemStack
        val iterator = event.drops.iterator()
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
