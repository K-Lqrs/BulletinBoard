package net.rk4z.igf

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

interface GUIListener {
    fun onInventoryClick(event: InventoryClickEvent)
    fun onInventoryClose(event: InventoryCloseEvent)
}