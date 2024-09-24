package net.rk4z.igf

import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.LoggerFactory

object IGF : Listener {
    val logger = LoggerFactory.getLogger(IGF::class.java.simpleName)
    lateinit var namespacedKey: NamespacedKey
    private var globalListener: GUIListener? = null

    /**
     * You must call this method in your plugin's onEnable method.
     */
    fun init(plugin: JavaPlugin, key: NamespacedKey) {
        namespacedKey = key
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun setGlobalListener(listener: GUIListener) {
        globalListener = listener
    }

    @Deprecated("Dont use this method, use setGlobalListener instead")
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val inventory = event.clickedInventory ?: return

        // Check if the inventory is controlled by IGF (BaseInventoryGUI)
        if (inventory.holder !is BaseInventoryGUI) return

        event.isCancelled = true

        val gui = inventory.holder as BaseInventoryGUI

        gui.getListener()?.onInventoryClick(event) ?: globalListener?.onInventoryClick(event)
    }

    @Deprecated("Dont use this method, use setGlobalListener instead")
    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory

        if (inventory.holder !is BaseInventoryGUI) return

        val gui = inventory.holder as BaseInventoryGUI

        gui.getListener()?.onInventoryClose(event) ?: globalListener?.onInventoryClose(event)
    }
}
