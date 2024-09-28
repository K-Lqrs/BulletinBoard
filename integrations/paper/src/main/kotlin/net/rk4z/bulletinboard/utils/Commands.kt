package net.rk4z.bulletinboard.utils

import net.rk4z.bulletinboard.guis.openMainBoard
import net.rk4z.bulletinboard.manager.CommandManager.displayHelp
import org.bukkit.entity.Player

typealias CommandExecute = (Player) -> Unit

enum class Commands(val execute: CommandExecute) {
    //TODO: Add more commands
    OPENBOARD({ player -> openMainBoard(player) }),
    HELP({ player -> displayHelp(player) })
    ;

    companion object {
        fun fromString(name: String): Commands? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}