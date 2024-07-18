package sh.miles.pineapplecollectors.tiles.api

import org.bukkit.NamespacedKey
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import sh.miles.pineapple.collection.registry.RegistryKey

interface TileData<T : Tile> : RegistryKey<NamespacedKey> {

    fun onInteract(event: PlayerInteractEvent, tile: T)

    fun onBlockBreak(event: BlockBreakEvent, tile: T)

    fun onBlockPlace(event: BlockPlaceEvent, tile: T)

    fun onBlockDrop(event: BlockDropItemEvent, tile: T)

    fun createItem(): ItemStack

    fun fromItem(): T

}