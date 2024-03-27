package sh.miles.collect.util

import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.config.annotation.Comment
import sh.miles.pineapple.config.annotation.ConfigPath

object MessageConfig {

    @ConfigPath("upgrades.not-enough-money")
    @Comment("Use <\$amount> as a placeholder for the money required, formatting docs: https://docs.miles.sh/pineapple-chat/bungee")
    var UPGRADE_NOT_ENOUGH_MONEY: PineappleComponent = PineappleChat.component("<red>You dont have enough money for this, you need <\$amount>")

    @ConfigPath("other.reloaded")
    var OTHER_RELOADED: PineappleComponent = PineappleChat.component("<green>The message config has been reloaded")

    @ConfigPath("other.player-only-command")
    var OTHER_PLAYER_ONLY_COMMAND: String = "This command is for a player only"

    @ConfigPath("other.player-not-online")
    var OTHER_PLAYER_NOT_ONLINE = PineappleChat.component("<red>Specified player is not online")

    @ConfigPath("other.give-command-player-usage")
    var OTHER_GIVE_COMMAND_USAGE = PineappleChat.component("<red>/collect give <player> <id>")

    @ConfigPath("other.player-not-online")
    var OTHER_INVALID_TEMPLATE_ID = PineappleChat.component("<red>Invalid template id")

    @ConfigPath("collector.already-in-chunk")
    @Comment("use <\$x>, <\$y>, <\$z> to represent the coordinates for the existing collector")
    var COLLECTOR_ALREADY_IN_CHUNK: PineappleComponent = PineappleChat.component("<red>There is already a collector in this chunk at <\$x>;<\$y>;<\$z>")

    @ConfigPath("collector.sold-all")
    @Comment("Use <\$amount> for the money amount, $ sign will not be prefixed")
    var COLLECTOR_SOLD_ALL: PineappleComponent = PineappleChat.component("<green>Sold all items in the collector for $<\$amount>")

    @ConfigPath("collector.upgraded")
    @Comment("Use <\$id> for the upgraded id and <\$title> as the title")
    var COLLECTOR_UPGRADED: PineappleComponent = PineappleChat.component("<green>Upgraded your collector to <\$title>")
}