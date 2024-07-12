package io.github.pineappledevelopmentgroup.collector

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.collection.registry.RegistryKey
import sh.miles.pineapple.item.ItemBuilder

/**
 * Data that represents a collector's meta
 *
 * @param id the registry id of this collector meta
 * @param item the item the collector is when placed, must be a block
 */
open class CollectorMeta(private val id: String, item: ItemStack) : RegistryKey<String> {

    override fun getKey(): String {
        return this.id
    }


    object Singleton : CollectorMeta(
        "singleton",
        ItemBuilder.of(Material.FLETCHING_TABLE)
            .name(PineappleChat.parse("<color:#221C35><italics:!>Item Collector"))
            .lore(
                listOf(
                    PineappleChat.parse("Place to start collecting items")
                )
            )
            .build()
    )
}
