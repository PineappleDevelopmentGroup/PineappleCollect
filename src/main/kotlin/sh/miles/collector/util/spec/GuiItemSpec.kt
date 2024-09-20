package sh.miles.collector.util.spec

import sh.miles.pineapple.item.ItemSpec

data class GuiItemSpec(
    val item: ItemSpec,
    val slot: Int,
    val actionId: String,
    val args: List<String>,
    val clickSound: SoundSpec
)
