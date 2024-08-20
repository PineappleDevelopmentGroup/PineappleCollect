package sh.miles.collector.util.spec

import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player

data class SoundSpec(val sound: Sound, val soundCategory: SoundCategory, val volume: Float, val pitch: Float) {
    constructor(sound: Sound, volume: Float, pitch: Float) : this(sound, SoundCategory.MASTER, volume, pitch)

    fun playSound(location: Location) {
        location.world!!.playSound(location, sound, soundCategory, volume, pitch)
    }

    fun playSound(player: Player) {
        player.playSound(player, sound, soundCategory, volume, pitch)
    }
}
