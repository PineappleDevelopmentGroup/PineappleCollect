package sh.miles.collect.util.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.json.JsonAdapter
import java.lang.reflect.Type

object CollectorMenuSpecAdapter : JsonAdapter<CollectorMenuSpecAdapter.SpecDetails> {

    override fun serialize(details: SpecDetails, type: Type, context: JsonSerializationContext): JsonElement {
        throw UnsupportedOperationException("Can not serialize spec")
    }

    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): SpecDetails {
        val parent = element.asJsonObject;
        println(parent.get("pickup_item_location"))
        println(parent.get("upgrade_item_location"))
        return SpecDetails(
            context.deserialize(parent.get("background_item"), ItemSpec::class.java),
            context.deserialize(parent.get("sell_item"), ItemSpec::class.java),
            context.deserialize(parent.get("upgrades_item"), ItemSpec::class.java),
            context.deserialize(parent.get("pickup_item"), ItemSpec::class.java),
            parent.get("sell_item_location").asInt,
            parent.get("pickup_item_location").asInt,
            parent.get("upgrade_item_location").asInt,
            PineappleChat.component(parent.get("upgrade_item_price_lore").asString)
        )
    }

    override fun getAdapterType(): Class<SpecDetails> {
        return SpecDetails::class.java
    }

    data class SpecDetails(
        val backgroundItem: ItemSpec,
        val sellItem: ItemSpec,
        val upgradeItem: ItemSpec,
        val pickupItem: ItemSpec,
        val sellItemLoc: Int,
        val pickupItemSlot: Int,
        val upgradeItemLoc: Int,
        val priceLore: PineappleComponent
    )
}
