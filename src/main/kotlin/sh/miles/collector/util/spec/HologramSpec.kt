package sh.miles.collector.util.spec

import org.bukkit.Location
import org.bukkit.entity.TextDisplay
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.chat.PineappleComponent

data class HologramSpec(val hologramText: PineappleComponent, val relative: VectorSpec) {

    fun spawn(location: Location, configuration: (TextDisplay) -> Unit): TextDisplay {
        return spawn(location, mutableMapOf(), configuration)
    }

    fun spawn(location: Location, replacements: Map<String, Any>, configuration: (TextDisplay) -> Unit): TextDisplay {
        return location.world!!.spawn(relative.modify(location), TextDisplay::class.java) {
            it.text = PineappleChat.parseLegacy(hologramText.source)
            configuration.invoke(it)
        }
    }
}
