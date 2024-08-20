package sh.miles.collector.util.spec.adapter

import sh.miles.collector.util.spec.VectorSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter

object VectorSpecAdapter : SerializedAdapter<VectorSpec> {

    private const val X = "x"
    private const val Y = "y"
    private const val Z = "z"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): VectorSpec {
        val parent = element.asObject
        val x = parent.getPrimitive(X).map { it.asDouble }.orElse(0.0)
        val y = parent.getPrimitive(Y).map { it.asDouble }.orElse(0.0)
        val z = parent.getPrimitive(Z).map { it.asDouble }.orElse(0.0)
        return VectorSpec(x, y, z)
    }

    override fun serialize(spec: VectorSpec, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("can not currently serialize VectorSpec")
    }

    override fun getKey(): Class<*> {
        return VectorSpec::class.java
    }
}
