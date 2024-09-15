package sh.miles.collector.upgrade.level

import net.md_5.bungee.api.chat.BaseComponent

interface UpgradeLevel {
    val level: Int
    val price: Double

    fun loreMutator(input: String, currentLevel: Int): BaseComponent
}
