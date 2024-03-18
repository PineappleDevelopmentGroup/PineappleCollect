package sh.miles.collect.collector

import org.bukkit.Chunk
import sh.miles.collect.util.ChunkPosition
import sh.miles.pineapple.function.Option

object CollectorManager {

    private val loadedCollectors = mutableMapOf<ChunkPosition, Collector>()

    fun load(collector: Collector) {
        loadedCollectors[collector.position.chunkpos()] = collector
    }

    fun unload(chunk: Chunk): Option<Collector> {
        return unload(ChunkPosition(chunk.world.uid, chunk.x, chunk.z))
    }

    fun unload(chunkPosition: ChunkPosition): Option<Collector> {
        return Option.some(loadedCollectors.remove(chunkPosition) ?: return Option.none())
    }

    fun obtain(chunk: Chunk): Option<Collector> {
        println(loadedCollectors)
        println(chunk)
        return obtain(ChunkPosition(chunk.world.uid, chunk.x, chunk.z))
    }

    fun obtain(chunkPosition: ChunkPosition): Option<Collector> {
        return Option.some(loadedCollectors[chunkPosition] ?: return Option.none())
    }
}