package sh.miles.collector.util

import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.command.CommandSender
import sh.miles.collector.tile.CollectorTile
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.tiles.api.Tiles

object CollectorDebugUtil {

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
            it.itemMeta!!.displayName + " X" + it.amount
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
