package sh.miles.pineapplecollectors.tiles.usage

import org.bukkit.event.entity.EntityDeathEvent
import sh.miles.pineapplecollectors.tiles.api.Tiles
import sh.miles.pineapplecollectors.tiles.api.pos.ChunkPos
import sh.miles.pineapplecollectors.tiles.api.pos.WorldPos
import java.util.*

class EntityDeathListener {


    fun onDeath(event: EntityDeathEvent) {
        val tile = Tiles.tileCache.get(ChunkPos(UUID.randomUUID(), 1, 1))?.getTiles()?.get(WorldPos(UUID.randomUUID(), 1, 1, 1)) as CollectorTile
        tile.someValue += 1
    }
}