package sh.miles.collector.configuration.adapter

import sh.miles.collector.configuration.CollectorConfiguration
import sh.miles.crown.infstacks.InfStackFactory
import sh.miles.crown.infstacks.InfStackSettings
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter
import sh.miles.pineapple.util.spec.HologramSpec
import sh.miles.pineapple.util.spec.SoundSpec

object CollectorConfigurationAdapter : SerializedAdapter<CollectorConfiguration> {

    private const val ID = "id"
    private const val MENU_ID = "menu_configuration"
    private const val STORAGE_SLOTS = "storage_slots"
    private const val HOLOGRAM = "hologram"
    private const val ITEM = "item"
    private const val PLACE_SOUND = "place_sound"
    private const val BREAK_SOUND = "break_sound"
    private const val STACK_SETTINGS = "stacking_settings"

    override fun deserialize(
        element: SerializedElement,
        context: SerializedDeserializeContext
    ): CollectorConfiguration {
        val parent = element.asObject
        val id =
            parent.getPrimitiveOrNull(ID)?.asString ?: throw IllegalStateException("Can not find required field $ID")
        val menuId = parent.getPrimitiveOrNull(MENU_ID)?.asString
            ?: throw IllegalStateException("Can not find required field $MENU_ID")
        val storageSlots = parent.getPrimitiveOrNull(STORAGE_SLOTS)?.asInt
            ?: throw IllegalStateException("Can not find required field $STORAGE_SLOTS")
        val hologramSpec = context.deserialize(
            parent.getOrNull(HOLOGRAM) ?: throw IllegalStateException("Can not find required field $HOLOGRAM"),
            HologramSpec::class.java
        )
        val itemSpec = context.deserialize(
            parent.getOrNull(ITEM) ?: throw IllegalStateException("Can not find required field $ITEM"),
            ItemSpec::class.java
        )
        val placeSoundSpec = context.deserialize(
            parent.getOrNull(PLACE_SOUND) ?: throw IllegalStateException("Can not find required field $PLACE_SOUND"),
            SoundSpec::class.java
        )
        val breakSoundSpec = context.deserialize(
            parent.getOrNull(BREAK_SOUND) ?: throw IllegalStateException("Can not find required field $BREAK_SOUND"),
            SoundSpec::class.java
        )
        val infStackSettings = context.deserialize(
            parent.getOrNull(STACK_SETTINGS)
                ?: throw IllegalStateException("Can not find required field $STACK_SETTINGS"),
            InfStackSettings::class.java
        )
        return CollectorConfiguration(
            id,
            menuId,
            storageSlots,
            hologramSpec,
            itemSpec,
            placeSoundSpec,
            breakSoundSpec,
            InfStackFactory(infStackSettings)
        )
    }

    override fun serialize(
        configuration: CollectorConfiguration,
        context: SerializedSerializeContext
    ): SerializedElement {
        throw UnsupportedOperationException("can not currently serialize collector configuration")
    }

    override fun getKey(): Class<*> {
        return CollectorConfiguration::class.java
    }
}
