package net.rk4z.bulletinboard.listener

import net.rk4z.igf.GUIListener
import net.rk4z.igf.InventoryGUI
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class BBListener : GUIListener {
    override fun onInventoryClick(event: InventoryClickEvent, gui: InventoryGUI) {
        // Since each GUI handles clicks, nothing is written here.
    }

    override fun onInventoryClose(event: InventoryCloseEvent, gui: InventoryGUI) {
        //TODO
    }
}