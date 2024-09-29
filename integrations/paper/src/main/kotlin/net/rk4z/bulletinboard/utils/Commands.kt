package net.rk4z.bulletinboard.utils

import net.rk4z.bulletinboard.guis.openMainBoard
import net.rk4z.bulletinboard.manager.CommandManager.displayHelp
import org.bukkit.entity.Player

typealias CommandExecute = (Player) -> Unit

enum class Commands(val execute: CommandExecute) {
    //TODO: Add more commands
    OPENBOARD({ player -> openMainBoard(player) }),
    HELP({ player -> displayHelp(player) }),
    ABOUT({ player -> player.sendMessage("This is a plugin that allows you to create posts and view them in a GUI.") })
    ;

    companion object {
        fun fromString(name: String): Commands? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}