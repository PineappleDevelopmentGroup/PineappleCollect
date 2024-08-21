package sh.miles.collector.util

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sh.miles.collector.tile.CollectorTile
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.tiles.api.Tiles

object CollectorDebugUtil {

    fun getTargetedCollector(player: Player): CollectorTile? {
        val target = player.getTargetBlockExact(10)
        if (target == null) {
            player.sendMessage("You must target a block to use this command")
            return null
        }

        val targetLocation = target.location
        val possibleTile = Tiles.getInstance().getTile(targetLocation) { it is CollectorTile }
        if (possibleTile == null) {
            player.spigot().sendMessage(
                PineappleChat.parse(
                    "<red>No collector found at location (${targetLocation.x}, ${targetLocation.y}, ${targetLocation.z})"
                )
            )
            return null
        }

        return possibleTile as CollectorTile
    }

    fun printAllInChunk(sender: CommandSender, chunk: Chunk) {
        val tiles = Tiles.getInstance().getTiles(chunk) { it is CollectorTile }
        val messageBuilder = StringBuilder()
        for (entry in tiles) {
            messageBuilder.append("<gold>").append(entry.key.toString()).append("<gray>: ")
                .append((entry.value as CollectorTile).configuration.id).append("\n")
        }

        if (messageBuilder.isEmpty()) {
            messageBuilder.append("<red>No collectors found in this chunk")
        }
        if (messageBuilder.endsWith("\n")) {
            messageBuilder.setLength(messageBuilder.length - 1)
        }
        sender.spigot().sendMessage(PineappleChat.parse(messageBuilder.toString()))
    }

    fun printDebug(sender: CommandSender, location: Location) {
        val possibleTile = Tiles.getInstance().getTile(location) { it is CollectorTile }
        if (possibleTile == null) {
            sender.spigot().sendMessage(
                PineappleChat.parse(
                    "<red>No collector found at location (${location.x},${location.y},${location.z})"
                )
            )
            return
        }
        val tile = possibleTile as CollectorTile
        val player = Bukkit.getOfflinePlayer(tile.owner!!).name
        val accessors = tile.accessWhitelist.map { Bukkit.getOfflinePlayer(it).name }.toList()
        val configurationId = tile.configuration.id
        val items = tile.stackContainer.getContents().map {
            if (it.type.isAir) return@map "Air"
            return@map it.itemMeta!!.displayName + " X" + it.amount
        }
        sender.spigot().sendMessage(
            PineappleChat.parse(
                """
                        <gold>Owner<gray>:<green> $player
                        <gold>Accessors<gray>:<dark_green> $accessors
                        <gold>Configuration Id<gray>:<light_purple> $configurationId
                        <gold>Items<gray>:<white> $items
                    """.trimIndent()
            )
        )
    }
}
