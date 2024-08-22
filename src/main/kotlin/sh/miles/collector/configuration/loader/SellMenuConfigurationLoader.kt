package sh.miles.collector.configuration.loader

import org.bukkit.plugin.Plugin
import sh.miles.collector.GlobalConfig
import sh.miles.collector.Registries
import sh.miles.collector.configuration.SellMenuConfiguration
import sh.miles.pineapple.json.JsonHelper
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.io.path.reader

class SellMenuConfigurationLoader(private val jsonHelper: JsonHelper, private val plugin: Plugin) :
    AbstractNestedLoader(plugin) {

    override val activationFile: String = "sell-menu-configurations.json"
    override val activationDir: String = "sell-menus"
    override val examples: List<String> = listOf(
        "default-configuration.json"
    )
    override val createExamples: Boolean = GlobalConfig.CREATE_EXAMPLES

    override fun loadFile(path: Path) {
        val menu = jsonHelper.gson.fromJson(path.reader(Charsets.UTF_8), SellMenuConfiguration::class.java)
        plugin.logger.info("loaded \"${path.pathString}\" as Sell Menu Configuration")
        Registries.SELL_MENU.register(menu)
    }
}
