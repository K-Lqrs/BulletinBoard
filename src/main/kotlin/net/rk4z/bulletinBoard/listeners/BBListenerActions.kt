package net.rk4z.bulletinBoard.listeners

import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.utils.PlayerState
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent

object BBListenerActions {

    fun handleBBClick(
        player: Player,
        customId: String?,
        inventoryTitle: Component,
        state: PlayerState,
        event: InventoryClickEvent
    ) {

    }

    fun handlePlayerCommandPreprocess(
        player: Player,
        command: String,
        event: PlayerCommandPreprocessEvent
    ) {

    }
}