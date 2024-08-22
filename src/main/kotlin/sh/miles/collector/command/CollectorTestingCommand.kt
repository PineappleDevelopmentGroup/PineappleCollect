package sh.miles.collector.command

import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sh.miles.collector.tile.CollectorTile
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.command.Command
import sh.miles.pineapple.command.CommandLabel
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapple.tiles.api.Tiles

object CollectorTestingCommand : Command(CommandLabel("testing", "collector.command.testing")) {

    init {
        registerSubcommand(CollectorTestingItem)
    }

    private object CollectorTestingItem : Command(CommandLabel("additem", "collector.command.testing.additem")) {
        override fun execute(sender: CommandSender, args: Array<out String>): Boolean {
            if (sender !is Player) {
                sender.sendMessage("Only players can send this command")
                return true
            }

            val target = sender.getTargetBlockExact(10)
            if (target == null) {
                sender.sendMessage("You must target a block to use this command")
                return true
            }

            val targetLocation = target.location
            val possibleTile = Tiles.getInstance().getTile(targetLocation) { it is CollectorTile }
            if (possibleTile == null) {
                sender.spigot().sendMessage(
                    PineappleChat.parse(
                        "<red>No collector found at location (${targetLocation.x}, ${targetLocation.y}, ${targetLocation.z})"
                    )
                )
                return true
            }
            val tile = possibleTile as CollectorTile

            if (!tile.addItem(
                    ItemBuilder.of(Material.OAK_PLANKS).name(PineappleChat.parse("<green>Woah this is sussy")).build()
                )
            ) throw IllegalStateException("Unable to add item to container for some reason")
            return true
        }
    }
}
