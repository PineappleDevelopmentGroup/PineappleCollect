package sh.miles.collect.collector

import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockState
import org.bukkit.block.TileState
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import sh.miles.collect.pdc.PDCConstants
import sh.miles.collect.util.Position
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.collection.NonNullList
import sh.miles.pineapple.function.Option

class Collector(val templateKey: String, val size: Int, val position: Position) {

    private val contents = NonNullList { ItemStack(Material.AIR) }

    fun getItemAt(index: Int): ItemStack {
        return contents[index]
    }

    fun removeItemAt(index: Int): ItemStack {
        return contents.removeAt(index)
    }

    fun getContentsCopy(): List<ItemStack> {
        return contents.stream().toList()
    }

    fun addContentsList(toAdd: List<ItemStack>) {
        this.contents.addAll(toAdd)
    }

    companion object {
        private val POSITION_KEY = NamespacedKey.fromString("collector:position")!!
        private val TEMPLATE_KEY = NamespacedKey.fromString("collector:template_key")!!
        private val SIZE_KEY = NamespacedKey.fromString("collector:size")!!
        private val CONTENT_KEY = NamespacedKey.fromString("collector:content")!!

        fun isCollector(blockState: BlockState): Boolean {
            if (blockState !is TileState) return false
            return blockState.persistentDataContainer.has(TEMPLATE_KEY)
        }

        fun hasCollector(chunk: Chunk): Boolean {
            val pdc = chunk.persistentDataContainer
            return pdc.has(POSITION_KEY)
        }

        fun load(chunk: Chunk): Option<Collector> {
            val pdc = chunk.persistentDataContainer
            val position = pdc.get(POSITION_KEY, PDCConstants.POSITION_DATA_TYPE) ?: return Option.none()

            val tileState = chunk.world.getBlockState(position.toLocation()) as TileState
            val blockPdc = tileState.persistentDataContainer
            val contents = blockPdc.get(CONTENT_KEY, PersistentDataType.LIST.byteArrays())!!
                .map { PineappleLib.getNmsProvider().itemFromBytes(it) }
                .toList()
            val templateKey = blockPdc.get(TEMPLATE_KEY, PersistentDataType.STRING)!!
            val sizeKey = blockPdc.get(SIZE_KEY, PersistentDataType.INTEGER)!!

            val collector = Collector(templateKey, sizeKey, position)
            collector.addContentsList(contents)
            return Option.some(collector)
        }

        fun save(chunk: Chunk, collector: Collector) {
            val chunkPdc = chunk.persistentDataContainer
            val hasPosition = chunkPdc.has(POSITION_KEY, PDCConstants.POSITION_DATA_TYPE)
            val position = collector.position

            if (!hasPosition) {
                chunkPdc.set(POSITION_KEY, PDCConstants.POSITION_DATA_TYPE, position)
            }

            val tileState = chunk.world.getBlockState(position.toLocation()) as TileState
            val blockPdc = tileState.persistentDataContainer
            blockPdc.set(
                CONTENT_KEY,
                PersistentDataType.LIST.byteArrays(),
                collector.getContentsCopy().map { PineappleLib.getNmsProvider().itemToBytes(it) }.toList()
            )
            blockPdc.set(TEMPLATE_KEY, PersistentDataType.STRING, collector.templateKey)
            blockPdc.set(SIZE_KEY, PersistentDataType.INTEGER, collector.size)
            tileState.update()
        }
    }

}
