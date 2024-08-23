package sh.miles.collector.util.spec.adapter

import sh.miles.collector.util.spec.GuiItemSpec
import sh.miles.collector.util.spec.SoundSpec
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter

object GuiItemSpecAdapter : SerializedAdapter<GuiItemSpec> {

    private const val SLOT = "slot"
    private const val ACTION = "action"
    private const val LINK = "link"
    private const val CLICK_SOUND = "click_sound"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): GuiItemSpec {
        val parent = element.asObject
        val itemSpec = context.deserialize(parent, ItemSpec::class.java)
        val slot = parent.getPrimitiveOrNull(SLOT)?.asInt ?: throw IllegalStateException("Missing required field $SLOT")
        val actionId =
            parent.getPrimitiveOrNull(ACTION)?.asString ?: throw IllegalStateException("Missing required field $ACTION")
        val link = parent.getPrimitiveOrNull(LINK)?.asString
        val clickSound = context.deserialize(
            parent.getOrNull(CLICK_SOUND) ?: throw IllegalStateException("Missing required field $CLICK_SOUND"),
            SoundSpec::class.java
        )

        return GuiItemSpec(itemSpec, slot, actionId, link, clickSound)
    }

    override fun serialize(obj: GuiItemSpec, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("can not currently serialize GuiItemSpec")
    }

    override fun getKey(): Class<*> {
        return GuiItemSpec::class.java
    }
}
