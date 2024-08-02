package net.rk4z.bulletinBoard.managers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.rk4z.bulletinBoard.BulletinBoard
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object BBCommandManager {

    fun handleCommand(
        sender: CommandSender,
        command: Command,
        args: Array<out String>?
    ): Boolean {
        if (command.name.equals("bb", ignoreCase = true)) {
            if (args.isNullOrEmpty() || !BulletinBoard.instance.subCommands.contains(args[0].lowercase())) {
                if (sender is Player) {
                    val player: Player = sender
                    player.performCommand("bb help")
                    return true
                } else {
                    sender.sendMessage("This command can only be run by a player.")
                    return true
                }
            }

            when (args[0].lowercase()) {
                "openboard" -> {
                    if (sender is Player) {
                        BulletinBoardManager.openMainBoard(sender)
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                        return true
                    }
                }

                "help" -> {
                    if (sender is Player) {
                        val player: Player = sender
                        val location = player.location
                        sender.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1.0f, 2.0f)

                        val headerComponent = LanguageManager.getMessage(player, "htu_header")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)

                        val hStartComponent = Component.text("=====")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)

                        val hEndComponent = Component.text("=====")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                        return true
                    }
                }
            }
        }
        return false
    }
}