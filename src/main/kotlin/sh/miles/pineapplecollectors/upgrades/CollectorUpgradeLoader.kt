package sh.miles.pineapplecollectors.upgrades

import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import sh.miles.pineapplecollectors.PineappleCollectorsPlugin
import sh.miles.pineapplecollectors.registry.CollectorUpgradeRegistry
import java.io.File

class CollectorUpgradeLoader {

    // Reads collector-upgrades.json and locates all files, reads contents of each file and constructs a CollectorUpgrade with the specified sub upgrade added

    private fun saveUpgradeFiles() {
        val plugin = PineappleCollectorsPlugin.plugin

        val collectorUpgradeFile = File(plugin.dataFolder, "collector-upgrades.json")
        if (!collectorUpgradeFile.exists()) plugin.saveResource("collector-upgrades.json", false)

        val upgradesExampleFile = File(File(plugin.dataFolder, "upgrades"), "collector-upgrade-example.json")
        if (!upgradesExampleFile.exists()) plugin.saveResource("upgrades/collector-upgrade-example.json", false)
    }


    fun load() {
        saveUpgradeFiles()
        val plugin = PineappleCollectorsPlugin.plugin
        val dataFolder = plugin.dataFolder
        val collectorMetaJson =
            JsonParser.parseReader(File(dataFolder, "collector-upgrades.json").reader(Charsets.UTF_8))
        val parent = collectorMetaJson.asJsonObject

        val metaFileArray = parent.getAsJsonArray("upgrades")

        for (element in metaFileArray) {
            if (element.isJsonObject) {
                plugin.logger.warning("Found invalid file path `$element` in `collector-upgrades.json`")
                continue
            }

            val primitive = element.asJsonPrimitive
            if (!primitive.isString) {
                plugin.logger.warning("Found invalid file path `$element` in `collector-upgrades.json`")
                continue
            }

            val upgradeFile = File(dataFolder, primitive.asString)
            if (upgradeFile.isDirectory) {
                plugin.logger.warning("Found invalid file path `$primitive` in `collector-upgrades.json`")
                continue
            }

            if (!upgradeFile.exists()) {
                plugin.logger.warning("Found invalid file path `$primitive` in `collector-upgrades.json`")
                continue
            }


            loadFile(upgradeFile)
        }
    }

    private fun loadFile(file: File) {
        val collectorMeta = PineappleCollectorsPlugin.plugin.json.gson.fromJson<CollectorUpgrade>(file.reader(Charsets.UTF_8), object : TypeToken<CollectorUpgrade>() {}.type)

        PineappleCollectorsPlugin.plugin.logger.info("Loaded `${file.name} as a CollectorUpgrade")

        CollectorUpgradeRegistry.register(collectorMeta)
    }
}