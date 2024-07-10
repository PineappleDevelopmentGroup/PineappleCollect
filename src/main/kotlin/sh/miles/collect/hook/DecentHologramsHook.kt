package sh.miles.collect.hook

import org.bukkit.Location

object DecentHologramsHook {

    // Interact with DHAPI (static class)

    // TODO create hologram, returns boolean if it can create it
    // TODO modify hologram data
    // TODO delete hologram

    // TODO take position as input if we use it again
    // Design:
    // 1 line with customizable data (in a json config)
    // multiple lines with customizable data (in a json config)
    // in json config expose all possible values to a pineapple component
    fun createHologram(collectorLocation: Location): Boolean {
        TODO("Not implemented yet")
    }

}