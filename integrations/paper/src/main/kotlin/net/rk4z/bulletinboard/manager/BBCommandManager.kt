package net.rk4z.bulletinboard.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.rk4z.bulletinboard.utils.Commands
import net.rk4z.bulletinboard.utils.MessageKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BBCommandManager : CommandExecutor {
    private val subCommandsList: List<String> = Commands.entries.map { it.name }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (args.isNullOrEmpty() || subCommandsList.map { it.lowercase() }.contains(args[0]).not()) {
            if (sender is Player) {
                val player: Player = sender
                displayHelp(player)
                return true
            } else {
                sender.sendMessage("This Command can only be run by a player.")
                return true
            }
        }

        val commandEnum = Commands.fromString(args[0])
        if (commandEnum != null && sender is Player) {
            with(commandEnum) {
                execute(sender)
                return true
            }
        } else {
            sender.sendMessage("Unknown command.")
            return true
        }
    }

    fun displayHelp(player: Player) {
        val headerComponent = LanguageManager.getMessage(player, MessageKey.USAGE_HEADER)
            .color(NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD)

        val hStartComponent = Component.text("=======").color(NamedTextColor.GOLD)
        val hEndComponent = Component.text("=======").color(NamedTextColor.GOLD)

        val commandsDescription = listOf(
            "openboard" to MessageKey.USAGE_OPENBOARD,
            "newpost" to MessageKey.USAGE_NEWPOST,
            "myposts" to MessageKey.USAGE_MYPOSTS,
            "posts" to MessageKey.USAGE_POSTS,
            "settings" to MessageKey.USAGE_SETTINGS,
            "deletedposts" to MessageKey.USAGE_DELETED_POSTS,
            "previewclose" to MessageKey.USAGE_PREVIEWCLOSE
        )

        player.sendMessage(hStartComponent.append(headerComponent).append(hEndComponent))

        commandsDescription.forEach { (command, key) ->
            player.sendMessage(
                Component.text("$command - ").append(LanguageManager.getMessage(player, key))
                    .color(NamedTextColor.GREEN)
            )
        }
        player.sendMessage(Component.text("=======================").color(NamedTextColor.GOLD))
    }
}