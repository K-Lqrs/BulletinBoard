@file:Suppress("DEPRECATION")

package net.rk4z.bulletinBoard.listeners

import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.managers.BulletinBoardManager
import net.rk4z.bulletinBoard.utils.CustomID
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryCloseEvent.Reason.*
import org.bukkit.event.player.AsyncPlayerChatEvent
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

        if (clickedItem.type.isAir) return
        if (clickedItem.type == Material.BLACK_STAINED_GLASS_PANE || customId == CustomID.NO_POSTS.name) {
            event.isCancelled = true
            return
        }

        val state = BulletinBoardManager.getPlayerState(player.uniqueId)

        BBListenerHandlers.onBBClick(player, customId, inventoryTitle, state, event)

        Bukkit.getScheduler().runTaskLater(BulletinBoard.instance, Runnable {
            removeItemFromPlayerInventory(player)
        }, 2L)
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val state = BulletinBoardManager.getPlayerState(player.uniqueId)

        BBListenerHandlers.onChat(player, state, event)
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val state = BulletinBoardManager.getPlayerState(player.uniqueId)
        val inventoryTitle = event.view.title()

        when (event.reason) {
            PLAYER,
            TELEPORT,
            DISCONNECT,
            DEATH,
            UNLOADED,
            CANT_USE
                -> state.clear()

            PLUGIN, OPEN_NEW -> {
                BBListenerHandlers.onInvClose(player, inventoryTitle, state)
            }

            UNKNOWN  -> {
                BulletinBoard.instance.logger.warn("Unknown InventoryCloseEvent.Reason: ${event.reason}")
                state.clear()
            }
        }

        removeItemFromPlayerInventory(player)
    }

    @EventHandler
    fun onCommandPreProcess(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val message = event.message

        BBListenerHandlers.onCommandPreProcess(player, message)
    }

    private fun removeItemFromPlayerInventory(player: Player) {
        val inventory = player.inventory
        val allIds = BulletinBoard.database.getAllIds()

        inventory.contents
            .filterNotNull()  // Nullチェック
            .filter { item ->
                val itemMeta = item.itemMeta
                val itemCustomIdName = itemMeta?.persistentDataContainer?.get(BulletinBoard.namespacedKey, PersistentDataType.STRING)
                itemCustomIdName != null && allIds.contains(itemCustomIdName)
            }
            .forEach { item ->
                inventory.remove(item)
            }
    }
}