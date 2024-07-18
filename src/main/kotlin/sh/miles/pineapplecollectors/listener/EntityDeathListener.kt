package sh.miles.pineapplecollectors.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import sh.miles.pineapplecollectors.collector.CollectorManager

class EntityDeathListener : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) = CollectorManager.handleEntityDeath(event)
}