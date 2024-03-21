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

        val collector: Collector
        when (val option = CollectorManager.obtain(clickedBlock.chunk)) {
            is Some -> {
                collector = option.some()
            }

            is None -> {
                PineappleLib.getLogger().severe(
                    """
                        SEVERE ERROR OCCURRED!
                        ============================================================================================
                        A collector existed, but was not in the cache at ${clickedBlock.location}.
                        This usually only occurs given the plugin is reloaded using `/reload` or another
                        similar functionality. Collect is now attempting to recover from this error.
                        ============================================================================================
                    """.trimIndent()
                )
                collector = PineappleLib.getAnomalyFactory().create()
                    .message("Collect was unable to recover from a severe error caused by reloading!")
                    .run { Collector.load(clickedBlock.chunk).orThrow() }
                    .hard(javaClass, "onPlayerInteract").orThrow()
                CollectorManager.load(collector)
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
            }
        }

        val player = event.player
        player.sendMessage(
            """
            Collector with the following data found
            template: ${collector.templateKey}
            position: ${collector.position}
            size: ${collector.size}
        """.trimIndent()
        )

        val menu = CollectorView(player, collector.inventory)
        menu.open()

        event.setUseInteractedBlock(Event.Result.DENY)
        event.setUseItemInHand(Event.Result.DENY)
        event.isCancelled = true
    }

}
