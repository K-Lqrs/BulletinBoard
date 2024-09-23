package net.rk4z.bulletinboard.listener

import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.utils.CustomID
import net.rk4z.igf.BaseInventoryGUI
import net.rk4z.igf.GUIListener
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.persistence.PersistentDataType

class BBListener : GUIListener {
    override fun onInventoryClick(event: InventoryClickEvent) {
        val inventory = event.clickedInventory ?: return

        if (inventory.holder !is BaseInventoryGUI) return

        val player = event.whoClicked as? Player ?: return
        val inventoryTitle = event.view.title()
        val item = event.currentItem ?: return
        val itemMeta = item.itemMeta ?: return
        val displayName = itemMeta.displayName()
        val customId = itemMeta.persistentDataContainer.get(BulletinBoard.key, PersistentDataType.STRING)

        if (item.type.isAir) return

        // This code can only be executed if the inventory is managed by an IGF
        event.isCancelled = true
        if (item.type == Material.BLACK_STAINED_GLASS_PANE || customId == CustomID.NO_POSTS.name) return


    }

    override fun onInventoryClose(event: InventoryCloseEvent) {
        if (event.inventory.holder !is BaseInventoryGUI) return
    }
}