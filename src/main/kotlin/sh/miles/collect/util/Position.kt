package sh.miles.collect.util

import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.UUID

data class Position(val uuid: UUID, val x: Int, val y: Int, val z: Int) {
    fun toLocation(): Location {
        return Location(Bukkit.getWorld(uuid), x.toDouble(), y.toDouble(), z.toDouble())
    }

    fun chunkpos(): ChunkPosition {
        return ChunkPosition(uuid, x shl 4, z shl 4);
    }
}
