package sh.miles.collector.util.spec.adapter

import net.md_5.bungee.api.chat.BaseComponent
import sh.miles.collector.hook.EconomyShopHook
import sh.miles.crown.infstacks.InfStackSettings
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter
import java.text.DecimalFormat
import java.util.Locale

object InfStackSettingsAdapter : SerializedAdapter<InfStackSettings> {

    private val DECIMAL_FORMAT = DecimalFormat.getCurrencyInstance(Locale.US);

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
            val baseLore: MutableList<BaseComponent> =
                PineappleLib.getNmsProvider().getItemLore(comparator).toMutableList()
            for (component in components) {

                // Check an item can be sold, if it cannot show [UNSELLABLE] to avoid needing a message send in the menu if its attempted to be sold
                val sellPrice: String = if (!EconomyShopHook.canSell(comparator)) {
                    "[UNSELLABLE]"
                } else {
                    DECIMAL_FORMAT.format((EconomyShopHook.getItemPrice(comparator) * currentAmount))
                }

                baseLore.add(
                    component.component(
                        mapOf(
                            "amount" to currentAmount,
                            "max_amount" to (if (maxStackSize == Long.MAX_VALUE) "∞" else maxStackSize),
                            "sell_price" to sellPrice,
                            "withdraw_amount" to (if (currentAmount > 64) 64 else currentAmount)
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
