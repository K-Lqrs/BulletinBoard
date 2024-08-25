package net.rk4z.bulletinBoard.events

import net.rk4z.beacon.ReturnableEvent
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

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