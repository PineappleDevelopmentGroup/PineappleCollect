package sh.miles.pineapplecollectors.meta

import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedObject
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter
import sh.miles.pineapple.util.serialization.exception.SerializedAdaptationException

object CollectorMetaAdapter : SerializedAdapter<CollectorMeta> {

    private val ID_KEY = "id"
    private val ITEM_KEY = "item"

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
        val spec = context.deserialize(element.getObjectOrNull(ITEM_KEY)!!, ItemSpec::class.java)

        return CollectorMeta(id, spec.buildSpec())
    }

    override fun serialize(meta: CollectorMeta, context: SerializedSerializeContext): SerializedElement {
        val serializedObject = SerializedElement.`object`()
        serializedObject.add(ID_KEY, meta.key)
        serializedObject.add(ITEM_KEY, context.serialize(meta.item))

        return serializedObject
    }

}