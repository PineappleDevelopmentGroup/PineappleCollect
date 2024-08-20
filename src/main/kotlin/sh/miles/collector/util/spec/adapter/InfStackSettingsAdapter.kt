package sh.miles.collector.util.spec.adapter

import net.md_5.bungee.api.chat.BaseComponent
import sh.miles.crown.infstacks.InfStackSettings
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter

object InfStackSettingsAdapter : SerializedAdapter<InfStackSettings> {

    private const val MAX_STACK = "max_stack_size"
    private const val LORE = "lore"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): InfStackSettings {
        val parent = element.asObject
        val maxStackSize = parent.getPrimitive(MAX_STACK).map { it.asLong }.orElse(Long.MAX_VALUE)
        val lore = parent.getArray(LORE).map {
            val chatLore = mutableListOf<PineappleComponent>()
            for (primitive in it) {
                chatLore.add(PineappleChat.component(primitive.asPrimitive.asString))
            }
            return@map chatLore
        }.orThrow()

        return InfStackSettings(lore, maxStackSize) { components, currentAmount, display, comparator, _ ->
            val baseLore: MutableList<BaseComponent> = PineappleLib.getNmsProvider().getItemLore(comparator).toMutableList()
            for (component in components) {
                baseLore.add(
                    component.component(
                        mapOf(
                            "amount" to currentAmount,
                            "max_amount" to (if (maxStackSize == Long.MAX_VALUE) "∞" else maxStackSize)
                        )
                    )
                )
            }
            val modifiedDisplay = PineappleLib.getNmsProvider().setItemLore(display, baseLore)
            return@InfStackSettings modifiedDisplay
        }
    }

    override fun serialize(settings: InfStackSettings, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("can not currently serialize InfStackSettingsAdapter")
    }

    override fun getKey(): Class<*> {
        return InfStackSettings::class.java
    }
}
