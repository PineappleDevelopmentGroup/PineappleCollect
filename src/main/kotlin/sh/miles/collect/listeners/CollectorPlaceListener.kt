package sh.miles.collect.listeners

import org.bukkit.block.Block
import org.bukkit.block.TileState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.collect.collector.template.CollectorTemplate
import sh.miles.collect.registry.CollectorTemplateRegistry
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

        val template: Option<CollectorTemplate>;
        when (templateString) {
            is Some -> {
                template = CollectorTemplateRegistry.get(templateString.some())
            }

            is None -> return
        }

        when (template) {
            is Some -> {
                if (Collector.hasCollector(event.blockPlaced.chunk)) {
                    // TODO: send message Collector placed at [position]
                    event.player.sendMessage("Collector already placed in this chunk")
                    event.isCancelled = true
                    return
                }

                placeTemplate(event.blockPlaced, template.some())
            }

            is None -> {
                return
            }
        }
    }

    private fun placeTemplate(block: Block, template: CollectorTemplate) {
        println("placeTemplate running")
        val state = block.state

        if (state !is TileState) {
            throw IllegalStateException("The template ${template.key}'s field \"block_entity\" is not a block entity instead found ${template.blockEntity} which is not a block entity")
        }

        val collector = Collector(template.key, 27, Position.fromLocation(block.location))
        Collector.save(block.chunk, collector)
        CollectorManager.load(collector)
    }

}
