package sh.miles.collector.configuration.adapter

import sh.miles.collector.configuration.MenuConfiguration
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter

object MenuConfigurationAdapter : SerializedAdapter<MenuConfiguration> {

    private const val ID = "id"
    private const val TITLE = "title"
    private const val VIEW_ROWS = "view_rows"
    private const val STORAGE_SLOTS = "storage_slots"
    private const val BACKGROUND_ITEM = "background_item"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): MenuConfiguration {
        val parent = element.asObject
        val id =
            parent.getPrimitiveOrNull(ID)?.asString ?: throw IllegalStateException("Can not find required field $ID")
        val title = PineappleChat.component(
            parent.getPrimitiveOrNull(TITLE)?.asString
                ?: throw IllegalStateException("Can not find required field $TITLE")
        )
        val viewRows = parent.getPrimitiveOrNull(VIEW_ROWS)?.asInt
            ?: throw IllegalStateException("Can not find required field $VIEW_ROWS")
        val storageSlots = parent.getArray(STORAGE_SLOTS).map {
            val slots = mutableSetOf<Int>()
            for (slot in it) {
                slots.add(slot.asPrimitive.asInt)
            }
            return@map slots.toSet()
        }.orElse(setOf())
        val backgroundItem = context.deserialize(
            parent.getOrNull(BACKGROUND_ITEM)
                ?: throw IllegalStateException("Can not find required field $BACKGROUND_ITEM"), ItemSpec::class.java
        )

        if (viewRows * 9 <= storageSlots.size) throw IllegalStateException("The viewRows are specified for size of ${viewRows * 9}, however storages size is configured for size ${storageSlots.size}. These configurations leave no room for the additional buttons in this menu")
        return MenuConfiguration(id, title, viewRows, storageSlots.size, storageSlots, backgroundItem)
    }

    override fun serialize(menu: MenuConfiguration, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("can not currently serialize MenuConfiguration")
    }

    override fun getKey(): Class<*> {
        return MenuConfiguration::class.java
    }

}
