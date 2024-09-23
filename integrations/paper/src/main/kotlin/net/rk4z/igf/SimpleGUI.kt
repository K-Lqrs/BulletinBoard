package net.rk4z.igf

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class SimpleGUI(
    player: Player,
    title: Component,
    size: Int
) : BaseInventoryGUI(player, title, size) {
    override fun handleClick(event: InventoryClickEvent) {
        onClickAction?.invoke(event)
    }

    override fun handleClose(event: InventoryCloseEvent) {
        onCloseAction?.invoke(event)
    }

    override fun build(): BaseInventoryGUI {
        displayItems()
        return this
    }
}
