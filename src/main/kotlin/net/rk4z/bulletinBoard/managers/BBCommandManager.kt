package net.rk4z.bulletinBoard.managers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.rk4z.beacon.EventHandler
import net.rk4z.beacon.IEventHandler
import net.rk4z.beacon.Priority
import net.rk4z.beacon.returnableHandler
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.events.*
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player

@Suppress("unused")
@EventHandler
class BBCommandManager : IEventHandler {
    val onCommand: Unit = returnableHandler<BulletinBoardOnCommandEvent, Boolean> {
        val sender = it.sender
        val command = it.command
        val args = it.args

        if (command.name.equals("bb", ignoreCase = true)) {
            if (args.isNullOrEmpty()) {
                if (sender is Player) {
                    val player: Player = sender
                    player.performCommand("bb help")
                    return@returnableHandler true
                } else {
                    sender.sendMessage("This Command can only be run by a player.")
                    return@returnableHandler true
                }
            }

            when (args[0].lowercase()) {
                "openboard" -> {
                    if (sender is Player) {
                        BulletinBoardManager.openMainBoard(sender)
                        return@returnableHandler true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                        return@returnableHandler true
                    }
                }

                "help" -> {
                    if (sender is Player) {
                        val headerComponent = LanguageManager.getMessage(sender, "usageHeader")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)

                        val hStartComponent = Component.text("=======")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)

                        val hEndComponent = Component.text("=======")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)

                        sender.sendMessage(hStartComponent.append(headerComponent).append(hEndComponent))

                        sender.sendMessage(
                            Component.text("openboard - ").append(LanguageManager.getMessage(sender, "usageOpenBoard"))
                                .color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(
                            Component.text("newpost - ").append(LanguageManager.getMessage(sender, "usageNewPost"))
                                .color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(
                            Component.text("myposts - ").append(LanguageManager.getMessage(sender, "usageMyPosts"))
                                .color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(
                            Component.text("posts - ").append(LanguageManager.getMessage(sender, "usagePosts"))
                                .color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(
                            Component.text("settings - ").append(LanguageManager.getMessage(sender, "usageSettings"))
                                .color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(
                            Component.text("deletedposts - ")
                                .append(LanguageManager.getMessage(sender, "usageDeletedPosts"))
                                .color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(
                            Component.text("previewclose - ")
                                .append(LanguageManager.getMessage(sender, "usagePreviewClose"))
                                .color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(Component.text("=======================").color(NamedTextColor.GOLD))
                        return@returnableHandler true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                    }
                }

                "about" -> {
                    if (sender is Player) {
                        val header = Component.text("=== BulletinBoard ===")
                            .color(NamedTextColor.DARK_GREEN)
                            .decorate(TextDecoration.BOLD)

                        val versionMessage = Component.text("Version: v${BulletinBoard.instance.version}")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)

                        val authorMessage =
                            Component.text("Made by ${BulletinBoard.instance.author.joinToString(", ")}")
                                .color(NamedTextColor.BLUE)
                                .decorate(TextDecoration.ITALIC)

                        val description = Component.text(BulletinBoard.instance.pluginDes!!)
                            .color(NamedTextColor.WHITE)

                        val footer = Component.text("===================")
                            .color(NamedTextColor.DARK_GREEN)
                            .decorate(TextDecoration.BOLD)

                        sender.sendMessage(header)
                        sender.sendMessage(versionMessage)
                        sender.sendMessage(authorMessage)
                        sender.sendMessage(description)
                        sender.sendMessage(footer)
                        return@returnableHandler true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                        return@returnableHandler true
                    }

                }

                "howtouse" -> {
                    if (sender is Player) {
                        val player: Player = sender
                        val location = player.location
                        player.playSound(location, Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1.0f, 1.0f)

                        val headerComponent = LanguageManager.getMessage(player, "htuHeader")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)
                        val hStartComponent = Component.text("=====")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)
                        val hEndComponent = Component.text("=====")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)

                        sender.sendMessage(hStartComponent.append(headerComponent).append(hEndComponent))

                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text(LanguageManager.getContentFromMessage(player, "htuOpenboard"))
                                .color(NamedTextColor.GREEN)
                        )

                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text(LanguageManager.getContentFromMessage(player, "htuNewPost"))
                                .color(NamedTextColor.GREEN)
                        )

                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text(LanguageManager.getContentFromMessage(player, "htuMyPosts"))
                                .color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text(LanguageManager.getContentFromMessage(player, "htuPosts"))
                                .color(NamedTextColor.GREEN)
                        )

                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text(LanguageManager.getContentFromMessage(player, "htuPreview"))
                                .color(NamedTextColor.GREEN)
                        )

                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text(LanguageManager.getContentFromMessage(player, "htuPreviewClose"))
                                .color(NamedTextColor.GREEN)
                        )

                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text("=====================================")
                                .color(NamedTextColor.GOLD)
                                .decorate(TextDecoration.BOLD)
                        )

                        return@returnableHandler true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                        return@returnableHandler true
                    }
                }
            }
        }
        return@returnableHandler false
    }

    val onTabComplete = returnableHandler<BulletinBoardOnTabCompleteEvent, MutableList<String>?> { event ->
        val command = event.command
        val args = event.args

        val subCommandsList = BulletinBoard.instance.subCommands

        if (command.name.equals("bb", ignoreCase = true)) {
            if (args.isNullOrEmpty()) {
                return@returnableHandler null
            }

            if (args.size == 1) {
                return@returnableHandler subCommandsList
            }
        }
        return@returnableHandler null
    }

}