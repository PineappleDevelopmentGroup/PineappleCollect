package sh.miles.collect.listeners

import org.bukkit.block.TileState
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.collect.collector.view.CollectorView
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some

object CollectorInteractListener : Listener {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val clickedBlock = event.clickedBlock ?: return
        if (!Collector.isCollector(clickedBlock.state)) return

        val collector = when (val option = CollectorManager.obtain(clickedBlock.chunk)) {
            is Some -> option.some()
            is None -> CollectorManager.recoverFromUnloaded(clickedBlock.chunk)
        }

        event.setUseInteractedBlock(Event.Result.DENY)
        event.setUseItemInHand(Event.Result.DENY)
        event.isCancelled = true

        val player = event.player

        val menu = CollectorView(player, collector.inventory, collector.size + 9, collector.templateKey, collector.position)
        menu.open()
    }

}
