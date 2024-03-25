package sh.miles.collect.registry.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import sh.miles.collect.collector.template.CollectorTemplateUpgradeData
import sh.miles.pineapple.json.JsonAdapter
import java.lang.reflect.Type

object CollectorTemplateUpgradeDataAdapter : JsonAdapter<CollectorTemplateUpgradeData> {


    override fun serialize(src: CollectorTemplateUpgradeData, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        throw UnsupportedOperationException("Can not serialize CollectorTemplates")
    }

    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): CollectorTemplateUpgradeData {
        val parent = element.asJsonObject

        val sizeUpgradeKey = parent.get("size_upgrade_key").asString
        val sizeUpgradeCost = parent.get("size_upgrade_cost").asDouble

        return CollectorTemplateUpgradeData(sizeUpgradeKey, sizeUpgradeCost)
    }

    override fun getAdapterType(): Class<CollectorTemplateUpgradeData> {
        return CollectorTemplateUpgradeData::class.java
    }
}