package net.rk4z.igf

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

/**
 * Represents a base class for creating custom inventory GUIs in Minecraft.
 * This class provides basic inventory manipulation methods and allows setting up buttons,
 * listeners, and background materials for GUI customization.
 *
 * @param player The player viewing the GUI.
 */
@Suppress("unused")
abstract class InventoryGUI(
    protected val player: Player
) : InventoryHolder {
    private var igfInventory: Inventory? = null
    private var listener: GUIListener? = null
    private var title: Component? = null
    private var size: Int? = null
    private var background: Material? = null
    private var shouldCallGlobalListener = false
    protected var items: List<Button> = emptyList()
        private set

    companion object {
        /**
         * Returns the background material for a given [InventoryGUI] instance.
         * This method can be called statically from outside the class.
         *
         * @param gui The [InventoryGUI] instance to get the background from.
         * @return The [Material] representing the background.
         */
        @JvmStatic
        fun getBackgroundMaterial(gui: InventoryGUI): Material? {
            return gui.background
        }

        /**
         * Returns the title of a given [InventoryGUI] instance.
         * This method can be called statically from outside the class.
         *
         * @param gui The [InventoryGUI] instance to get the title from.
         * @return The [Component] representing the title.
         */
        @JvmStatic
        fun getTitle(gui: InventoryGUI): Component? {
            return gui.title
        }

        /**
         * Returns the size of a given [InventoryGUI] instance.
         * This method can be called statically from outside the class.
         *
         * @param gui The [InventoryGUI] instance to get the size from.
         * @return The size as an integer.
         */
        @JvmStatic
        fun getSize(gui: InventoryGUI): Int? {
            return gui.size
        }

        /**
         * Returns the items set in a given [InventoryGUI] instance.
         * This method can be called statically from outside the class.
         *
         * @param gui The [InventoryGUI] instance to get the items from.
         * @return The list of [Button]s.
         */
        @JvmStatic
        fun getItems(gui: InventoryGUI): List<Button> {
            return gui.items
        }

        /**
         * Returns the listener set in a given [InventoryGUI] instance.
         * This method can be called statically from outside the class.
         *
         * @param gui The [InventoryGUI] instance to get the listener from.
         * @return The [GUIListener] instance, or null if not set.
         */
        @JvmStatic
        fun getListener(gui: InventoryGUI): GUIListener? {
            return gui.listener
        }

        /**
         * Indicates whether the global listener should be called on inventory events for a given [InventoryGUI] instance.
         * This method can be called statically from outside the class.
         *
         * @param gui The [InventoryGUI] instance to check.
         * @return True if the global listener should be called, false otherwise.
         */
        @JvmStatic
        fun getShouldCallGlobalListener(gui: InventoryGUI): Boolean {
            return gui.shouldCallGlobalListener
        }
    }

    /**
     * Abstract method for building the inventory GUI.
     * This method should be implemented by subclasses to define the layout and items of the GUI.
     *
     * @return The constructed [InventoryGUI] instance.
     */
    abstract fun build(): InventoryGUI

    /**
     * Displays the items set in this GUI in their respective slots.
     * This method iterates through the list of [Button]s and places them in the specified slots.
     */
    protected fun displayItems() {
        items.forEach { button ->
            inventory.setItem(button.slot, button.toItemStack())
        }
    }

    /**
     * Applies a background to the inventory GUI using the specified [Material].
     * Fills every slot in the inventory with the given background item.
     */
    protected fun applyBackground() {
        background?.let {
            val itemStack = it.toItemStack()
            val meta = itemStack.itemMeta
            meta?.displayName(Component.text(""))
            itemStack.itemMeta = meta

            for (i in 0 until size!!) {
                inventory.setItem(i, itemStack)
            }
        }
    }

    /**
     * Creates the inventory based on the title and size set in this class.
     * This method must be called before attempting to use the inventory.
     *
     * @throws IllegalStateException If title or size is not set.
     */
    protected fun create() {
        if (title == null || size == null) {
            throw IllegalStateException("Title and size must be set")
        }

        igfInventory = player.server.createInventory(this, size!!, title!!)
    }

    /**
     * Returns the inventory associated with this GUI.
     * @return The [Inventory] object.
     * @throws IllegalStateException If the inventory is not created yet.
     */
    override fun getInventory(): Inventory {
        return igfInventory ?: throw IllegalStateException("Inventory is not created yet")
    }

    /**
     * Gets the title of this GUI.
     * @return The [Component] representing the title.
     * @throws IllegalStateException If the title is not set.
     */
    fun getTitle(): Component? {
        return title
    }

    /**
     * Gets the size of the inventory GUI.
     * @return The size as an integer.
     * @throws IllegalStateException If the size is not set.
     */
    fun getSize(): Int? {
        return size
    }

    /**
     * Gets the background material set for the inventory GUI.
     * @return The [Material] used as the background.
     * @throws IllegalStateException If the background material is not set.
     */
    fun getBackgroundMaterial(): Material? {
        return background
    }

    /**
     * Sets the background material for the inventory GUI.
     * @param background The [Material] to use as the background.
     * @return The current [InventoryGUI] instance for chaining.
     */
    fun setBackground(background: Material): InventoryGUI {
        this.background = background
        return this
    }

    /**
     * Sets multiple items at once for the GUI.
     * This replaces the existing item list.
     *
     * @param items The list of [Button]s to add.
     * @return The current [InventoryGUI] instance for chaining.
     */
    fun setItems(items: List<Button>): InventoryGUI {
        this.items = items
        return this
    }

    /**
     * Adds a single item to the existing item list.
     * @param button The [Button] to add.
     * @return The current [InventoryGUI] instance for chaining.
     */
    fun addItem(button: Button): InventoryGUI {
        this.items += button
        return this
    }

    /**
     * Sets the listener for handling GUI events.
     * @param listener The [GUIListener] instance to handle events.
     * @return The current [InventoryGUI] instance for chaining.
     */
    fun setListener(listener: GUIListener): InventoryGUI {
        this.listener = listener
        return this
    }

    /**
     * Gets the current listener associated with this GUI.
     * @return The [GUIListener] instance, or null if not set.
     */
    fun getListener(): GUIListener? {
        return this.listener
    }

    /**
     * Indicates whether the global listener should be called on inventory events.
     * @return True if the global listener should be called, false otherwise.
     */
    fun shouldCallGlobalListener(): Boolean {
        return this.shouldCallGlobalListener
    }

    /**
     * Sets whether the global listener should be called on inventory events.
     * @param shouldCallGlobalListener True to call the global listener, false otherwise.
     * @return The current [InventoryGUI] instance for chaining.
     */
    fun setShouldCallGlobalListener(shouldCallGlobalListener: Boolean): InventoryGUI {
        this.shouldCallGlobalListener = shouldCallGlobalListener
        return this
    }

    /**
     * Sets the title of the inventory GUI.
     * @param title The [Component] representing the title.
     * @return The current [InventoryGUI] instance for chaining.
     */
    fun setTitle(title: Component): InventoryGUI {
        this.title = title
        return this
    }

    /**
     * Sets the size of the inventory GUI.
     * @param size The number of slots in the inventory.
     * @return The current [InventoryGUI] instance for chaining.
     */
    fun setSize(size: Int): InventoryGUI {
        this.size = size
        return this
    }

    /**
     * Opens the inventory for the player.
     */
    fun open() {
        if (igfInventory == null) {
            throw IllegalStateException("Inventory not set")
        }
        player.openInventory(igfInventory!!)
    }

    /**
     * Closes the inventory for the player.
     */
    fun close() {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
    }
}
