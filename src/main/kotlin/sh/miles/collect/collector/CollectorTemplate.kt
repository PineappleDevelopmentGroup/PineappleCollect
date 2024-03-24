package sh.miles.collect.collector

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import sh.miles.collect.util.PDC_TEMPLATE_KEY
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.function.Option

class CollectorTemplate(
    private val key: String,
    val blockEntity: Material,
    val title: PineappleComponent,
    private val source: ItemStack,
    val size: Int
) : RegistryKey<String> {

    companion object {

        fun template(itemMeta: ItemMeta): Option<String> {
            return Option.some(
                itemMeta.persistentDataContainer.get(PDC_TEMPLATE_KEY, PersistentDataType.STRING) ?: return Option.none()
            )
        }

        fun hasTemplate(itemMeta: ItemMeta): Boolean {
            return itemMeta.persistentDataContainer.has(PDC_TEMPLATE_KEY)
        }
    }

    private val item: ItemStack

    init {
        val temp = source.clone()
        val meta = temp.itemMeta!!
        meta.run { persistentDataContainer.set(PDC_TEMPLATE_KEY, PersistentDataType.STRING, key) }
        temp.itemMeta = meta

        item = temp.clone()
    }

    fun item(): ItemStack {
        return item.clone()
    }


    override fun getKey(): String {
        return this.key
    }
}
