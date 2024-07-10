package sh.miles.collect.config

import sh.miles.pineapple.config.annotation.Comment
import sh.miles.pineapple.config.annotation.ConfigPath

object GlobalConfig {

    @ConfigPath("chunk.one-per")
    @Comment("Only allow 1 collector per chunk")
    var CHUNK_ONE_PER: Boolean = true

    @ConfigPath("chunk.one-per-collecting")
    @Comment("If chunk.one-per is true, only allows 1 collector to collect items in a chunk, Applies to range upgraded collectors")
    var CHUNK_ONE_PER_COLLECTING: Boolean = true

}