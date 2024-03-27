package sh.miles.collect.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import sh.miles.collect.collector.template.CollectorTemplate
import sh.miles.collect.registry.CollectorTemplateRegistry
import sh.miles.collect.util.MessageConfig
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineapple.function.Option.None
import sh.miles.pineapple.function.Option.Some

internal object GiveCommand : Command(CommandLabel("give", "collect.command.give")) {

    override fun execute(sender: CommandSender, args: Array<String>): Boolean {
        val target: Player
        val template: String

        when (args.size) {
            1 -> {
                if (sender !is Player) {
                    sender.sendMessage(MessageConfig.OTHER_PLAYER_ONLY_COMMAND)
                    return true
                }

                target = sender
                template = args[0]
            }

            2 -> {
                val temp = Bukkit.getPlayer(args[0])
                if (temp == null) {
                    sender.spigot().sendMessage(MessageConfig.OTHER_PLAYER_NOT_ONLINE.component())
                    return true
                }
                target = temp
                template = args[1]
            }

            else -> {
                sender.spigot().sendMessage(MessageConfig.OTHER_GIVE_COMMAND_USAGE.component())
                return true
            }
        }

        val collectorTemplate = CollectorTemplateRegistry.get(template)
        when (collectorTemplate) {
            is Some<CollectorTemplate> -> target.inventory.addItem(collectorTemplate.some().item())
            is None<CollectorTemplate> -> {
                sender.spigot().sendMessage(MessageConfig.OTHER_INVALID_TEMPLATE_ID.component())
                return true
            }
        }

        return true
    }

    override fun complete(sender: CommandSender, args: Array<String>): List<String> {
        if (args.isEmpty()) {
            if (sender !is Player) {
                return StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().map { it.name }.toList(), mutableListOf())
            }
            return StringUtil.copyPartialMatches(args[0], CollectorTemplateRegistry.keys().toList(), mutableListOf())
        } else if (args.size == 1) {
            return StringUtil.copyPartialMatches(args[0], CollectorTemplateRegistry.keys().toList(), mutableListOf())
        } else {
            return listOf()
        }
    }

}
