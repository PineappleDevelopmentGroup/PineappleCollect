package sh.miles.collect.listeners

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.block.TileState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.collect.registry.CollectorTemplateRegistry
import sh.miles.collect.util.PDC_CONTENT_KEY
import sh.miles.collect.util.PDC_SIZE_KEY
import sh.miles.collect.util.PDC_TEMPLATE_KEY
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some
import sh.miles.pineapple.item.ItemBuilder
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
                val collectorSome = collector.some()
                val template =
                    CollectorTemplateRegistry.getOrNull(collectorSome.templateKey)!! // this should never be null
                val world = block.world
                event.isDropItems = false
                if (event.player.gameMode != GameMode.CREATIVE) {
                    var item = template.item()
                    val itemMeta = item.itemMeta!!

                    if(!collectorSome.inventory.isOnlyAir()) {
                        itemMeta.persistentDataContainer.set(PDC_CONTENT_KEY, PersistentDataType.BYTE_ARRAY, PineappleLib.getNmsProvider().itemsToBytes(collectorSome.inventory.contents))
                        itemMeta.persistentDataContainer.set(PDC_SIZE_KEY, PersistentDataType.INTEGER, template.size)
                        itemMeta.persistentDataContainer.set(PDC_TEMPLATE_KEY, PersistentDataType.STRING, template.key)
                    }
                    item.itemMeta = itemMeta

                    item = ItemBuilder.modifyStack(item).lore(listOf(template.hasContentLore.component())).build()

                    val unableToAdd = event.player.inventory.addItem(item)
                    if (unableToAdd.isNotEmpty()) {
                        world.dropItemNaturally(event.block.location, unableToAdd[0]!!)
                    }
                }
                Collector.delete(block.chunk)
            }

            is None -> {
                event.player.sendMessage("An internal error occurred")
                throw IllegalStateException("No Collector Found Even Though All Checks Passed") // DEBUG ONLY
            }
        }
    }
}
