package sh.miles.pineapplecollectors.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import sh.miles.pineapplecollectors.collector.CollectorManager

class BlockDropItemListener : Listener {

    @EventHandler
    fun onItemDrop(event: BlockDropItemEvent) = CollectorManager.handleBlockDropItem(event)
}