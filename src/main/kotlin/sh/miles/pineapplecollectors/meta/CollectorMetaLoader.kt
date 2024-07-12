package sh.miles.pineapplecollectors.meta

import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import sh.miles.pineapplecollectors.PineappleCollectorsPlugin
import sh.miles.pineapplecollectors.registry.CollectorMetaRegistry
import java.io.File

class CollectorMetaLoader {

    // Read the collector-metas.json file, locate files and create CollectorMeta based on recieved data

    private fun saveMetaFiles() {
        val plugin = PineappleCollectorsPlugin.plugin

        val collectorMetaFile = File(plugin.dataFolder, "collector-metas.json")
        if (!collectorMetaFile.exists()) plugin.saveResource("collector-metas.json", false)

        val metaExampleFile = File(File(plugin.dataFolder, "metas"), "collector-meta-example.json")
        if (!metaExampleFile.exists()) plugin.saveResource("metas/collector-meta-example.json", false)
    }


    fun load() {
        saveMetaFiles()
        val plugin = PineappleCollectorsPlugin.plugin
        val dataFolder = plugin.dataFolder
        val collectorMetaJson =
            JsonParser.parseReader(File(dataFolder, "collector-metas.json").reader(Charsets.UTF_8))
        val parent = collectorMetaJson.asJsonObject

        val metaFileArray = parent.getAsJsonArray("metas")

        for (element in metaFileArray) {
            if (element.isJsonObject) {
                plugin.logger.warning("Found invalid file path `$element` in `collector-metas.json`")
                continue
            }

            val primitive = element.asJsonPrimitive
            if (!primitive.isString) {
                plugin.logger.warning("Found invalid file path `$element` in `collector-metas.json`")
                continue
            }

            val metaFile = File(dataFolder, primitive.asString)
            if (metaFile.isDirectory) {
                plugin.logger.warning("Found invalid file path `$primitive` in `collector-metas.json`")
                continue
            }

            if (!metaFile.exists()) {
                plugin.logger.warning("Found invalid file path `$primitive` in `collector-metas.json`")
                continue
            }


            loadFile(metaFile)
        }
    }

    private fun loadFile(file: File) {
        val collectorMeta = PineappleCollectorsPlugin.plugin.json.gson.fromJson<CollectorMeta>(file.reader(Charsets.UTF_8), object : TypeToken<CollectorMeta>() {}.type)

        PineappleCollectorsPlugin.plugin.logger.info("Loaded `${file.name} as a CollectorMeta")

        CollectorMetaRegistry.register(collectorMeta)
    }
}