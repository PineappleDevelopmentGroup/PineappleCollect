package sh.miles.collector.configuration.loader

import org.bukkit.plugin.Plugin
import sh.miles.collector.GlobalConfig
import sh.miles.collector.Registries
import sh.miles.collector.configuration.UpgradeConfiguration
import sh.miles.pineapple.json.JsonHelper
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.io.path.reader

class UpgradeConfigurationLoader(private val jsonHelper: JsonHelper, private val plugin: Plugin) : AbstractNestedLoader(plugin) {
    override val activationFile: String = "upgrade-configurations.json"
    override val activationDir: String = "upgrades"
    override val examples: List<String> = listOf(
        "autosell-configuration.json"
    )
    override val createExamples: Boolean = GlobalConfig.CREATE_EXAMPLES
    override fun loadFile(path: Path) {
        val upgrade = jsonHelper.gson.fromJson(path.reader(Charsets.UTF_8), UpgradeConfiguration::class.java)
        plugin.logger.info("loaded \"${path.pathString}\" as Upgrade Configuration")
        Registries.UPGRADE.register(upgrade)
    }
}
