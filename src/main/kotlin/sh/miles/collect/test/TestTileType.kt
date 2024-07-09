package sh.miles.collect.test

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import sh.miles.crown.tiles.Tile
import sh.miles.crown.tiles.TileType
import sh.miles.crown.tiles.factory.item.TileItemFactory
import sh.miles.crown.tiles.factory.tile.TileFactory

object TestTileType : TileType<TestTileType.TestTile> {

    val KEY = NamespacedKey.fromString("collect:test")!!

    override fun getKey(): NamespacedKey {
        return KEY
    }

    override fun onInteract(event: PlayerInteractEvent, tile: Tile) {
        val testTile = tile as TestTile
        testTile.clickCount += 1
        event.player.sendMessage("You Clicked Test Tile!")
        event.player.sendMessage("You've clicked this tile ${testTile.clickCount} times")
    }

    override fun onBlockBreak(event: BlockBreakEvent, tile: Tile) {
        event.player.sendMessage("Broke Test Tile")
    }

    override fun onBlockPlace(event: BlockPlaceEvent, tile: Tile) {
        event.player.sendMessage("Placed Test Tile")
    }

    override fun onBlockDrop(event: BlockDropItemEvent, tile: Tile) {
        event.player.sendMessage("Dropping Test Tile")
        val itemEntity = event.items[0]
        itemEntity.itemStack = tileItemFactory.create(tile as TestTile)
    }

    override fun getTileFactory(): TileFactory<TestTile> {
        return TestTileFactory
    }

    override fun getTileItemFactory(): TileItemFactory<TestTile> {
        return TestTileItemFactory
    }

    object TestTileFactory : TileFactory<TestTile> {

        override fun create(): TestTile {
            return TestTile()
        }

        override fun create(item: ItemStack): TestTile {
            val tile = create();
            tile.load(item.itemMeta!!.persistentDataContainer)
            return tile
        }

    }

    object TestTileItemFactory : TileItemFactory<TestTile> {

        override fun create(): ItemStack {
            val item = ItemStack(Material.ACACIA_FENCE_GATE)
            val meta = item.itemMeta!!
            meta.persistentDataContainer.set(TileItemFactory.TILE_ITEM_KEY, PersistentDataType.STRING, KEY.toString())
            item.itemMeta = meta

            return item
        }

        override fun isFactoryResultant(item: ItemStack): Boolean {
            val meta = item.itemMeta ?: return false
            if (!meta.persistentDataContainer.has(TileItemFactory.TILE_ITEM_KEY)) return false
            val key = meta.persistentDataContainer.get(TileItemFactory.TILE_ITEM_KEY, PersistentDataType.STRING)
            return key.equals(KEY.toString())
        }

        override fun create(tile: TestTile): ItemStack {
            val item = create()
            val meta = item.itemMeta!!
            meta.persistentDataContainer.set(TestTile.COUNT_KEY, PersistentDataType.INTEGER, tile.clickCount)
            item.itemMeta = meta

            return item
        }

    }

    class TestTile : Tile {

        companion object {
            val COUNT_KEY = NamespacedKey.fromString("collect:countkey")!!
        }

        var clickCount: Int = 0

        override fun save(container: PersistentDataContainer) {
            container.set(COUNT_KEY, PersistentDataType.INTEGER, clickCount)
        }

        override fun load(container: PersistentDataContainer): Boolean {
            if (!container.has(COUNT_KEY)) return false
            this.clickCount = container.get(COUNT_KEY, PersistentDataType.INTEGER)!!
            return true
        }

        override fun delete(container: PersistentDataContainer) {
            container.remove(COUNT_KEY)
        }

        override fun getTileTypeKey(): NamespacedKey {
            return KEY
        }
    }

}
