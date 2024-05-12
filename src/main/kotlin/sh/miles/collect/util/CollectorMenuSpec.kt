package sh.miles.collect.util

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import org.bukkit.inventory.ItemStack
import sh.miles.collect.CollectPlugin
import sh.miles.collect.util.json.CollectorMenuSpecAdapter
import sh.miles.pineapple.chat.PineappleComponent
import sh.miles.pineapple.json.JsonAdapter
import java.io.File
import java.io.FileReader
import java.lang.reflect.Type

object CollectorMenuSpec {

    val backgroundItem: ItemStack
    val sellItem: ItemStack
    val upgradeItem: ItemStack
    val pickupItem: ItemStack
    val sellItemLoc: Int
    val upgradeItemLoc: Int
    val pickupItemLoc: Int
    val priceLore: PineappleComponent

    init {
        val gson = CollectPlugin.plugin.json.gson
        val specDetails = gson.fromJson(
            FileReader(File(CollectPlugin.plugin.dataFolder, "collector-menu.json")),
            CollectorMenuSpecAdapter.SpecDetails::class.java
        )
        backgroundItem = specDetails.backgroundItem.buildSpec()
        sellItem = specDetails.sellItem.buildSpec()
        upgradeItem = specDetails.upgradeItem.buildSpec()
        pickupItem = specDetails.pickupItem.buildSpec()
        sellItemLoc = specDetails.sellItemLoc
        upgradeItemLoc = specDetails.upgradeItemLoc
        pickupItemLoc = specDetails.pickupItemSlot
        priceLore = specDetails.priceLore

        check(
            sellItemLoc != upgradeItemLoc
        ) { "sell_item_location ($sellItemLoc) MUST NOT equal upgrade_item_location ($upgradeItemLoc)" }
    }
}
