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
        when (val collector = Collector.load(event.chunk)) {
            is Some<Collector> -> CollectorManager.load(collector.some())
            is None<Collector> -> return
        }
    }

    @EventHandler
    fun onChunkUnload(event: ChunkUnloadEvent) {
        when (val collector = CollectorManager.unload(event.chunk)) {
            is Some<Collector> -> Collector.save(event.chunk, collector.some())
            is None<Collector> -> return
        }
    }
}
