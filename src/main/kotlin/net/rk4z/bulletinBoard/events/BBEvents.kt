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
import org.bukkit.event.player.PlayerCommandPreprocessEvent

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
class BulletinBoardCommandPreprocessEvent(
    val player: Player,
    val command: String,
    val event: PlayerCommandPreprocessEvent
): Event() {
    companion object {
        fun get(
            player: Player,
            command: String,
            event: PlayerCommandPreprocessEvent
        ): BulletinBoardCommandPreprocessEvent{
            return BulletinBoardCommandPreprocessEvent(
                player,
                command,
                event
            )
        }
    }
}

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

class BulletinBoardChatEvent(
    val player: Player,
    val event: AsyncPlayerChatEvent
): Event() {
    companion object {
        fun get(
            player: Player,
            event: AsyncPlayerChatEvent
        ): BulletinBoardChatEvent {
            return BulletinBoardChatEvent(
                player,
                event
            )
        }
    }
}