package net.rk4z.bulletinBoard.managers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.rk4z.beacon.IEventHandler
import net.rk4z.beacon.returnableHandler
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.BulletinBoard.Companion.runTask
import net.rk4z.bulletinBoard.events.BulletinBoardOnCommandEvent
import net.rk4z.bulletinBoard.events.BulletinBoardOnTabCompleteEvent
import net.rk4z.bulletinBoard.utils.Commands
import net.rk4z.bulletinBoard.utils.MessageKey
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player

@Suppress("unused")
object BBCommandManager : IEventHandler {
    private val p = BulletinBoard.instance
    private val subCommandsList: List<String> = Commands.entries.map { it.name }

    val onCommand: Unit = returnableHandler<BulletinBoardOnCommandEvent, Boolean> { event ->
        val sender = event.sender
        val command = event.command
        val args = event.args

        if (command.name.equals("bb", ignoreCase = true)) {
            if (sender !is Player) {
                sender.sendMessage("This command can only be run by a player.")
                return@returnableHandler true
            }

            if (args.isNullOrEmpty() || subCommandsList.contains(args[0].lowercase()).not()) {
                runTask(p) {
                    sender.performCommand("bb help")
                }
                return@returnableHandler true
            }

            Commands.fromString(args[0])?.execute?.invoke(sender) ?: run {
                sender.sendMessage("Unknown command.")
                return@returnableHandler true
            }
        }
        return@returnableHandler false
    }

    val onTabComplete: Unit = returnableHandler<BulletinBoardOnTabCompleteEvent, List<String>?> { event ->
        val command = event.command
        val args = event.args

        if (command.name.equals("bb", ignoreCase = true)) {
            if (args.isNullOrEmpty()) {
                return@returnableHandler subCommandsList
            }

            if (args.size == 1) {
                return@returnableHandler subCommandsList
                    .filter { it.startsWith(args[0].lowercase()) }
            }
        }
        return@returnableHandler null
    }

    fun displayHelp(player: Player) {
        val headerComponent = LanguageManager.getMessage(player, MessageKey.USAGE_HEADER)
            .color(NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD)

        val commandsDescription = listOf(
            "openboard" to MessageKey.USAGE_OPENBOARD,
            "newpost" to MessageKey.USAGE_NEWPOST,
            "myposts" to MessageKey.USAGE_MYPOSTS,
            "posts" to MessageKey.USAGE_POSTS,
            "settings" to MessageKey.USAGE_SETTINGS,
            "deletedposts" to MessageKey.USAGE_DELETED_POSTS,
            "previewclose" to MessageKey.USAGE_PREVIEWCLOSE
        )

        player.sendMessage(headerComponent)

        commandsDescription.forEach { (command, key) ->
            player.sendMessage(
                Component.text("$command - ").append(LanguageManager.getMessage(player, key))
                    .color(NamedTextColor.GREEN)
            )
        }
        player.sendMessage(Component.text("=======================").color(NamedTextColor.GOLD))
    }

    fun displayAbout(player: Player) {
        val header = Component.text("=== BulletinBoard ===")
            .color(NamedTextColor.DARK_GREEN)
            .decorate(TextDecoration.BOLD)

        val versionMessage = Component.text("Version: v${BulletinBoard.instance.version}")
            .color(NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD)

        val authorMessage = Component.text("Made by ${BulletinBoard.instance.author.joinToString(", ")}")
            .color(NamedTextColor.BLUE)
            .decorate(TextDecoration.ITALIC)

        val description = Component.text(BulletinBoard.instance.pluginDes ?: "No Description")
            .color(NamedTextColor.WHITE)

        player.sendMessage(header)
        player.sendMessage(versionMessage)
        player.sendMessage(authorMessage)
        player.sendMessage(description)
        player.sendMessage(Component.text("===================")
            .color(NamedTextColor.DARK_GREEN)
            .decorate(TextDecoration.BOLD))
    }

    fun displayHowToUse(player: Player) {
        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1.0f, 1.0f)

        val headerComponent = LanguageManager.getMessage(player, MessageKey.HTU_HEADER)
            .color(NamedTextColor.GOLD)
            .decorate(TextDecoration.BOLD)

        val contentKeys = listOf(
            MessageKey.HTU_OPENBOARD,
            MessageKey.HTU_NEWPOST,
            MessageKey.HTU_MYPOSTS,
            MessageKey.HTU_POSTS,
            MessageKey.HTU_PREVIEW,
            MessageKey.HTU_PREVIEW_CLOSE
        )

        player.sendMessage(headerComponent)

        contentKeys.forEach { key ->
            player.sendMessage(
                Component.text(LanguageManager.getContentFromMessage(player, key))
                    .color(NamedTextColor.GREEN)
            )
            player.sendMessage(Component.text(""))
        }

        player.sendMessage(
            Component.text("=====================================")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD)
        )
    }

}