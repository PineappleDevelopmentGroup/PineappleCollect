package sh.miles.collect.command

import org.bukkit.command.CommandSender
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

internal object GiveCommand : Command(CommandLabel("give", "collect.command.give")) {

    override fun execute(sender: CommandSender, args: Array<String>): Boolean {
        return super.execute(sender, args)
    }

    override fun complete(sender: CommandSender, args: Array<String>): List<String> {
        return super.complete(sender, args)
    }

}
