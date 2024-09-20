package sh.miles.collector.configuration.adapter

import sh.miles.collector.Registries
import sh.miles.collector.configuration.UpgradeConfiguration
import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.serialization.SerializedDeserializeContext
import sh.miles.pineapple.util.serialization.SerializedElement
import sh.miles.pineapple.util.serialization.SerializedSerializeContext
import sh.miles.pineapple.util.serialization.adapter.SerializedAdapter

object UpgradeConfigurationAdapter : SerializedAdapter<UpgradeConfiguration> {

    private const val ID = "id"
    private const val ICON = "icon"
    private const val DISABLED_ICON = "disabled_icon"
    private const val MAXED_ICON = "maxed_icon"
    private const val LEVELS = "levels"

    override fun deserialize(element: SerializedElement, context: SerializedDeserializeContext): UpgradeConfiguration {
        val parent = element.asObject
        val id = parent.getPrimitive(ID).orThrow("Can not find valid id field $ID").asString
        val icon = context.deserialize(
            parent.get(ICON).orThrow("Can not find required field $ICON for id $id"), ItemSpec::class.java
        )
        val disabledIcon = context.deserialize(
            parent.get(DISABLED_ICON).orThrow("Can not find required field for $ICON for id $id"), ItemSpec::class.java
        )
        val maxedIcon = context.deserialize(
            parent.get(MAXED_ICON).orThrow("Can not find required field $MAXED_ICON for id $id"), ItemSpec::class.java
        )
        val upgradeAction =
            Registries.UPGRADE_ACTION.get(id).orThrow("Can not find Upgrade Action with given ID $id for id $id")
        val levels = parent.getArray(LEVELS).orThrow("Can not find required levels array $LEVELS for id $id").stream()
            .map { upgradeAction.createLevel(it.asObject) }.toList()

        return UpgradeConfiguration(upgradeAction, levels, icon, disabledIcon, maxedIcon)
    }

    override fun serialize(obj: UpgradeConfiguration, context: SerializedSerializeContext): SerializedElement {
        throw UnsupportedOperationException("can not currently serialize UpgradeConfiguration")
    }

    override fun getKey(): Class<*> {
        return UpgradeConfiguration::class.java
    }
}
