package sh.miles.collect.hook.skyblock

import com.bgsoftware.superiorskyblock.api.island.Island
import org.bukkit.entity.Player

interface AbstractSkyblockHook {

    fun canPlace(island: Island, player: Player): Boolean
}