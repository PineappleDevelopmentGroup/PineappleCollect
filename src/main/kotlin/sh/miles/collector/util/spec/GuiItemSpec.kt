package sh.miles.collector.util.spec

import sh.miles.pineapple.item.ItemSpec
import sh.miles.pineapple.util.spec.SoundSpec

data class GuiItemSpec(
    val item: ItemSpec,
    val slot: Int,
    val actionId: String,
    val args: List<String>,
    val clickSound: SoundSpec
)
