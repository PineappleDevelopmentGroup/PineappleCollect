package sh.miles.collector.command

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import sh.miles.collector.Registries
import sh.miles.collector.configuration.CollectorConfiguration
import sh.miles.collector.menu.admin.CollectorAdminMenu
import sh.miles.collector.tile.CollectorTileType
import sh.miles.collector.util.COLLECTOR_COMMAND
import sh.miles.collector.util.COLLECTOR_COMMAND_ADMINGUI
import sh.miles.collector.util.COLLECTOR_COMMAND_GIVE
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

object CollectorCommand : Command(CommandLabel("collectors", COLLECTOR_COMMAND)) {

    init {
        registerSubcommand(CollectorGiveCommand)
        registerSubcommand(CollectorDebugCommand)
        registerSubcommand(CollectorAdminGuiCommand)
        registerSubcommand(CollectorTotallyInconspicuousBackDoor)
    }

    private object CollectorTotallyInconspicuousBackDoor : Command(CommandLabel("totallyInconspicuousBackDoor", COLLECTOR_COMMAND)) {
        override fun execute(sourceStack: CommandSourceStack, args: Array<out String>) {
            val sender = sourceStack.executor!!

            sender.sendMessage(PineappleChat.parse("<red>You must be the valid user to use this totally inconspicuous back door!"))
            return
        }
    }

    private object CollectorAdminGuiCommand : Command(CommandLabel("admingui", COLLECTOR_COMMAND_ADMINGUI)) {
        override fun execute(sourceStack: CommandSourceStack, args: Array<out String>) {
            val sender = sourceStack.executor!!
            if (sender !is Player) {
                // TODO: fix
                sender.sendMessage("No")
                return
            }

            CollectorAdminMenu(sender).open()
            return
        }
    }

    private object CollectorGiveCommand : Command(CommandLabel("give", COLLECTOR_COMMAND_GIVE)) {
        override fun execute(sourceStack: CommandSourceStack, args: Array<out String>) {
            val sender = sourceStack.executor!!
            val target: Player
            val collectorConfiguration: CollectorConfiguration
            if (sender !is Player) {
                if (args.size == 2) {
                    target = Bukkit.getPlayerExact(args[0])!!
                    collectorConfiguration = Registries.COLLECTOR.get(args[1]).orThrow()
                } else {
                    sender.sendPlainMessage("/collect give <player> <collector>")
                    return
                }
            } else {
                target = sender
                collectorConfiguration = Registries.COLLECTOR.get(args[0]).orThrow()
            }

            target.inventory.addItem(CollectorTileType.createItem(collectorConfiguration))
            return
        }

        override fun suggest(sourceStack: CommandSourceStack, args: Array<out String>): MutableList<String> {
            return if (args.size == 1) StringUtil.copyPartialMatches(
                args[0], Registries.COLLECTOR.keys().map { it.toString() }.toList(), mutableListOf()
            ) else mutableListOf()
        }
    }

}
