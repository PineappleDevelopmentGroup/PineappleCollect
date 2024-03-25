package sh.miles.collect.command

import org.bukkit.command.CommandSender
import sh.miles.collect.CollectPlugin
import sh.miles.collect.util.MessageConfig
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

internal object ReloadCommand : Command(CommandLabel("reload", "collect.command.reload")) {

    override fun execute(sender: CommandSender, args: Array<String>): Boolean {
        CollectPlugin.plugin.reloadMessages()
        sender.spigot().sendMessage(MessageConfig.OTHER_RELOADED.component())

        return true
    }

    override fun complete(sender: CommandSender, args: Array<String>): List<String> {
        return emptyList()
    }

}