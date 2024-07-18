package sh.miles.pineapplecollectors.tiles.api

import sh.miles.pineapplecollectors.tiles.api.pos.WorldPos
import java.util.HashMap

class ChunkCache {

    private val tiles: MutableMap<WorldPos, Tile> = mutableMapOf()

    fun getTiles(): Map<WorldPos, Tile> {
        return HashMap(tiles)
    }
}