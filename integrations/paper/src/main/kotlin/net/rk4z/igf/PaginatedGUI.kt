package net.rk4z.igf

import net.kyori.adventure.text.Component
import net.rk4z.igf.IGF.namespacedKey
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.persistence.PersistentDataType

class PaginatedGUI(
    player: Player,
    title: Component,
    size: Int,
    private val itemsPerPage: Int = 4
) : BaseInventoryGUI(player, title, size) {
    private var currentPage: Int = 0
    private val totalRows = size / 9
    private val items: MutableList<Button> = mutableListOf()

    private fun displayPage() {
        inventory.clear()

        val startIndex = currentPage * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, items.size)

        items.subList(startIndex, endIndex).forEachIndexed { index, button ->
            inventory.addButton(button)
        }

        if (currentPage > 0) {
            val prevPageSlot = (totalRows - 1) * 9
            inventory.setItem(
                prevPageSlot,
                createCustomItem(
                    Material.ARROW,
                    Component.text("Previous Page"),
                    customId = "PREV_PAGE"
                )
            )
        }

        if (endIndex < items.size) {
            val nextPageSlot = (totalRows * 9) - 1
            inventory.setItem(
                nextPageSlot,
                createCustomItem(
                    Material.ARROW,
                    Component.text("Next Page"),
                    customId = "NEXT_PAGE"
                )
            )
        }
    }

    override fun handleClick(event: InventoryClickEvent) {
        val clickedItem = event.currentItem ?: return
        val meta = clickedItem.itemMeta ?: return
        val customId = meta.persistentDataContainer.get(namespacedKey, PersistentDataType.STRING) ?: return

        when (customId) {
            "PREV_PAGE" -> {
                currentPage--
                displayPage()
            }
            "NEXT_PAGE" -> {
                currentPage++
                displayPage()
            }
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {}

    override fun build(): BaseInventoryGUI {
        displayPage()
        return this
    }
}
