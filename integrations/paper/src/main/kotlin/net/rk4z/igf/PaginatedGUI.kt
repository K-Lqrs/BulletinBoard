package net.rk4z.igf

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class PaginatedGUI(
    player: Player,
    title: Component,
    size: Int,
    private val itemsPerPage: Int = 4
) : BaseInventoryGUI(player, title, size) {
    private var currentPage: Int = 0
    private val totalRows = size / 9
    private val items: MutableList<Button> = mutableListOf()

    fun setItems(items: List<Button>): PaginatedGUI {
        this.items.clear()
        this.items.addAll(items)
        return this
    }

    fun addItem(item: Button): PaginatedGUI {
        this.items.add(item)
        return this
    }

    fun build(): PaginatedGUI {
        displayPage()
        return this
    }

    private fun displayPage() {
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
                    customId = "PREV_PAGE:$currentPage"
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
                    customId = "NEXT_PAGE:$currentPage"
                )
            )
        }
    }

    override fun handleClick(event: InventoryClickEvent) {
        val clickedItem = event.currentItem ?: return
        when (clickedItem.type) {
            Material.ARROW -> {
                val customId = PlainTextComponentSerializer.plainText().serialize(clickedItem.itemMeta?.displayName() ?: Component.text(""))

                if (customId.startsWith("PREV_PAGE")) {
                    currentPage--
                } else if (customId.startsWith("NEXT_PAGE")) {
                    currentPage++
                }
                displayPage()
            }
            else -> {}
        }
    }

    override fun handleClose(event: InventoryCloseEvent) {}

    override fun getInventory(): Inventory {
        return inventory
    }
}
