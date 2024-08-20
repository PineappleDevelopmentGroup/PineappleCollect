package sh.miles.collector.configuration

import sh.miles.collector.util.spec.HologramSpec
import sh.miles.collector.util.spec.SoundSpec
import sh.miles.crown.infstacks.InfStackFactory
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.item.ItemSpec

data class CollectorConfiguration(
    val id: String,
    val menuId: String,
    val storageSlots: Int,
    val hologram: HologramSpec,
    val item: ItemSpec,
    val placeSound: SoundSpec,
    val breakSound: SoundSpec,
    val infStackFactory: InfStackFactory
) : RegistryKey<String> {
    override fun getKey(): String {
        return id
    }
}
