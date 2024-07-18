package sh.miles.pineapplecollectors.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import sh.miles.pineapplecollectors.collector.CollectorManager

class BlockBreakListener : Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) = CollectorManager.handleBlockBreak(event)
}