package net.rk4z.bulletinboard.utils.igf

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

interface GUIListener {
    fun onInventoryClick(event: InventoryClickEvent, gui: InventoryGUI)
    fun onInventoryClose(event: InventoryCloseEvent, gui: InventoryGUI)
}