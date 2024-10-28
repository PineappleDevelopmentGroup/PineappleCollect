package sh.miles.collector.menu.admin

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import sh.miles.collector.Registries
import sh.miles.collector.menu.CollectorMenu
import sh.miles.collector.tile.CollectorTile
import sh.miles.pineapple.chat.PineappleChat
import sh.miles.pineapple.gui.PagedPlayerGui
import sh.miles.pineapple.gui.slot.GuiSlot.GuiSlotBuilder
import sh.miles.pineapple.item.ItemBuilder
import sh.miles.pineapple.nms.api.menu.MenuType
import sh.miles.pineapple.nms.api.menu.scene.MenuScene
import sh.miles.pineapple.tiles.api.Tiles

class CollectorAdminMenu(viewer: Player) : PagedPlayerGui<MenuScene>(
    { MenuType.GENERIC_9x2.create(viewer, PineappleChat.parse("<red>Admin Collector View")) }, viewer
) {
    private val allCollectors = Tiles.getInstance().getAllLoadedTiles { it is CollectorTile }

    override fun decorate() {
        val maxPage = findMaxPage();
        for (page in (0 until maxPage)) {
            decorate(page, maxPage)
        }

        deployPage(0)

    }

    private fun decorate(page: Int, maxPage: Int) {
        var collectorIndex: Int
        for (slot in (0 until 9)) {
            collectorIndex = (page * 9) + slot
            if (collectorIndex >= allCollectors.size) {
                slot(page, slot) { inventory ->
                    GuiSlotBuilder()
                        .inventory(inventory)
                        .index(slot)
                        .drag { it.isCancelled = true }
                        .click { it.isCancelled = true }
                        .build()
                }
                continue
            }
            val tile = allCollectors[collectorIndex]
            val collector = tile.right as CollectorTile

            slot(page, slot) { inventory ->
                GuiSlotBuilder()
                    .inventory(inventory)
                    .index(slot)
                    .drag { it.isCancelled = true }
                    .click {
                        it.isCancelled = true

                        if (it.click == ClickType.LEFT) {
                            val menuConfiguration = Registries.MENU.get(collector.configuration.menuId).orThrow()
                            CollectorMenu(viewer(), this, collector, menuConfiguration).open()
                        } else if (it.click == ClickType.RIGHT) {
                            viewer.teleport(collector.location!!)
                        }
                    }
                    .item(buildCollectorItem(collector))
                    .build()
            }
        }

        for (slot in 9 until 18) {
            slot(page, slot) { inventory ->
                GuiSlotBuilder()
                    .inventory(inventory)
                    .index(slot)
                    .drag { it.isCancelled = true }
                    .click { it.isCancelled = true }
                    .item(ItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).nameLegacy(" ").build())
                    .build()
            }
        }

        if (page > 0) {
            slot(page, 9) { inventory ->
                GuiSlotBuilder()
                    .inventory(inventory)
                    .index(9)
                    .drag { it.isCancelled = true }
                    .click {
                        it.isCancelled = true
                        previousPage()
                    }
                    .item(
                        ItemBuilder.of(Material.PLAYER_HEAD)
                            .name(PineappleChat.parse("<italic:!><gray>Previous Page"))
                            .skullTexture("1f7dadf1063b4d4419ed4a5d900455754988b23418bad8cca2bf7950c3070abf")
                            .build()
                    )
                    .build()
            }
        }

        if (page < maxPage - 1) {
            slot(page, 17) { inventory ->
                GuiSlotBuilder()
                    .inventory(inventory)
                    .index(17)
                    .drag { it.isCancelled = true }
                    .click {
                        it.isCancelled = true
                        nextPage()
                    }
                    .item(
                        ItemBuilder.of(Material.PLAYER_HEAD)
                            .name(PineappleChat.parse("<italic:!><gray>Next Page"))
                            .skullTexture("70ef38b73e0a44886eb312cff555ed2ee88b624277f8eca47ef44c7a4ed74796")
                            .build()
                    ).build()
            }
        }
    }

    private fun buildCollectorItem(tile: CollectorTile): ItemStack {
        val location = tile.location ?: throw IllegalStateException("CollectorTile is loaded, but not placed?")
        val builder = ItemBuilder.of(Material.PLAYER_HEAD)
            .name(PineappleChat.parse("<italic:!><gold>Collector <gray>at <dark_gray>(<gold>${location.x}<dark_gray>, <gold>${location.y}<dark_gray>, <gold>${location.z}<dark_gray>, <gold>${location.world?.name ?: "Unknown"}<dark_gray>)"))

        val lore = mutableListOf<BaseComponent>()
        lore.add(PineappleChat.parse("<italic:!><dark_gray>Left click to open menu"))
        lore.add(PineappleChat.parse("<italic:!><dark_gray>Right click to teleport to"))
        lore.add(TextComponent(""))
        lore.add(PineappleChat.parse("<italic:!><gray>Configuration Id: <gold>${tile.configuration.key}"))
        lore.add(PineappleChat.parse("<italic:!><gray>Data version<dark_gray>: <gold>${tile.dataVersion}"))

        val offlinePlayer = Bukkit.getOfflinePlayer(tile.owner!!);
        if (offlinePlayer.isOnline) {
            builder.skullTexture(offlinePlayer.player)
            lore.add(PineappleChat.parse("<italic:!><gray>Owner<dark_gray>: <gold>${offlinePlayer.name}"))
        } else {
            builder.skullTexture("7df0ee9d25b41cb645dd2fe5c7746cbb8a1d37fd3e01e25e013242f9d03a30d6")
            lore.add(PineappleChat.parse("<italic:!><gray>Owner<dark_gray>: <gold>${offlinePlayer.name ?: "Unknown Name"}"))
        }

        for (upgrade in tile.upgrades) {
            val status = tile.getUpgradeStatus(upgrade.key)
            lore.add(PineappleChat.parse("<italic:!><gray>Upgrade<dark_gray>: <gold>${upgrade.key.key.key} <gray>Level<dark_gray>: <gold>${status.first} <gray>Enabled: <gold>${status.second}"))
        }

        return builder.lore(lore).build()
    }

    override fun findMaxPage(): Int {
        val allCollectors = Tiles.getInstance().getAllLoadedTiles { it is CollectorTile }
        return (allCollectors.size / 9) + 1
    }

}
