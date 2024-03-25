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
}