package net.rk4z.bulletinboard.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.rk4z.bulletinboard.utils.Commands
import net.rk4z.bulletinboard.utils.Main
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import kotlin.text.append
import kotlin.text.lowercase

object CommandManager : CommandExecutor, TabCompleter {
    private val subCommandsList: List<String> = Commands.entries.map { it.name }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        if (args.isNullOrEmpty() || subCommandsList.map { it.lowercase() }.contains(args[0]).not()) {
            if (sender is Player) {
                displayHelp(sender)
                return true
            } else {
                sender.sendMessage(LanguageManager.getSysMessage(Main.Command.Message.PLAYER_ONLY))
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
            if (sender is Player) {
                sender.sendMessage(LanguageManager.getMessage(sender, Main.Command.Message.UNKNOWN_COMMAND))
            } else {
                sender.sendMessage(LanguageManager.getSysMessage(Main.Command.Message.UNKNOWN_COMMAND))
            }
            return true
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): List<String?> {
        if (args.isNullOrEmpty()) {
            return subCommandsList.map { it.lowercase() }
        }

        if (args.size == 1) {
            return subCommandsList
                .map { it.lowercase() }
                .filter { it.startsWith(args[0].lowercase()) }
        }

        return listOf()
    }

    fun displayHelp(player: Player) {
        val headerComponent = LanguageManager.getMessage(player, Main.Command.Help.USAGE_HEADER)
            .color(NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD)

        val hStartComponent = Component.text("=======").color(NamedTextColor.GOLD)
        val hEndComponent = Component.text("=======").color(NamedTextColor.GOLD)

        val commandsDescription = listOf(
            "openboard" to Main.Command.Help.USAGE_OPENBOARD,
            "newpost" to Main.Command.Help.USAGE_NEWPOST,
            "myposts" to Main.Command.Help.USAGE_MYPOSTS,
            "posts" to Main.Command.Help.USAGE_POSTS,
            "settings" to Main.Command.Help.USAGE_SETTINGS,
            "deletedposts" to Main.Command.Help.USAGE_DELETED_POSTS,
            "previewclose" to Main.Command.Help.USAGE_PREVIEWCLOSE
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