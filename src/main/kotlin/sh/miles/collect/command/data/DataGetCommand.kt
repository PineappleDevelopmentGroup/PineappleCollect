package sh.miles.collect.command.data

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sh.miles.collect.collector.CollectorManager
import sh.miles.collect.registry.CollectorTemplateRegistry
import sh.miles.collect.util.MessageConfig
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some

object DataGetCommand : Command(CommandLabel("get", "collect.command.data.get")) {

    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(MessageConfig.OTHER_PLAYER_ONLY_COMMAND)
            return true
        }

        val chunk = sender.location.chunk
        when (val collector = CollectorManager.obtain(chunk)) {
            is Some -> {
                val src = collector.some()
                val template = CollectorTemplateRegistry.get(src.templateKey)
                val upgradeData = template.orThrow().upgradeSpec.orThrow()
                sender.sendMessage(
                    """
                    Chunk (${chunk.x}, ${chunk.z}) Collector Data:
                    Template Key=${src.templateKey}
                    Size=${src.size}
                    Position=${src.position}
                    UpgradeData=Id:${upgradeData.sizeUpgradeKey},Cost:${upgradeData.sizeUpgradeCost}
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
