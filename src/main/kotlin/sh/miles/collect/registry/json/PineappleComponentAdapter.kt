package sh.miles.collect.registry.json

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.json.JsonAdapter
import java.lang.reflect.Type

object PineappleComponentAdapter : JsonAdapter<PineappleComponent> {

    override fun serialize(component: PineappleComponent, type: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(component.source)
    }

    override fun deserialize(
        element: JsonElement, type: Type, context: JsonDeserializationContext
    ): PineappleComponent {
        return PineappleChat.component(element.asString)
    }

    override fun getAdapterType(): Class<PineappleComponent> {
        return PineappleComponent::class.java
    }
}
