package sh.miles.pineapplecollectors.tiles.api

import sh.miles.pineapplecollectors.tiles.api.pos.ChunkPos

class TileCache {

    private val tiles: MutableMap<ChunkPos, ChunkCache> = mutableMapOf()

    fun load(pos: ChunkPos, cache: ChunkCache) {
        tiles[pos] = cache
    }

    fun get(pos: ChunkPos): ChunkCache? {
        return tiles[pos]
    }
}