package sh.miles.collect.command.data

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sh.miles.collect.collector.CollectorManager
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some

object DataGetCommand : Command(CommandLabel("get", "collect.command.data.get")) {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            // TODO: Configurable player only message
            return true
        }

        val chunk = sender.location.chunk
        // TODO: Configurable messages
        when (val collector = CollectorManager.obtain(chunk)) {
            is Some -> {
                val src = collector.some()
                sender.sendMessage(
                    """
                    Chunk (${chunk.x}, ${chunk.z}) Collector Data:
                    Template Key=${src.templateKey}
                    Size=${src.size}
                    Position=${src.position}
                """.trimIndent()
                )
            }

            is None -> {
                sender.sendMessage("No collector in this chunk")
            }
        }
        return true
    }

}
