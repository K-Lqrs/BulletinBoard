package net.rk4z.bulletinboard.utils

import net.rk4z.bulletinboard.utils.igf.GUIListener
import net.rk4z.bulletinboard.utils.igf.InventoryGUI
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

object EL : Listener {
    private var globalListener: GUIListener? = null

    fun setGlobalListener(listener: GUIListener) {
        globalListener = listener
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClick(event: InventoryClickEvent) {
        val holder = event.clickedInventory?.holder
        if (holder !is InventoryGUI) return

        holder.getListener()?.onInventoryClick(event, holder) ?: globalListener?.onInventoryClick(event, holder)
        if (holder.shouldCallGlobalListener()) {
            globalListener?.onInventoryClick(event, holder)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val holder = event.inventory.holder
        if (holder !is InventoryGUI) return

        holder.getListener()?.onInventoryClose(event, holder) ?: globalListener?.onInventoryClose(event, holder)
        if (holder.shouldCallGlobalListener()) {
            globalListener?.onInventoryClose(event, holder)
        }
    }
}