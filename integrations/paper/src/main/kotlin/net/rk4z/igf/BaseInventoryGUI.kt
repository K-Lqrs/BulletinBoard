package net.rk4z.igf

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

abstract class BaseInventoryGUI(
    protected val player: Player,
    protected var title: Component,
    protected var size: Int
) : InventoryHolder {
    protected val igfInventory: Inventory = createInventory()
    private var listener: GUIListener? = null
    protected val buttons: MutableList<Button> = mutableListOf()
    protected var onClickAction: ((InventoryClickEvent) -> Unit)? = null
    protected var onCloseAction: ((InventoryCloseEvent) -> Unit)? = null

    abstract fun handleClick(event: InventoryClickEvent)
    abstract fun handleClose(event: InventoryCloseEvent)

    fun setButtons(buttons: List<Button>): BaseInventoryGUI {
        this.buttons.clear()
        this.buttons.addAll(buttons)
        return this
    }

    fun addButton(button: Button): BaseInventoryGUI {
        this.buttons.add(button)
        return this
    }

    fun setListener(listener: GUIListener): BaseInventoryGUI {
        this.listener = listener
        return this
    }

    fun setOnClick(action: (InventoryClickEvent) -> Unit): BaseInventoryGUI {
        this.onClickAction = action
        return this
    }

    fun setOnClose(action: (InventoryCloseEvent) -> Unit): BaseInventoryGUI {
        this.onCloseAction = action
        return this
    }

    abstract fun build(): BaseInventoryGUI

    protected fun displayItems() {
        buttons.forEach { button ->
            inventory.addButton(button)
        }
    }

    fun getListener(): GUIListener? {
        return listener
    }

    private fun createInventory(): Inventory {
        return player.server.createInventory(this, size, title)
    }

    fun open() {
        player.openInventory(inventory)
    }

    fun close() {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
    }
}
