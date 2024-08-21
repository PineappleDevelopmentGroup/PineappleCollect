package sh.miles.collector.tile

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import sh.miles.collector.Registries
import sh.miles.collector.configuration.CollectorConfiguration
import sh.miles.collector.menu.InfStackContainer
import sh.miles.collector.upgrade.CollectorUpgrade
import sh.miles.pineapple.PineappleLib
import sh.miles.pineapple.tiles.api.Tile
import sh.miles.pineapple.tiles.api.TileType
import java.util.UUID

class CollectorTile : Tile {

    var owner: UUID? = null
    lateinit var configuration: CollectorConfiguration
    lateinit var textDisplayUUID: UUID
    var accessWhitelist = mutableSetOf<UUID>()
        private set
    var upgrades = mutableMapOf<CollectorUpgrade, Int>()
    lateinit var stackContainer: InfStackContainer

    override fun save(container: PersistentDataContainer, excludeFields: MutableSet<String>?) {
        setIfIncludes(COLLECTOR_OWNER, PersistentDataType.STRING, owner.toString(), container, excludeFields)
        setIfIncludes(COLLECTOR_CONFIGURATION, PersistentDataType.STRING, configuration.id, container, excludeFields)
        setIfIncludes(
            COLLECTOR_DISPLAY_MSB,
            PersistentDataType.LONG,
            textDisplayUUID.mostSignificantBits,
            container,
            excludeFields
        )
        setIfIncludes(
            COLLECTOR_DISPLAY_LSB,
            PersistentDataType.LONG,
            textDisplayUUID.leastSignificantBits,
            container,
            excludeFields
        )
        setIfIncludes(
            COLLECTOR_ACCESSORS,
            PersistentDataType.LIST.strings(),
            accessWhitelist.map { it.toString() }.toList(),
            container,
            excludeFields
        )
        setIfIncludes(
            COLLECTOR_UPGRADES,
            PersistentDataType.TAG_CONTAINER,
            container,
            excludeFields
        ) {
            val upgradeContainer = it.newPersistentDataContainer()
            upgrades.forEach { (upgrade, level) ->
                upgradeContainer.set(
                    upgrade.key,
                    PersistentDataType.INTEGER,
                    level
                )
            }
            return@setIfIncludes upgradeContainer
        }
        setIfIncludes(
            COLLECTOR_ITEMS,
            PersistentDataType.BYTE_ARRAY,
            PineappleLib.getNmsProvider().itemsToBytes(stackContainer.getContents()),
            container,
            excludeFields
        )
    }

    override fun load(container: PersistentDataContainer) {
        this.owner = getOrNull(COLLECTOR_OWNER, PersistentDataType.STRING, container) {
            UUID.fromString(
                it ?: return@getOrNull null
            )
        }
        this.configuration = getOrThrow(COLLECTOR_CONFIGURATION, PersistentDataType.STRING, container, {
            Registries.COLLECTOR.get(it).orThrow()
        }) { throw IllegalStateException("Unable to load saved collector with missing configuration value") }
        val msigBits = getOrNull(
            COLLECTOR_DISPLAY_MSB, PersistentDataType.LONG, container
        )
        val lsigBits = getOrNull(
            COLLECTOR_DISPLAY_LSB, PersistentDataType.LONG, container
        )
        if (msigBits != null && lsigBits != null) {
            this.textDisplayUUID = UUID(msigBits, lsigBits)
        }
        this.accessWhitelist = getOrNull(
            COLLECTOR_ACCESSORS, PersistentDataType.LIST.strings(), container
        ) { it?.map { entry -> UUID.fromString(entry) }?.toMutableSet() } ?: mutableSetOf()
        this.upgrades = getOrNull(
            COLLECTOR_UPGRADES, PersistentDataType.TAG_CONTAINER, container
        ) {
            if (it == null) return@getOrNull null
            val map = mutableMapOf<CollectorUpgrade, Int>()
            for (key in it.keys) {
                map[Registries.UPGRADE.get(key).orThrow()] = container.get(key, PersistentDataType.INTEGER)!!
            }
            return@getOrNull map
        } ?: mutableMapOf()

        this.stackContainer = getOrNull(COLLECTOR_ITEMS, PersistentDataType.BYTE_ARRAY, container) {
            if (it == null) return@getOrNull null
            return@getOrNull InfStackContainer(
                configuration,
                PineappleLib.getNmsProvider().itemsFromBytes(it, configuration.storageSlots).toList()
            )
        } ?: InfStackContainer(configuration)
        println(this)
        println(stackContainer)
    }

    override fun getTileType(): TileType<*> {
        return CollectorTileType
    }

    private fun <T, R> getOrNull(
        key: NamespacedKey, type: PersistentDataType<*, T>, container: PersistentDataContainer, mapper: (T?) -> R?
    ): R? {
        return mapper.invoke(container.get(key, type))
    }

    private fun <T> getOrNull(
        key: NamespacedKey, type: PersistentDataType<*, T>, container: PersistentDataContainer
    ): T? {
        return container.get(key, type)
    }

    private fun <T, R> getOrThrow(
        key: NamespacedKey,
        type: PersistentDataType<*, T>,
        container: PersistentDataContainer,
        mapper: (T) -> R,
        thrower: () -> Exception
    ): R {
        val output = container.get(key, type) ?: throw thrower.invoke()
        return mapper.invoke(output)
    }

    private fun <T> setIfIncludes(
        key: NamespacedKey,
        type: PersistentDataType<*, T>,
        value: T?,
        container: PersistentDataContainer,
        excludeFields: MutableSet<String>?
    ) {
        return setIfIncludes(key, type, container, excludeFields) { value }
    }

    private fun <T> setIfIncludes(
        key: NamespacedKey,
        type: PersistentDataType<*, T>,
        container: PersistentDataContainer,
        excludeFields: MutableSet<String>?,
        value: (PersistentDataAdapterContext) -> T?
    ) {
        if (excludeFields != null && excludeFields.contains(key.toString())) return
        val result = value.invoke(container.adapterContext) ?: return
        container.set(key, type, result)
    }
}
