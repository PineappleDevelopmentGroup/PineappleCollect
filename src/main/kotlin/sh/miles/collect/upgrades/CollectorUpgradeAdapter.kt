package sh.miles.collect.upgrades

import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedObject
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter
import sh.miles.pineapple.util.serialization.exception.SerializedAdaptationException

object CollectorUpgradeAdapter : SerializedAdapter<CollectorUpgrade> {

    private val ID_KEY = "id"

    override fun getKey(): Class<*> {
        return CollectorUpgrade::class.java
    }

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): CollectorUpgrade {
        if (!element.isObject) {
            throw SerializedAdaptationException("CollectorUpgrade's can only be deserialized from an object")
        }

        element as SerializedObject

        // TODO use anomoly

        val id = element.getPrimitive(ID_KEY).orThrow().asString

        return CollectorUpgrade(id, listOf())
    }

    override fun serialize(meta: CollectorUpgrade, context: SerializedSerializeContext): SerializedElement {
        val serializedObject = SerializedElement.`object`()
        serializedObject.add(ID_KEY, meta.id)

        return serializedObject
    }

}