package sh.miles.collector.configuration

import sh.miles.collector.util.spec.SoundSpec
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.item.ItemSpec

data class SellMenuConfiguration(
    val id: String,
    val title: PineappleComponent,
    val viewRows: Int,
    val storageSize: Int,
    val storageSlots: Set<Int>,
    val sellAllItemSlot: Int,
    val sellAllItem: ItemSpec,
    val sellSound: SoundSpec,
    val extractSound: SoundSpec,
    val backgroundItem: ItemSpec
) : RegistryKey<String> {
    override fun getKey(): String {
        return id
    }
}
