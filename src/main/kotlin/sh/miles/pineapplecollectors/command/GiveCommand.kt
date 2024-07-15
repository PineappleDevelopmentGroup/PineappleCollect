package sh.miles.pineapplecollectors.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineapplecollectors.collector.tile.CollectorTileItemFactory

object GiveCommand : Command(CommandLabel("give", "pineapplecollectors.command.give")) {

    // TODO allow for player names
    // collectors give id
    override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
        if (args.isEmpty()) return true
        if (sender !is Player) return true

        sender.inventory.addItem(CollectorTileItemFactory.create(args[0]).orThrow())

        return true
    }
}