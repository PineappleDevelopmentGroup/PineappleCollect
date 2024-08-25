package sh.miles.collector

import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.config.annotation.Comment
import sh.miles.pineapple.config.annotation.ConfigPath

object GlobalConfig {

    @ConfigPath("create-examples")
    @Comment("Determines whether or not the example files should be attempted to be created")
    var CREATE_EXAMPLES = true

    @ConfigPath("pickup-non-sellables")
    @Comment("Determine whether a collector can pickup items that can not be sold in the shop")
    var ALLOW_PICKUP_NON_SELLABLES = true

    @ConfigPath("display-refresh-time")
    @Comment("The time between refreshing the display entity on top of collectors in ticks")
    var DISPLAY_REFRESH_TIME = 100

    @ConfigPath("auto-sell-time")
    @Comment("The time between selling items in ticks")
    var AUTO_SELL_COOLDOWN = 300

    @ConfigPath("messages.already-have-upgrade")
    @Comment("Sent if the player already has an upgrade")
    var ALREADY_HAVE_UPGRADE: PineappleComponent =
        PineappleChat.component("<red>You already have <\$upgrade> maxed out at level <\$level>")

    @ConfigPath("messages.not-enough-money")
    @Comment("Sent if the player doesn't have enough money for the upgrade")
    var NOT_ENOUGH_MONEY: PineappleComponent =
        PineappleChat.component("<red>You do not have enough money to buy <\$upgrade> you have <\$player_balance> you need <\$upgrade_cost>")

    @ConfigPath("messages.upgrade-purchased")
    @Comment("Sent if a player purchases an upgrade for a specific price")
    var UPGRADE_PURCHASED: PineappleComponent =
        PineappleChat.component("<green>You successfully purchased <\$upgrade> for <\$price>")

    @ConfigPath("anvil-title")
    @Comment("The name of the text collecting anvil title")
    var ANVIL_TITLE: PineappleComponent = PineappleChat.component("<gray>Enter Text")

    @ConfigPath("messages.offline-player-web-request-failed")
    @Comment("Shown to a player if a web request for information regarding a certain offline player isn't found")
    var OFFLINE_PLAYER_WEB_REQUEST_FAILED: PineappleComponent =
        PineappleChat.component("<red>An issue occurred when retrieving information for the player with the name <\$name>")

    @ConfigPath("messages.not-whitelisted")
    @Comment("Shown to a player if they are not whitelisted on a collector")
    var NOT_WHITELISTED: PineappleComponent =
        PineappleChat.component("<red>You are not whitelisted on this collector so you can not access it")

    @ConfigPath("messages.collector-already-in-this-chunk")
    @Comment("Shown to a player if they try to place a collector and there is already one in teh chunk")
    var COLLECTOR_ALREADY_PLACED: PineappleComponent =
        PineappleChat.component("<red>You can not place this here because there is already a collector in this chunk")


}
