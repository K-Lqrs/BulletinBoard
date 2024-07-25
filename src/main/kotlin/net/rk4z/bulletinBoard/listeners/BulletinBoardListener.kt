@file:Suppress("DEPRECATION", "DuplicatedCode")

package net.rk4z.bulletinBoard.listeners

import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.manager.BulletinBoardManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.persistence.PersistentDataType

class BulletinBoardListener : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val clickedItem = event.currentItem ?: return
        val player = event.whoClicked as Player
        val inventoryTitle = event.view.title()
        val itemMeta = clickedItem.itemMeta ?: return

        if (clickedItem.type.isAir) {
            return
        }
        val customId = itemMeta.persistentDataContainer.get(BulletinBoard.namespacedKey, PersistentDataType.STRING)

        if (clickedItem.type == Material.BLACK_STAINED_GLASS_PANE || customId == "no_posts") {
            event.isCancelled = true
            return
        }

        val state = BulletinBoardManager.getPlayerState(player.uniqueId)
        BBListenerActions.handleInventoryClick(player, customId, inventoryTitle, state, event)
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val state = BulletinBoardManager.getPlayerState(player.uniqueId)
        val inventoryTitle = event.view.title()

        BBListenerActions.handleInventoryClose(player, inventoryTitle, state)
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val state = BulletinBoardManager.getPlayerState(player.uniqueId)

        BBListenerActions.handlePlayerChat(player, event.message, state, event)
    }

    @EventHandler
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val command = event.message

        BBListenerActions.handlePlayerCommandPreprocess(player, command, event)
    }
}
