package net.rk4z.bulletinboard.manager

import net.rk4z.bulletinboard.utils.Commands
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
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
                //TODO: Apply localization
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
            //TODO: Apply localization
            sender.sendMessage("Unknown command.")
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
        //TODO
    }
}