package sh.miles.collector.configuration.loader

import com.google.gson.JsonParser
import org.bukkit.plugin.Plugin
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.notExists
import kotlin.io.path.pathString
import kotlin.io.path.reader
import kotlin.io.path.relativeTo

abstract class AbstractNestedLoader(private val plugin: Plugin) {
    protected abstract val activationFile: String
    protected abstract val activationDir: String
    protected abstract val examples: List<String>
    protected abstract val createExamples: Boolean

    fun load() {
        saveDefaults()
        val activationJson =
            JsonParser.parseReader(Path(plugin.dataFolder.path, activationFile).reader(Charsets.UTF_8)).asJsonObject
        val activateAll = activationJson.getAsJsonPrimitive("activate-all").asBoolean
        val activationList = activationJson.getAsJsonArray("active").map { it.asString }.toSet()

        val activationPath = Path(plugin.dataFolder.path, activationDir)
        for (child in Files.list(activationPath)) {
            val relativeChild = child.relativeTo(Path(plugin.dataFolder.path))
            if (activateAll) {
                plugin.logger.info("Activating file ${relativeChild.pathString}")
                loadFile(child)
            } else if (activationList.contains(relativeChild.pathString)) {
                plugin.logger.info("Activating file ${relativeChild.pathString}")
                loadFile(child)
            }
        }
    }

    private fun saveDefaults() {
        val loaderFile = Path(plugin.dataFolder.path, activationFile)

        if (loaderFile.notExists() && createExamples) {
            plugin.saveResource(activationFile, false)
        }

        for (example in examples) {
            val exampleFile = Path(plugin.dataFolder.path, activationDir, example)
            if (exampleFile.notExists() && createExamples) {
                plugin.saveResource("$activationDir/$example", false)
            }
        }
    }

    protected abstract fun loadFile(path: Path)

}
