package net.rk4z.bulletinboard.utils.igf

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.utils.Button
import net.rk4z.bulletinboard.utils.toItemStack
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class InventoryGUI(
    protected val player: Player
) : InventoryHolder {
    private var igfInventory: Inventory? = null
    private var listener: GUIListener? = null
    private var title: Component? = null
    private var size: Int? = null
    private var background: Material? = null
    private var items: List<Button> = emptyList()
    private var shouldCallGlobalListener = false

    abstract fun build(): InventoryGUI

    protected fun displayItems() {
        items.forEach { button ->
            inventory.setItem(button.slot, button.toItemStack())
        }
    }

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

    protected fun create() {
        if (title == null || size == null) {
            throw IllegalStateException("Title and size must be set")
        }

        igfInventory = player.server.createInventory(this, size!!, title!!)
    }

    override fun getInventory(): Inventory {
        return igfInventory ?: throw IllegalStateException("Inventory not set")
    }

    fun getTitle(): Component {
        return title ?: throw IllegalStateException("Title not set")
    }

    fun getItems(): List<Button> {
        return items
    }

    fun getSize(): Int {
        return size ?: throw IllegalStateException("Size not set")
    }

    fun getBackgroundMaterial(): Material {
        return background ?: throw IllegalStateException("Background material not set")
    }

    fun setBackground(background: Material): InventoryGUI {
        this.background = background
        return this
    }

    /**
     * Add multiple items at once.
     * @param items List of items to add
     * @author Lars
     * @since 1.0.0
     * @see Button
     * @see addItem
     * @see getItems
     */
    fun setItems(items: List<Button>): InventoryGUI {
        this.items = items
        return this
    }

    /**
     * Add a single item.
     * @param button Item to add
     * @author Lars
     * @since 1.0.0
     * @see setItems
     * @see getItems
     * @see Button
     */
    fun addItem(button: Button): InventoryGUI {
        this.items += button
        return this
    }

    /**
     * Set the listener for this GUI.
     * @param listener Listener to set
     */
    fun setListener(listener: GUIListener): InventoryGUI {
        this.listener = listener
        return this
    }

    fun getListener(): GUIListener? {
        return this.listener
    }

    fun shouldCallGlobalListener(): Boolean {
        return this.shouldCallGlobalListener
    }

    fun setShouldCallGlobalListener(shouldCallGlobalListener: Boolean): InventoryGUI {
        this.shouldCallGlobalListener = shouldCallGlobalListener
        return this
    }

    fun setTitle(title: Component): InventoryGUI {
        this.title = title
        return this
    }

    fun setSize(size: Int): InventoryGUI {
        this.size = size
        return this
    }

    fun open() {
        if (igfInventory == null) {
            throw IllegalStateException("Inventory not set")
        }

        player.openInventory(igfInventory!!)
    }

    fun close() {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
    }
}