package sh.miles.collector.configuration.loader

import org.bukkit.plugin.Plugin
import sh.miles.collector.GlobalConfig
import sh.miles.collector.Registries
import sh.miles.collector.configuration.MenuConfiguration
import sh.miles.pineapple.json.JsonHelper
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.io.path.reader

class MenuConfigurationLoader(private val jsonHelper: JsonHelper, private val plugin: Plugin) :
    AbstractNestedLoader(plugin) {

    override val activationFile: String = "menu-configurations.json"
    override val activationDir: String = "menus"
    override val examples: List<String> = listOf(
        "default-configuration.json"
    )
    override val createExamples: Boolean = GlobalConfig.CREATE_EXAMPLES

    override fun loadFile(path: Path) {
        val menu = jsonHelper.gson.fromJson(path.reader(Charsets.UTF_8), MenuConfiguration::class.java)
        plugin.logger.info("loaded \"${path.pathString}\" as Menu Configuration")
        Registries.MENU.register(menu)
    }
}
