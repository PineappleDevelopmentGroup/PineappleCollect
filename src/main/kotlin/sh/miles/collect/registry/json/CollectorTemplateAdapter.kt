package sh.miles.collect.registry.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import sh.miles.collect.collector.CollectorTemplate
import sh.miles.collect.util.CollectUtils
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.chat.PineappleComponent
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

        return CollectorTemplate(id, blockEntity, title, item)
    }

    override fun getAdapterType(): Class<CollectorTemplate> {
        return CollectorTemplate::class.java
    }
}
