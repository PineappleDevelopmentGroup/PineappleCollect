package sh.miles.collector.util.spec.adapter

import sh.miles.collector.util.spec.GuiItemSpec
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter
import sh.miles.pineapple.util.spec.SoundSpec

object GuiItemSpecAdapter : SerializedAdapter<GuiItemSpec> {

    private const val SLOT = "slot"
    private const val ACTION = "action"
    private const val ARGUMENTS = "args"
    private const val CLICK_SOUND = "click_sound"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): GuiItemSpec {
        val parent = element.asObject
        val itemSpec = context.deserialize(parent, ItemSpec::class.java)
        val slot = parent.getPrimitive(SLOT).orThrow("Missing required field $SLOT").asInt
        val actionId = parent.getPrimitive(ACTION).orThrow("Missing required field $ACTION").asString
        val arguments = parent.getArray(ARGUMENTS).orElse(SerializedElement.array()).stream().map { it.asPrimitive.asString }.toList()
        val clickSound = context.deserialize(
            parent.getOrNull(CLICK_SOUND) ?: throw IllegalStateException("Missing required field $CLICK_SOUND"),
            SoundSpec::class.java
        )

        return GuiItemSpec(itemSpec, slot, actionId, arguments, clickSound)
    }

    override fun serialize(obj: GuiItemSpec, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("can not currently serialize GuiItemSpec")
    }

    override fun getKey(): Class<*> {
        return GuiItemSpec::class.java
    }
}
