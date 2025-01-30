package sh.miles.collector.upgrade.level

import net.kyori.adventure.text.Component
import sh.miles.pineapple.chat.PineappleChat

interface UpgradeLevel {
    val level: Int
    val price: Double

    fun textMutator(input: String, currentLevel: Int): Component {
        return PineappleChat.parse(input, replacements(currentLevel))
    }

    fun textMutator(
        input: String, currentLevel: Int, mutate: (MutableMap<String, Any>) -> MutableMap<String, Any>
    ): Component {
        return PineappleChat.parse(input, mutate.invoke(replacements(currentLevel)))
    }

    fun replacements(currentLevel: Int): MutableMap<String, Any>

    fun prefixReplacements(prefix: String, currentLevel: Int): MutableMap<String, Any> {
        return replacements(currentLevel).mapKeys { "${prefix}_${it.key}" }.toMutableMap()
    }
}
