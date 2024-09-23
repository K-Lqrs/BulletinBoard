package net.rk4z.igf

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.utils.toItemStack
import org.bukkit.Material
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
    private lateinit var igfInventory: Inventory
    private var listener: GUIListener? = null
    protected val buttons: MutableList<Button> = mutableListOf()
    protected var onClickAction: ((InventoryClickEvent) -> Unit)? = null
    protected var onCloseAction: ((InventoryCloseEvent) -> Unit)? = null

    private var backgroundMaterial: Material? = null

    protected abstract fun handleClick(event: InventoryClickEvent)
    protected abstract fun handleClose(event: InventoryCloseEvent)
    abstract fun build(): BaseInventoryGUI

    fun setBackground(material: Material): BaseInventoryGUI {
        this.backgroundMaterial = material
        BulletinBoard.instance.logger.info("Background material set to: $material")
        return this
    }

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

    fun applyBackground() {
        backgroundMaterial?.let { material ->
            val itemStack = material.toItemStack()
            val meta = itemStack.itemMeta
            meta?.displayName(Component.text(""))
            itemStack.itemMeta = meta

            for (i in 0 until size) {
                inventory.setItem(i, itemStack)
            }
        }
    }

    protected fun displayItems() {
        buttons.forEach { button ->
            inventory.setItem(button.slot, button.toItemStack())
        }
    }

    fun getListener(): GUIListener? {
        return listener
    }

    override fun getInventory(): Inventory {
        return igfInventory
    }

    fun create() {
        if (!this::igfInventory.isInitialized) {
            igfInventory = player.server.createInventory(this, size, title)
        }
    }

    fun open() {
        player.openInventory(inventory)
    }

    fun close() {
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN)
    }
}
