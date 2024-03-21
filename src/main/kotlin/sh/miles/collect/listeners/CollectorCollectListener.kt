package sh.miles.collect.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some

object CollectorCollectListener : Listener {

    @EventHandler
    fun onMobDeath(event: EntityDeathEvent) {
        val chunk = event.entity.location.chunk
        if (!Collector.hasCollector(chunk)) return

        val collector = when (val option = CollectorManager.obtain(chunk)) {
            is Some -> option.some()
            is None -> CollectorManager.recoverFromUnloaded(chunk)
        }

        val inventory = collector.inventory
        val iterator = event.drops.iterator()
        var drop: ItemStack
        while (iterator.hasNext()) {
            drop = iterator.next()
            if (inventory.addItem(drop)) {
                iterator.remove()
            }
        }
    }


}
