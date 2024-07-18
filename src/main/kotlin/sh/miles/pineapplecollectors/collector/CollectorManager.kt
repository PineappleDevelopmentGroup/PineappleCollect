package sh.miles.pineapplecollectors.collector

import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDeathEvent

object CollectorManager {

    fun handleEntityDeath(event: EntityDeathEvent) {
//        val items = event.drops.map { ShopGuiHook.canSell(it) }
//
//        val entityChunk = event.entity.location.chunk
//        val chunkCache = Tiles.getServerTileCache().getTileCache(entityChunk) ?: return
    }

    fun handleBlockDropItem(event: BlockDropItemEvent) {
    }

    fun handleBlockBreak(event: BlockBreakEvent) {
    }
}