package sh.miles.collect.registry.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import sh.miles.collect.collector.template.CollectorTemplate
import sh.miles.collect.collector.template.CollectorTemplateUpgradeData
import sh.miles.collect.util.CollectUtils
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.function.Option
import sh.miles.pineapple.json.JsonAdapter
import java.lang.reflect.Type

object CollectorTemplateAdapter : JsonAdapter<CollectorTemplate> {
    override fun serialize(
        template: CollectorTemplate, type: Type, serializationContext: JsonSerializationContext
    ): JsonElement {
        throw UnsupportedOperationException("Can not serialize CollectorTemplates")
    }

    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): CollectorTemplate {
        val parent = element.asJsonObject;
        val id = PineappleLib.getAnomalyFactory().create()
            .message("CollectorTemplates must have an \"id\" field e.g. \"id\": \"standard\"")
            .run { parent.get("id").asString }
            .hard(javaClass, "deserialize")
            .orThrow()

        val title = PineappleLib.getAnomalyFactory().create()
            .message("CollectorTemplates must have a \"title\" field e.g. \"title\": \"My Title\"")
            .run { context.deserialize<PineappleComponent>(parent.get("title"), PineappleComponent::class.java) }
            .hard(javaClass, "deserialize")
            .orThrow()

        val blockEntity = PineappleLib.getAnomalyFactory().create()
            .message("CollectorTemplates must have a \"block_entity\" field e.g. \"block_entity\": \"lectern\"")
            .run {
                val material = Material.matchMaterial(parent.get("block_entity").asString)
                    ?: throw throw IllegalArgumentException("the given material is null")

                if (CollectUtils.BLOCK_ENTITIES.contains(material)) {
                    return@run material
                }

                throw IllegalArgumentException("the given material is not a block entity")
            }.hard(javaClass, "deserialize").orThrow()

        val item = PineappleLib.getAnomalyFactory().create()
            .message("CollectorTemplates must have \"item\" field")
            .run { context.deserialize<ItemStack>(parent.get("item"), ItemStack::class.java) }
            .hard(javaClass, "deserialize").orThrow()

        val size = PineappleLib.getAnomalyFactory().create()
            .message("CollectorTemplate must have \"size\" field")
            .run { parent.get("size").asInt }
            .hard(javaClass, "deserialize").orThrow()

        val hasContent = PineappleLib.getAnomalyFactory().create()
            .message("CollectorTemplate must have a \"has_content\" field")
            .run { context.deserialize<PineappleComponent>(parent.get("has_content"), PineappleComponent::class.java) }
            .hard(javaClass, "deserialize").orThrow()

        val hasUpgradeData = parent.has("upgrade_data")
        val upgradeData: Option<CollectorTemplateUpgradeData> = if (hasUpgradeData) {
            val upgradeDataObject = parent.get("upgrade_data").asJsonObject

            val sizeUpgradeKey = PineappleLib.getAnomalyFactory().create()
                .message("If an \"upgrade_data\" block is present it must contain a \"size_upgrade_key\" field")
                .run { upgradeDataObject.get("size_upgrade_key").asString }
                .hard(javaClass, "deserialize").orThrow()

            val sizeUpgradeCost = PineappleLib.getAnomalyFactory().create()
                .message("If an \"upgrade_data\" block is present it must contain a \"size_upgrade_cost\" field")
                .run { upgradeDataObject.get("size_upgrade_cost").asDouble }
                .hard(javaClass, "deserialize").orThrow()

            Option.some(CollectorTemplateUpgradeData(sizeUpgradeKey, sizeUpgradeCost))
        } else {
            Option.none()
        }

        return CollectorTemplate(id, blockEntity, title, item, size, upgradeData, hasContent)
    }

    override fun getAdapterType(): Class<CollectorTemplate> {
        return CollectorTemplate::class.java
    }
}
