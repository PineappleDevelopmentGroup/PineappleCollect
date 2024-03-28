package sh.miles.collect.listeners

import dev.norska.dsw.DeluxeSellwands
import dev.norska.dsw.api.DeluxeSellwandSellEvent
import dev.norska.dsw.api.DeluxeSellwandsAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import sh.miles.collect.collector.Collector
import sh.miles.collect.collector.CollectorManager
import sh.miles.collect.util.PluginHooks
import sh.miles.collect.util.item.InfStack
import sh.miles.pineapple.function.Option
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some
import java.math.BigDecimal

object DeluxeSellWandsListener : Listener {

    @EventHandler
    fun onSell(event: DeluxeSellwandSellEvent) {
        val clickedBlock = event.clickedBlock;
        if (!Collector.isCollector(clickedBlock.state)) return

        val collector = when (val option = CollectorManager.obtain(clickedBlock.chunk)) {
            is Some -> option.some()
            is None -> CollectorManager.recoverFromUnloaded(clickedBlock.chunk)
        }

        val api = DeluxeSellwands.getInstance().api
        val multiplier = api.getMultiplier(event.sellwand)
        val container = collector.inventory
        var sellPrice: BigDecimal = BigDecimal.ZERO
        for (index in 0 until container.size) {
            when (val option = container.getInfStackAt(index)) {
               is Some -> {
                   val stack = option.some()
                   sellPrice = sellPrice.add(PluginHooks.priceItem(event.player, stack.comparator(), stack.size()))
                   container.setInfStackAt(index, InfStack())
               }
                is None -> break
            }
        }

        sellPrice = sellPrice.multiply(BigDecimal(multiplier))
        event.money = sellPrice.toDouble()
    }

}
