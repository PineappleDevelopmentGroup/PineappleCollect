package sh.miles.collect.command.data.get

import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel

object DataGetCommand : Command(CommandLabel("get", "collect.command.data.get")) {

    init {
        registerSubcommand(DataGetGlobalCommand)
        registerSubcommand(DataGetChunkCommand)
    }

}
