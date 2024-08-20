package sh.miles.pineapplecollectors.command

import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

object CollectCommandHandler : Command(CommandLabel("collectors", "pineapplecollectors.command")) {

    init {
        registerSubcommand(GiveCommand)
    }
}