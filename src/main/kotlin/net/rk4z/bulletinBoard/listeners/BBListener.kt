package net.rk4z.bulletinBoard.listeners

import net.rk4z.bulletinBoard.managers.BulletinBoardManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.persistence.PersistentDataType

class BBListener : Listener {

    @EventHandler
    fun onBulletinBoardClick(event: InventoryClickEvent) {
        val clickedItem = event.currentItem ?: return
        val player = event.whoClicked as Player
        val inventoryTitle = event.view.title()
        val itemMeta = clickedItem.itemMeta ?: return
        val customId = itemMeta.persistentDataContainer.get(BulletinBoard.namespacedKey, PersistentDataType.STRING)

        if (clickedItem.type.isAir) {
            return
        }

        if (clickedItem.type == Material.BLACK_STAINED_GLASS_PANE || customId == "no_posts") {
            event.isCancelled = true
            return
        }

        val state = BulletinBoardManager.getPlayerState(player.uniqueId)

        BBListenerActions.handleBBClick(player, customId, inventoryTitle, state, event)
    }

    @EventHandler
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val command = event.message

        //BBListenerActions.handlePlayerCommandPreprocess(player, command, event)
    }
}