package net.rk4z.igf

import net.rk4z.igf.IGF.namespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.persistence.PersistentDataType

/**
 * A class that provides paginated inventory GUI functionality with improved item management.
 * It allows dynamic page navigation, slot management, and customization using PersistentDataContainer.
 *
 * @param player The player who will view the GUI.
 */
class PaginatedGUI(
    player: Player
) : InventoryGUI(player) {
    protected var currentPage = 0
    protected var itemsPerPage = 9
    protected var totalPages = 1
        private set
    private var slotPositions: List<Int> = emptyList()
    private var emptyMessageButton: Button? = null
    private var items: List<Button> = emptyList()

    private var prevPageButton: Button? = null
    private var nextPageButton: Button? = null
    private var prevCustomID: String? = null
    private var nextCustomID: String? = null

    /**
     * Builds the paginated GUI.
     * This method should be overridden by subclasses to set up the layout and items.
     */
    override fun build(): PaginatedGUI {
        create()
        applyBackground()
        displayItemsForPage()
        return this
    }

    /**
     * Sets the slot positions for items.
     * @param slots The list of slot positions for the items.
     * @return This [PaginatedGUI] instance.
     */
    fun setSlotPositions(slots: List<Int>): PaginatedGUI {
        this.slotPositions = slots
        return this
    }

    /**
     * Sets the items to be paginated.
     * @param items The list of items to display across multiple pages.
     * @return This [PaginatedGUI] instance.
     */
    fun setPageItems(items: List<Button>): PaginatedGUI {
        this.items = items
        setTotalPages(items.size)
        return this
    }

    /**
     * Sets a default button to display when there are no items.
     * @param button The button to display when there are no items.
     */
    fun setEmptyMessageButton(button: Button): PaginatedGUI {
        this.emptyMessageButton = button
        return this
    }

    /**
     * Sets the total number of pages.
     * This method should be called after setting up items.
     */
    fun setTotalPages(totalItems: Int): PaginatedGUI {
        this.totalPages = (totalItems + itemsPerPage - 1) / itemsPerPage
        return this
    }

    /**
     * Displays the items for the current page in the inventory.
     * Clears previous items and adds only the items that belong to the current page.
     */
    fun displayItemsForPage() {
        inventory.clear()

        if (items.isEmpty()) {
            emptyMessageButton?.let { addItem(it) }
            addPageNavigationButtons()
            return
        }

        val startIndex = currentPage * itemsPerPage
        val endIndex = (startIndex + itemsPerPage).coerceAtMost(items.size)

        items.subList(startIndex, endIndex).forEachIndexed { index, button ->
            val slot = slotPositions.getOrNull(index) ?: return@forEachIndexed
            inventory.setItem(slot, button.toItemStack())
        }

        addPageNavigationButtons()
    }

    /**
     * Sets the buttons for page navigation.
     * @param prevButton The button to navigate to the previous page.
     * @param nextButton The button to navigate to the next page.
     * @return This [PaginatedGUI] instance.
     * @throws IllegalStateException If custom IDs are not set for page navigation.
     * @see setPageButtonsCustomID
     * @see handlePageNavigation
     */
    fun setPageButtons(prevButton: Button, nextButton: Button): PaginatedGUI {
        prevPageButton = prevButton
        nextPageButton = nextButton
        return this
    }

    fun setPageButtonsCustomID(prevCustomID: String, nextCustomID: String): PaginatedGUI {
        this.prevCustomID = prevCustomID
        this.nextCustomID = nextCustomID
        return this
    }

    /**
     * Sets the current page for the inventory.
     * @param page The page number to set.
     * @return This [PaginatedGUI] instance.
     */
    fun setPage(page: Int): PaginatedGUI {
        this.currentPage = page.coerceIn(0, totalPages - 1)
        return this
    }

    /**
     * Navigates to the next page if available.
     */
    fun nextPage() {
        if (currentPage < totalPages - 1) {
            setPage(currentPage + 1)
            displayItemsForPage()
        }
    }

    /**
     * Navigates to the previous page if available.
     */
    fun prevPage() {
        if (currentPage > 0) {
            setPage(currentPage - 1)
            displayItemsForPage()
        }
    }

    /**
     * Adds the navigation buttons for paging.
     * It uses the buttons set by [setPageButtons].
     */
    protected fun addPageNavigationButtons() {
        prevPageButton?.let { button ->
            if (currentPage > 0) {
                inventory.setItem(button.slot, button.toItemStack())
            }
        }

        nextPageButton?.let { button ->
            if (currentPage < totalPages - 1) {
                inventory.setItem(button.slot, button.toItemStack())
            }
        }
    }

    /**
     * Handles the click events for page navigation using PersistentDataContainer.
     */
    fun handlePageNavigation(event: InventoryClickEvent) {
        val clickedItem = event.currentItem ?: return
        val meta = clickedItem.itemMeta ?: return
        val action = meta.persistentDataContainer.get(namespacedKey, PersistentDataType.STRING) ?: return

        if (prevCustomID == null || nextCustomID == null) throw IllegalStateException("Custom IDs must be set for page navigation")

        when (action) {
            prevCustomID -> prevPage()
            nextCustomID -> nextPage()
        }
    }
}
