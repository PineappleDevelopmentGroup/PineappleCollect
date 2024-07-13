package sh.miles.pineapplecollectors.meta

import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedObject
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter
import sh.miles.pineapple.util.serialization.exception.SerializedAdaptationException

object CollectorMetaAdapter : SerializedAdapter<CollectorMeta> {

    private val ID_KEY = "id"

    override fun getKey(): Class<*> {
        return CollectorMeta::class.java
    }

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): CollectorMeta {
        if (!element.isObject) {
            throw SerializedAdaptationException("CollectorMeta's can only be deserialized from an object")
        }

        element as SerializedObject

        // TODO use anomoly

        val id = element.getPrimitive(ID_KEY).orThrow().asString

        return CollectorMeta(id)
    }

    override fun serialize(meta: CollectorMeta, context: SerializedSerializeContext): SerializedElement {
        val serializedObject = SerializedElement.`object`()
        serializedObject.add(ID_KEY, meta.key)

        return serializedObject
    }

}