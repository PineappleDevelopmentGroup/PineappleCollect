package sh.miles.collector.configuration.loader

import org.bukkit.plugin.Plugin
import sh.miles.collector.GlobalConfig
import sh.miles.collector.configuration.CollectorConfiguration
import sh.miles.collector.Registries
import sh.miles.pineapple.json.JsonHelper
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.io.path.reader

class CollectorConfigurationLoader(private val jsonHelper: JsonHelper, private val plugin: Plugin) :
    AbstractNestedLoader(plugin) {
    override val activationFile: String = "collector-configurations.json"
    override val activationDir: String = "collectors"
    override val examples: List<String> = listOf(
        "default-configuration.json"
    )
    override val createExamples: Boolean = GlobalConfig.CREATE_EXAMPLES

    override fun loadFile(path: Path) {
        val configuration = jsonHelper.gson.fromJson(path.reader(Charsets.UTF_8), CollectorConfiguration::class.java)
        plugin.logger.info("Loaded \"${path.pathString}\" as Collector Configuration")
        Registries.COLLECTOR.register(configuration)
    }
}
