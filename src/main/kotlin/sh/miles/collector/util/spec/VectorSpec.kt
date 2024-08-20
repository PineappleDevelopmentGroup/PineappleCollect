package sh.miles.collector.util.spec

import org.bukkit.Location

data class VectorSpec(val x: Double, val y: Double, val z: Double) {

    fun modify(location: Location): Location {
        return location.clone().add(x, y, z)
    }

}
