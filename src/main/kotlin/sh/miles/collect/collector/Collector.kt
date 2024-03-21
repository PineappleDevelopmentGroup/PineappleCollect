package sh.miles.collect.collector

import org.bukkit.Chunk
import org.bukkit.block.BlockState
import org.bukkit.block.TileState
import org.bukkit.persistence.PersistentDataType
import sh.miles.collect.util.PDC_CONTENT_KEY
import sh.miles.collect.util.PDC_POSITION_DATA_TYPE
import sh.miles.collect.util.PDC_POSITION_KEY
import sh.miles.collect.util.PDC_SIZE_KEY
import sh.miles.collect.util.PDC_TEMPLATE_KEY
import sh.miles.collect.util.Position
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.container.Container
import sh.miles.pineapple.container.SimpleContainer
import sh.miles.pineapple.function.Option

class Collector(val templateKey: String, val size: Int, val position: Position) {

    val inventory: Container = SimpleContainer(size)

    companion object {
        fun isCollector(blockState: BlockState): Boolean {
            if (blockState !is TileState) return false
            return blockState.persistentDataContainer.has(PDC_TEMPLATE_KEY)
        }

        fun hasCollector(chunk: Chunk): Boolean {
            val pdc = chunk.persistentDataContainer
            return pdc.has(PDC_POSITION_KEY)
        }

        fun load(chunk: Chunk): Option<Collector> {
            val pdc = chunk.persistentDataContainer
            val position = pdc.get(PDC_POSITION_KEY, PDC_POSITION_DATA_TYPE) ?: return Option.none()

            val tileState = chunk.world.getBlockState(position.toLocation()) as TileState
            val blockPdc = tileState.persistentDataContainer
            val templateKey = blockPdc.get(PDC_TEMPLATE_KEY, PersistentDataType.STRING)!!
            val sizeKey = blockPdc.get(PDC_SIZE_KEY, PersistentDataType.INTEGER)!!
            val contents = PineappleLib.getNmsProvider()
                .itemsFromBytes(blockPdc.get(PDC_CONTENT_KEY, PersistentDataType.BYTE_ARRAY)!!, sizeKey)

            val collector = Collector(templateKey, sizeKey, position)
            for ((index, itemStack) in contents.withIndex()) {
                collector.inventory.setItemAt(index, itemStack)
            }
            return Option.some(collector)
        }

        fun save(chunk: Chunk, collector: Collector) {
            val chunkPdc = chunk.persistentDataContainer
            val hasPosition = chunkPdc.has(PDC_POSITION_KEY, PDC_POSITION_DATA_TYPE)
            val position = collector.position

            if (!hasPosition) {
                chunkPdc.set(PDC_POSITION_KEY, PDC_POSITION_DATA_TYPE, position)
            }

            val tileState = chunk.world.getBlockState(position.toLocation()) as TileState
            val blockPdc = tileState.persistentDataContainer
            blockPdc.set(
                PDC_CONTENT_KEY,
                PersistentDataType.BYTE_ARRAY,
                PineappleLib.getNmsProvider().itemsToBytes(collector.inventory.contents)
            )
            blockPdc.set(PDC_TEMPLATE_KEY, PersistentDataType.STRING, collector.templateKey)
            blockPdc.set(PDC_SIZE_KEY, PersistentDataType.INTEGER, collector.size)
            tileState.update()
        }

        fun delete(chunk: Chunk) {
            val pdc = chunk.persistentDataContainer
            if (!pdc.has(PDC_POSITION_KEY)) return
            pdc.remove(PDC_POSITION_KEY)
        }
    }

}
