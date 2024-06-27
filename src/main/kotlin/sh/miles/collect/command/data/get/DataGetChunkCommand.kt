package sh.miles.collect.command.data.get

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataContainer
import sh.miles.collect.util.MessageConfig
import sh.miles.collect.util.PDC_POSITION_DATA_TYPE
import sh.miles.collect.util.PDC_POSITION_KEY
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

object DataGetChunkCommand : Command(CommandLabel("chunk", "collect.command.data.get.global")) {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(MessageConfig.OTHER_PLAYER_ONLY_COMMAND)
            return true
        }

        val chunk = sender.location.chunk
        sender.sendMessage("Current Chunk PDC:")
        sender.sendMessage("${getCollectorData(chunk.persistentDataContainer)}")

        return true
    }

    private fun getCollectorData(pdc: PersistentDataContainer): Map<String, String> {
        val foundData = mutableMapOf<String, String>()

        foundData[PDC_POSITION_KEY.toString()] = (pdc.get(PDC_POSITION_KEY, PDC_POSITION_DATA_TYPE) ?: return foundData).toString()

        return foundData
    }
}