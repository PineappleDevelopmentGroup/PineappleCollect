package sh.miles.collect.collector

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.collection.registry.RegistryKey

class CollectorTemplate(
    private val key: String,
    val blockEntity: Material,
    val title: PineappleComponent,
    private val source: ItemStack
) : RegistryKey<String> {

    companion object {
        val COLLECTOR_KEY = NamespacedKey.fromString("collector:template")!!
    }

    private val item: ItemStack

    init {
        val temp = source.clone()
        val meta = temp.itemMeta!!
        meta.run { persistentDataContainer.set(COLLECTOR_KEY, PersistentDataType.STRING, key) }
        temp.itemMeta = meta

        item = temp.clone()
    }

    fun item(): ItemStack {
        return item.clone()
    }


    override fun getKey(): String {
        return this.key;
    }
}
