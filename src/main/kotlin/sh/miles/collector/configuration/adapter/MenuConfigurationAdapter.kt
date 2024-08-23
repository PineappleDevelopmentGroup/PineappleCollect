package sh.miles.collector.configuration.adapter

import sh.miles.collector.configuration.MenuConfiguration
import sh.miles.collector.util.spec.GuiItemSpec
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
    private const val ITEMS = "items"
    private const val BACKGROUND_ITEM = "background_item"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): MenuConfiguration {
        val parent = element.asObject
        val id =
            parent.getPrimitiveOrNull(ID)?.asString ?: throw IllegalStateException("Can not find required field $ID")
        val title = PineappleChat.component(
            parent.getPrimitiveOrNull(TITLE)?.asString ?: throw IllegalStateException("Missing required field $TITLE")
        )
        val viewRows = parent.getPrimitiveOrNull(VIEW_ROWS)?.asInt
            ?: throw IllegalStateException("Missing required field $VIEW_ROWS")
        val itemMap = parent.getArrayOrNull(ITEMS)?.map {
            context.deserialize(it.asObject, GuiItemSpec::class.java)
        }?.associateBy { it.slot } ?: throw IllegalStateException("Missing required field $ITEMS")
        val backgroundItem = context.deserialize(
            parent.getOrNull(BACKGROUND_ITEM) ?: throw IllegalStateException("Missing required field $BACKGROUND_ITEM"),
            ItemSpec::class.java
        )

        return MenuConfiguration(id, title, viewRows, itemMap, backgroundItem)
    }

    override fun serialize(obj: MenuConfiguration, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("can not currently serialize MenuConfiguration")
    }

    override fun getKey(): Class<*> {
        return MenuConfiguration::class.java
    }
}
