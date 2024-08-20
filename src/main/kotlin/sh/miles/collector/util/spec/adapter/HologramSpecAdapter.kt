package sh.miles.collector.util.spec.adapter

import sh.miles.collector.util.spec.HologramSpec
import sh.miles.collector.util.spec.VectorSpec
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter

object HologramSpecAdapter : SerializedAdapter<HologramSpec> {

    private const val TEXT = "text"
    private const val VECTOR = "relative_position"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): HologramSpec {
        val parent = element.asObject
        val text = PineappleChat.component(parent.getArrayOrNull(TEXT)?.joinToString("\n") { it.asPrimitive.asString }
            ?: throw IllegalStateException("Can not find required field $TEXT"))
        val vector = parent.getObject(VECTOR).map { context.deserialize(it, VectorSpec::class.java) }
            .orElse(VectorSpec(0.0, 0.0, 0.0))

        return HologramSpec(text, vector)
    }

    override fun serialize(spec: HologramSpec, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("can not currently serialize HologramSpec")
    }

    override fun getKey(): Class<*> {
        return HologramSpec::class.java
    }
}
