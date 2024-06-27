package sh.miles.collect.util

import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.UUID

data class Position(val uuid: UUID, val x: Int, val y: Int, val z: Int) {
    fun toLocation(): Location {
        return Location(Bukkit.getWorld(uuid), x.toDouble(), y.toDouble(), z.toDouble())
    }

    fun chunkpos(): ChunkPosition {
        return ChunkPosition(uuid, x shr 4, z shr 4);
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Position) return false

        if (uuid != other.uuid) return false
        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }

    override fun toString(): String {
        return "Position(uuid=$uuid, x=$x, y=$y, z=$z)"
    }


    companion object {
        fun fromLocation(location: Location): Position {
            return Position(location.world!!.uid, location.blockX, location.blockY, location.blockZ)
        }
    }
}
