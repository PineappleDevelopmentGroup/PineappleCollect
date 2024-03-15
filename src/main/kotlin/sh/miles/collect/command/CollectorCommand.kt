package sh.miles.collect.command

import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

object CollectorCommand : Command(CommandLabel("collect", "collect.command")) {

    init {
        registerSubcommand(GiveCommand)
    }

}
