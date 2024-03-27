package sh.miles.collect.command.data

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sh.miles.collect.collector.Collector
import sh.miles.collect.util.MessageConfig
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

object DataRemoveCommand : Command(CommandLabel("remove", "collect.command.data.remove")) {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(MessageConfig.OTHER_PLAYER_ONLY_COMMAND)
            return true
        }

        val chunk = sender.location.chunk
        val deleted = Collector.delete(chunk)
        if (deleted) sender.sendMessage("Chunk Data for (${chunk.x}, ${chunk.z}) cleared") else sender.sendMessage("No collector was found in the chunk")
        return true
    }

}
