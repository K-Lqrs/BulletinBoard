package net.rk4z.igf

import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.plugin.java.JavaPlugin

/**
 * Main handler for the InventoryGUI framework (IGF).
 * It manages global listeners, event handling, and NamespacedKey initialization.
 */
object IGF : Listener {
    /**
     * The NamespacedKey used for identifying GUI-specific data.
     */
    lateinit var namespacedKey: NamespacedKey
        private set

    const val ID = "igf"
    private var globalListener: GUIListener = NoOpListener

    /**
     * Initializes the IGF with the given plugin and registers the events.
     *
     * @param plugin The JavaPlugin instance to initialize with.
     */
    fun init(plugin: JavaPlugin) {
        namespacedKey = NamespacedKey(plugin, ID)
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    /**
     * Sets the global listener for all GUIs managed by IGF.
     *
     * @param listener The [GUIListener] to be used globally.
     */
    fun setGlobalListener(listener: GUIListener) {
        globalListener = listener
    }

    /**
     * Handles inventory click events.
     * Cancels clicks on the background material by default and delegates to the appropriate listener.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClick(event: InventoryClickEvent) {
        val item = event.currentItem ?: return
        val holder = event.clickedInventory?.holder

        // Check if the clicked inventory holder is of type InventoryGUI
        if (holder !is InventoryGUI) return

        // Cancel the event if the clicked item is a background item
        val background = holder.getBackgroundMaterial()
        if (background != null && item.type == background) {
            event.isCancelled = true
        }

        // Handle click events based on the type of GUI
        if (holder is PaginatedGUI) {
            // Handle page navigation
            holder.handlePageNavigation(event)
        }

        // Delegate the event to the local or global listener
        holder.getListener()?.onInventoryClick(event, holder) ?: globalListener.onInventoryClick(event, holder)
        if (holder.shouldCallGlobalListener()) {
            globalListener.onInventoryClick(event, holder)
        }
    }

    /**
     * Handles inventory close events.
     * Delegates to the appropriate listener if set.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val holder = event.inventory.holder

        // Check if the closed inventory holder is of type InventoryGUI
        if (holder !is InventoryGUI) return

        // Delegate the event to the local or global listener
        holder.getListener()?.onInventoryClose(event, holder) ?: globalListener.onInventoryClose(event, holder)
        if (holder.shouldCallGlobalListener()) {
            globalListener.onInventoryClose(event, holder)
        }
    }

    /**
     * A no-operation listener to handle cases when the global listener is not set.
     * This prevents null checks and provides a safe default.
     */
    private object NoOpListener : GUIListener {
        override fun onInventoryClick(event: InventoryClickEvent, gui: InventoryGUI) {}
        override fun onInventoryClose(event: InventoryCloseEvent, gui: InventoryGUI) {}
    }
}
