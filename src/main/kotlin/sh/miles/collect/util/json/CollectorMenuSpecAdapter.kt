package sh.miles.collect.util.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.json.JsonAdapter
import java.lang.reflect.Type

object CollectorMenuSpecAdapter : JsonAdapter<CollectorMenuSpecAdapter.SpecDetails> {

    override fun serialize(details: SpecDetails, type: Type, context: JsonSerializationContext): JsonElement {
        throw UnsupportedOperationException("Can not serialize spec")
    }

    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): SpecDetails {
        val parent = element.asJsonObject;
        return SpecDetails(
            context.deserialize(parent.get("background_item"), ItemStack::class.java),
            context.deserialize(parent.get("sell_item"), ItemStack::class.java),
            context.deserialize(parent.get("upgrades_item"), ItemStack::class.java),
            parent.get("sell_item_location").asInt,
            parent.get("upgrade_item_location").asInt
        )
    }

    override fun getAdapterType(): Class<SpecDetails> {
        return SpecDetails::class.java
    }

    data class SpecDetails(
        val backgroundItem: ItemStack,
        val sellItem: ItemStack,
        val upgradeItem: ItemStack,
        val sellItemLoc: Int,
        val upgradeItemLoc: Int
    )
}
