package sh.miles.collect.command.data

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sh.miles.collect.util.MessageConfig
import sh.miles.collect.util.PDC_POSITION_KEY
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

object DataRemoveChunkCommand : Command(CommandLabel("removeChunk", "collect.command.data.removechunk")) {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(MessageConfig.OTHER_PLAYER_ONLY_COMMAND)
            return true
        }

        val chunk = sender.location.chunk
        chunk.persistentDataContainer.remove(PDC_POSITION_KEY)
        sender.sendMessage("Chunk PositionKey Removed for (${chunk.x}, ${chunk.z})")
        return true
    }
}