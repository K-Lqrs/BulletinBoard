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
    protected val inventory: Inventory = createInventory()

    private var listener: GUIListener? = null

    abstract fun handleClick(event: InventoryClickEvent)
    abstract fun handleClose(event: InventoryCloseEvent)

    fun setListener(listener: GUIListener): BaseInventoryGUI {
        this.listener = listener
        return this
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
