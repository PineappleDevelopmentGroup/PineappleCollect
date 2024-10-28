package sh.miles.collector.hook

import org.bukkit.plugin.Plugin
import sh.miles.pineapple.collection.registry.RegistryKey

interface SupportedPlugin : RegistryKey<String> {
    val name: String

    fun load(root: Plugin): Boolean
}
