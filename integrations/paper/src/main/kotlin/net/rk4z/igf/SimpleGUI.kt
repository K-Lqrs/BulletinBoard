package net.rk4z.igf

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

class SimpleGUI(
    player: Player,
    title: Component,
    size: Int
) : BaseInventoryGUI(player, title, size) {
    private val buttons: MutableList<Button> = mutableListOf()
    private var onClickAction: ((InventoryClickEvent) -> Unit)? = null
    private var onCloseAction: ((InventoryCloseEvent) -> Unit)? = null

    fun setButtons(buttons: List<Button>): SimpleGUI {
        this.buttons.clear()
        this.buttons.addAll(buttons)
        return this
    }

    fun addButton(button: Button): SimpleGUI {
        this.buttons.add(button)
        return this
    }

    fun setOnClick(action: (InventoryClickEvent) -> Unit): SimpleGUI {
        this.onClickAction = action
        return this
    }

    fun setOnClose(action: (InventoryCloseEvent) -> Unit): SimpleGUI {
        this.onCloseAction = action
        return this
    }

    fun build(): SimpleGUI {
        displayItems()
        return this
    }

    private fun displayItems() {
        buttons.forEach { button ->
            inventory.addButton(button)
        }
    }

    override fun handleClick(event: InventoryClickEvent) {
        onClickAction?.invoke(event)
    }

    override fun handleClose(event: InventoryCloseEvent) {
        onCloseAction?.invoke(event)
    }

    override fun getInventory(): Inventory {
        return inventory
    }
}
