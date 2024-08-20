package sh.miles.collector.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import sh.miles.collector.configuration.CollectorConfiguration
import sh.miles.collector.Registries
import sh.miles.collector.tile.CollectorTileType
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

object CollectorCommand : Command(CommandLabel("collectors", "collector.command")) {

    init {
        registerSubcommand(CollectorGiveCommand)
        registerSubcommand(CollectorDebugCommand)
    }

    private object CollectorGiveCommand : Command(CommandLabel("give", "collector.command.give")) {
        override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
            val target: Player
            val collectorConfiguration: CollectorConfiguration
            if (sender !is Player) {
                if (args.size == 2) {
                    target = Bukkit.getPlayer(args[0])!!
                    collectorConfiguration = Registries.COLLECTOR.get(args[1]).orThrow()
                } else {
                    sender.sendMessage("/collect give <player> <collector>")
                    return true
                }
            } else {
                target = sender
                collectorConfiguration = Registries.COLLECTOR.get(args[0]).orThrow()
            }

            target.inventory.addItem(CollectorTileType.createItem(collectorConfiguration))
            return true
        }

        override fun complete(sender: CommandSender, args: Array<out String>): MutableList<String> {
            return if (args.size == 1) StringUtil.copyPartialMatches(
                args[0], Registries.COLLECTOR.keys().map { it.toString() }.toList(), mutableListOf()
            ) else mutableListOf()
        }
    }

}
