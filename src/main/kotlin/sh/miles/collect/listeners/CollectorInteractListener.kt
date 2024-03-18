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

object CollectorInteractListener : Listener {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND) return
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val clickedBlock = event.clickedBlock ?: return
        if (!Collector.isCollector(clickedBlock.state)) return

        val collector =
            CollectorManager.obtain(clickedBlock.chunk).orThrow() // we should guarantee its a collector above
        val player = event.player
        player.sendMessage(
            """
            Collector with the following data found
            template: ${collector.templateKey}
            position: ${collector.position}
            size: ${collector.size}
        """.trimIndent()
        )

        event.setUseItemInHand(Event.Result.DENY)
        event.isCancelled = true
    }

}
