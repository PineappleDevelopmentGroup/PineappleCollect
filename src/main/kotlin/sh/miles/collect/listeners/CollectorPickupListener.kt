package sh.miles.collect.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some

object CollectorPickupListener : Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block
        val state = block.state
        if (!Collector.isCollector(state)) return

        val collector = CollectorManager.obtain(state.chunk)
        when (collector) {
            is Some -> {
                CollectorManager.unload(block.chunk)
                Collector.delete(block.chunk)
            }

            is None -> return
        }
    }
}
