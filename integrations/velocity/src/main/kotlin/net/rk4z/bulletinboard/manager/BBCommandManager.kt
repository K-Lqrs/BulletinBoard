package net.rk4z.bulletinboard.manager

import com.velocitypowered.api.command.SimpleCommand
import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.utils.Commands

class BBCommandManager : SimpleCommand {
    private val subCommandsList: List<String> = Commands.entries.map { it.name }

    override fun execute(invocation: SimpleCommand.Invocation) {
        val sender = invocation.source()
        val args = invocation.arguments()

        if (args.isEmpty() || !subCommandsList.map { it.lowercase() }.contains(args[0].lowercase())) {
            if (sender is Player) {
                //TODO: Not yet implemented
                //displayHelp(sender)
            } else {
                sender.sendMessage(Component.text("This command can only be run by a player."))
            }
            return
        }

        val commandEnum = Commands.fromString(args[0])
        if (commandEnum != null && sender is Player) {
            commandEnum.execute(sender)
        } else {
            sender.sendMessage(Component.text("Unknown command."))
        }
    }

    override fun suggest(invocation: SimpleCommand.Invocation?): MutableList<String> {
        return super.suggest(invocation)
    }
}

