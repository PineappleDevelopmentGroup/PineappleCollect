package sh.miles.collector

import sh.miles.pineapple.config.annotation.Comment
import sh.miles.pineapple.config.annotation.ConfigPath

object GlobalConfig {

    @ConfigPath("create-examples")
    @Comment("Determines whether or not the example files should be attempted to be created")
    var CREATE_EXAMPLES = true

}
