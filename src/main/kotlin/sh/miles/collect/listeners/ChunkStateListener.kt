package sh.miles.collect.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some

object ChunkStateListener : Listener {

    @EventHandler
    fun onChunkLoad(event: ChunkLoadEvent) {
        if (!Collector.hasCollector(event.chunk)) {
            return
        }

        when (val collector = Collector.load(event.chunk)) {
            is Some<Collector> -> {
                println("Load collector at ${collector.some().position}")
                CollectorManager.load(collector.some())
            }

            is None<Collector> -> return
        }
    }

    @EventHandler
    fun onChunkUnload(event: ChunkUnloadEvent) {
        if (!Collector.hasCollector(event.chunk)) {
            return
        }

        when (val collector = CollectorManager.unload(event.chunk)) {
            is Some<Collector> -> {
                println("Saving collector at ${collector.some().position}")
                Collector.save(event.chunk, collector.some())
            }

            is None<Collector> -> return
        }
    }
}
