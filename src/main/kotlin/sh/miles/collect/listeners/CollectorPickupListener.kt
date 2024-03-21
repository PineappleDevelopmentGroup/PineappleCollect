package sh.miles.collect.listeners

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.block.TileState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.ItemStack
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.collect.registry.CollectorTemplateRegistry
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some
import java.lang.IllegalStateException

object CollectorPickupListener : Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val block = event.block
        if (!Collector.hasCollector(block.chunk)) return
        if (block.state !is TileState) return
        val state = block.state as TileState
        if (!Collector.isCollector(state)) return

        when (val collector = CollectorManager.unload(block.chunk)) {
            is Some -> {
                val template =
                    CollectorTemplateRegistry.getOrNull(collector.some().templateKey)!! // this should never be null
                val world = block.world
                event.isDropItems = false
                if (event.player.gameMode != GameMode.CREATIVE) {
                    world.dropItemNaturally(block.location, template.item())
                }
                Collector.delete(block.chunk)
            }

            is None -> {
                throw IllegalStateException("No Collector Found Even Though All Checks Passed") // DEBUG ONLY
            }
        }
    }
}
