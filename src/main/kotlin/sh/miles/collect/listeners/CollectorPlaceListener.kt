package sh.miles.collect.listeners

import org.bukkit.block.Block
import org.bukkit.block.TileState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.collect.collector.template.CollectorTemplate
import sh.miles.collect.registry.CollectorTemplateRegistry
import sh.miles.collect.util.MessageConfig
import sh.miles.collect.util.PDC_CONTENT_KEY
import sh.miles.collect.util.PDC_POSITION_DATA_TYPE
import sh.miles.collect.util.PDC_POSITION_KEY
import sh.miles.collect.util.Position
import sh.miles.pineapple.function.Option
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some
import java.lang.IllegalStateException

object CollectorPlaceListener : Listener {

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val item = event.itemInHand
        if (!item.hasItemMeta()) return
        val meta = item.itemMeta!!
        if (!CollectorTemplate.hasTemplate(meta)) return

        val templateString = CollectorTemplate.template(meta)

        val template: Option<CollectorTemplate>
        when (templateString) {
            is Some -> {
                template = CollectorTemplateRegistry.get(templateString.some())
            }

            is None -> return
        }

        when (template) {
            is Some -> {
                val location = event.blockPlaced.location
                if (Collector.hasCollector(event.blockPlaced.chunk)) {
                    event.player.spigot().sendMessage(MessageConfig.COLLECTOR_ALREADY_IN_CHUNK.component(mapOf("x" to location.x, "y" to location.y, "z" to location.z)))
                    event.isCancelled = true
                    return
                }

                placeTemplate(event.blockPlaced, template.some(), item)
            }

            is None -> {
                return
            }
        }
    }

    private fun placeTemplate(block: Block, template: CollectorTemplate, item: ItemStack) {
        val state = block.state

        if (state !is TileState) {
            throw IllegalStateException("The template ${template.key}'s field \"block_entity\" is not a block entity instead found ${template.blockEntity} which is not a block entity")
        }


        var collector = Collector(template.key, template.size, Position.fromLocation(block.location))
        val hasContentKey = item.itemMeta!!.persistentDataContainer.has(PDC_CONTENT_KEY)
        if (hasContentKey) {
            item.itemMeta!!.persistentDataContainer.copyTo(state.persistentDataContainer, true)
            state.update()
            block.chunk.persistentDataContainer.set(PDC_POSITION_KEY, PDC_POSITION_DATA_TYPE, Position.fromLocation(block.location))
            collector = Collector.load(block.chunk).orThrow()
        } else {
            Collector.save(block.chunk, collector)
        }

        CollectorManager.load(collector)
    }

}
