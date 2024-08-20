package sh.miles.collector.util.spec.adapter

import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.SoundCategory
import sh.miles.collector.util.spec.SoundSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter

object SoundSpecAdapter : SerializedAdapter<SoundSpec> {

    private const val SOUND = "sound"
    private const val SOUND_CATEGORY = "category"
    private const val PITCH = "pitch"
    private const val VOLUME = "volume"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): SoundSpec {
        val parent = element.asObject
        val sound = Registry.SOUNDS.get(
            NamespacedKey.fromString(
                parent.getPrimitiveOrNull(SOUND)?.asString
                    ?: throw IllegalStateException("Can not find required field $SOUND")
            )!!
        ) ?: throw IllegalStateException("Can not find sound with value ${parent.getPrimitiveOrNull(SOUND)!!.asString}")
        val category = SoundCategory.valueOf(parent.getPrimitiveOrNull(SOUND_CATEGORY)?.asString ?: "MASTER")
        val pitch = parent.getPrimitiveOrNull(PITCH)?.asDouble ?: 1.0
        val volume = parent.getPrimitiveOrNull(VOLUME)?.asDouble ?: 1.0

        return SoundSpec(sound, category, pitch.toFloat(), volume.toFloat())
    }

    override fun serialize(spec: SoundSpec, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("can not currently serialize SoundSpec")
    }

    override fun getKey(): Class<*> {
        return SoundSpec::class.java
    }
}
