package sh.miles.collect.command.data

import sh.miles.collect.command.data.get.DataGetCommand
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

object DataCommand: Command(CommandLabel("data", "collect.command.data")) {

    init {
        registerSubcommand(DataGetCommand)
        registerSubcommand(DataRemoveCommand)
        registerSubcommand(DataRemoveChunkCommand)
    }

}
