@file:Suppress("DEPRECATION")

package net.rk4z.bulletinBoard.events

import net.kyori.adventure.text.Component
import net.rk4z.beacon.Event
import net.rk4z.beacon.ReturnableEvent
import net.rk4z.bulletinBoard.utils.PlayerState
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.AsyncPlayerChatEvent

class BulletinBoardOnCommandEvent(
    val sender: CommandSender,
    val command: Command,
    val args: Array<out String>?
) : ReturnableEvent<Boolean>() {
    companion object {
        fun get(
            sender: CommandSender,
            command: Command,
            args: Array<out String>?
        ): BulletinBoardOnCommandEvent {
            return BulletinBoardOnCommandEvent(
                sender,
                command,
                args
            )
        }
    }
}

class BulletinBoardOnTabCompleteEvent(
    val command: Command,
    val args: Array<out String>?
) : ReturnableEvent<List<String>?>() {
    companion object {
        fun get(
            command: Command,
            args: Array<out String>?
        ): BulletinBoardOnTabCompleteEvent {
            return BulletinBoardOnTabCompleteEvent(
                command,
                args
            )
        }
    }
}

class BulletinBoardClickEvent(
    val player: Player,
    val customId: String?,
    val inventoryTitle: Component,
    val state: PlayerState,
    val event: InventoryClickEvent
) : Event() {
    companion object {
        fun get(
            player: Player,
            customId: String?,
            inventoryTitle: Component,
            state: PlayerState,
            event: InventoryClickEvent
        ): BulletinBoardClickEvent {
            return BulletinBoardClickEvent(
                player,
                customId,
                inventoryTitle,
                state,
                event
            )
        }
    }
}

class BulletinBoardOnChatEvent(
    val player: Player,
    val state: PlayerState,
    val event: AsyncPlayerChatEvent
) : Event() {
    companion object {
        fun get(
            player: Player,
            state: PlayerState,
            event: AsyncPlayerChatEvent
        ): BulletinBoardOnChatEvent {
            return BulletinBoardOnChatEvent(
                player,
                state,
                event
            )
        }
    }
}

class BulletinBoardCloseEvent(
    val player: Player,
    val inventoryTitle: Component,
    val state: PlayerState
) : Event() {
    companion object {
        fun get(
            player: Player,
            inventoryTitle: Component,
            state: PlayerState
        ): BulletinBoardCloseEvent {
            return BulletinBoardCloseEvent(
                player,
                inventoryTitle,
                state
            )
        }
    }
}