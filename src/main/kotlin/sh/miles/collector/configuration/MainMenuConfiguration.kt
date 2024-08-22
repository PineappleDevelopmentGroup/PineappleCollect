package sh.miles.collector.configuration

import sh.miles.collector.util.spec.GuiItemSpec
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.item.ItemSpec

data class MainMenuConfiguration(
    val id: String,
    val title: PineappleComponent,
    val viewRows: Int,
    val sellMenuId: String,
    val itemMap: Map<Int, GuiItemSpec>,
    val backgroundItem: ItemSpec
) :
    RegistryKey<String> {
    override fun getKey(): String {
        return id
    }
}
