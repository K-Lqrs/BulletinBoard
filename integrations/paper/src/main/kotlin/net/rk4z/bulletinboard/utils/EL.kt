package net.rk4z.bulletinboard.utils

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

object EL : Listener {
    private var globalListener: GUIListener? = null

    fun setGlobalListener(listener: GUIListener) {
        globalListener = listener
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val holder = event.clickedInventory?.holder
        if (holder !is InventoryGUI) return

        holder.getListener()?.onInventoryClick(event) ?: globalListener?.onInventoryClick(event)
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val holder = event.inventory.holder
        if (holder !is InventoryGUI) return

        holder.getListener()?.onInventoryClose(event) ?: globalListener?.onInventoryClose(event)
    }
}