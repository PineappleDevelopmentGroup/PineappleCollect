package sh.miles.collect.collector

import com.google.gson.reflect.TypeToken
import org.bukkit.Chunk
import sh.miles.collect.CollectPlugin
import sh.miles.collect.collector.template.CollectorTemplate
import sh.miles.collect.registry.CollectorTemplateRegistry
import sh.miles.collect.util.ChunkPosition
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.function.Option
import java.io.File

object CollectorManager {

    private val loadedCollectors = HashMap<ChunkPosition, Collector>()
    private val upgrades = HashMap<String, String>()

    fun load(collector: Collector) {
        loadedCollectors[collector.position.chunkpos()] = collector
        println(loadedCollectors.values.map { it.position.chunkpos() }.toList())
    }

    fun unload(chunk: Chunk): Option<Collector> {
        return unload(ChunkPosition(chunk.world.uid, chunk.x, chunk.z))
    }

    fun unload(chunkPosition: ChunkPosition): Option<Collector> {
        println("$chunkPosition | ${loadedCollectors.values.map { it.position.chunkpos() }.toList()}")
        return Option.some(loadedCollectors.remove(chunkPosition) ?: return Option.none())
    }

    fun obtain(chunk: Chunk): Option<Collector> {
        println("Obtain Attempted from! $chunk")
        return obtain(ChunkPosition(chunk.world.uid, chunk.x, chunk.z))
    }

    fun obtain(chunkPosition: ChunkPosition): Option<Collector> {
        println("Collector Status: ${loadedCollectors[chunkPosition]}")
        return Option.some(loadedCollectors[chunkPosition] ?: return Option.none())
    }

    fun obtainAll(): Collection<Collector> {
        return loadedCollectors.values
    }

    fun recoverFromUnloaded(chunk: Chunk): Collector {
        PineappleLib.getLogger().severe(
            """
                        SEVERE ERROR OCCURRED!
                        ============================================================================================
                        A collector existed, but was not in the cache at ${chunk}.
                        This usually only occurs given the plugin is reloaded using `/reload` or another
                        similar functionality. Collect is now attempting to recover from this error.
                        ============================================================================================
                    """.trimIndent()
        )
        val collector = PineappleLib.getAnomalyFactory().create()
            .message("Collect was unable to recover from a severe error caused by reloading!")
            .run { Collector.load(chunk).orThrow() }
            .hard(javaClass, "onPlayerInteract").orThrow()
        load(collector)
        PineappleLib.getLogger().info(
            """
                        SEVERE ERROR RECOVERED!
                        ============================================================================================                       
                        Collect was able to recover from the severe error likely caused by reloading.
                        While Collect was able to recover this time there is no guarantee it will be-able
                        to in the future, please refrain from using reloads or similar in the future.
                        While Collect recovered data loss still may have occurred!
                        ============================================================================================
                    """.trimIndent()
        )
        return collector
    }
}
