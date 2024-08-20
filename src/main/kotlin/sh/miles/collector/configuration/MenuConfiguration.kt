package sh.miles.collector.configuration

import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.item.ItemSpec

data class MenuConfiguration(val id: String, val title: PineappleComponent, val viewRows: Int, val storageSize: Int, val storageSlots: Set<Int>, val backgroundItem: ItemSpec) : RegistryKey<String> {
    override fun getKey(): String {
        return id
    }
}
