package sh.miles.collect.util

import java.util.UUID

data class ChunkPosition(val uuid: UUID, val x: Int, val z: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChunkPosition) return false

        if (uuid != other.uuid) return false
        if (x != other.x) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + x
        result = 31 * result + z
        return result
    }

}
