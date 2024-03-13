package sh.miles.collect.data

import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*

data class Position(val uuid: UUID, val x: Int, val y: Int, val z: Int) {
    fun toLocation(): Location {
        return Location(Bukkit.getWorld(uuid), x.toDouble(), y.toDouble(), z.toDouble())
    }
}
