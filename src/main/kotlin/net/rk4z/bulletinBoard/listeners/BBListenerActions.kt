package net.rk4z.bulletinBoard.listeners

import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.utils.PlayerState
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

object BBListenerActions {

    fun handleBBClick(
        player: Player,
        customId: String?,
        inventoryTitle: Component,
        state: PlayerState,
        event: InventoryClickEvent
    ) {

    }
}