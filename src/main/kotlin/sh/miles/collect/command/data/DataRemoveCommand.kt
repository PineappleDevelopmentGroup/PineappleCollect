package sh.miles.collect.command.data

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sh.miles.collect.collector.Collector
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

object DataRemoveCommand : Command(CommandLabel("remove", "collect.command.data.remove")) {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            //TODO: player only command
            return true
        }

        val chunk = sender.location.chunk
        Collector.delete(chunk)
        // TODO: send configurable message
        sender.sendMessage("Chunk Data for (${chunk.x}, ${chunk.z}) cleared")
        return true
    }

}
