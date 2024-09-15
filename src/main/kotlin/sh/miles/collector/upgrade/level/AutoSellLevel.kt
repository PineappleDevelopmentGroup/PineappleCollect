package sh.miles.collector.upgrade.level

import net.md_5.bungee.api.chat.BaseComponent
import sh.miles.pineapple.chat.PineappleChat

data class AutoSellLevel(override val level: Int, override val price: Double, val ticks: Int) : UpgradeLevel {
    override fun loreMutator(input: String, currentLevel: Int): BaseComponent {
        return PineappleChat.parse(
            input,
            mutableMapOf<String, Any>(
                "level" to currentLevel,
                "price" to price,
                "ticks" to ticks
            )
        )
    }
}
