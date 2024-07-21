package net.rk4z.bulletinBoard

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.rk4z.bulletinBoard.listeners.BulletinBoardListener
import net.rk4z.bulletinBoard.listeners.ChatListener
import net.rk4z.bulletinBoard.listeners.PlayerJoinListener
import net.rk4z.bulletinBoard.manager.BulletinBoardManager
import net.rk4z.bulletinBoard.manager.LanguageManager
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

@Suppress("unused", "MemberVisibilityCanBePrivate", "DEPRECATION")
class BulletinBoard : JavaPlugin() {
    companion object {
        lateinit var instance: BulletinBoard
            private set
        lateinit var namespacedKey: NamespacedKey
            private set
    }

    val version = this.description.version

    val logger: Logger = LoggerFactory.getLogger(this::class.java.simpleName)

    val dataFile: File = dataFolder.resolve("data.json")

    override fun onLoad() {
        logger.info("Loading $name v$version")
        instance = this
        namespacedKey = NamespacedKey(instance, "bulletinboard")
        checkDataFolderAndDataFile()
    }

    override fun onEnable() {
        server.pluginManager.apply {
            registerEvents(ChatListener(), this@BulletinBoard)
            registerEvents(PlayerJoinListener(), this@BulletinBoard)
            registerEvents(BulletinBoardListener(), this@BulletinBoard)
        }
        logger.info("$name v$version Enabled!")
    }

    override fun onDisable() {
        logger.info("$name v$version Disabled!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name.equals("bb", ignoreCase = true)) {
            if (args.isEmpty()) {
                if (sender is Player) {
                    val player: Player = sender
                    sender.sendMessage(LanguageManager.getMessage(player, "please_use_help"))
                    return true
                } else {
                    sender.sendMessage("This command can only be used by players.")
                }
            }

            when (args[0].lowercase()) {
                "openboard" -> {
                    if (sender is Player) {
                        BulletinBoardManager.openMainBoard(sender)
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                    }
                }
                "newpost" -> {
                    if (sender is Player) {
                        BulletinBoardManager.openPostEditor(sender)
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                    }
                }
                "myposts" -> {
                    if (sender is Player) {
                        BulletinBoardManager.openMyPosts(sender)
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                    }
                }
                "posts" -> {
                    if (sender is Player) {
                        BulletinBoardManager.openAllPosts(sender)
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                    }
                }
                "previewclose" -> {
                    if (sender is Player) {
                        BulletinBoardManager.Previews.closePreview(sender)
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                    }
                }
                "help" -> {
                    if (sender is Player) {
                        sender.sendMessage(Component.text("Usage: /bboard <subcommand>").color(NamedTextColor.YELLOW))
                        sender.sendMessage(Component.text(""))
                        sender.sendMessage(Component.text("-- Subcommands --").color(NamedTextColor.GOLD))
                        sender.sendMessage(
                            Component.text("openboard - ").append(LanguageManager.getMessage(sender, "usage_openboard")).color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(
                            Component.text("newpost - ").append(LanguageManager.getMessage(sender, "usage_newpost")).color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(
                            Component.text("myposts - ").append(LanguageManager.getMessage(sender, "usage_myposts")).color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(
                            Component.text("posts - ").append(LanguageManager.getMessage(sender, "usage_posts")).color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(
                            Component.text("previewclose - ").append(LanguageManager.getMessage(sender, "usage_previewclose")).color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(Component.text("------------------").color(NamedTextColor.GOLD))
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                    }
                }
                "about" -> {
                    val header = Component.text("=== BulletinBoard ===")
                        .color(NamedTextColor.DARK_GREEN)
                        .decorate(TextDecoration.BOLD)

                    val versionMessage = Component.text("Version: v$version")
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD)

                    val authorMessage = Component.text("Author: rk4z")
                        .color(NamedTextColor.BLUE)
                        .decorate(TextDecoration.ITALIC)

                    val description = Component.text("A Minecraft bulletin board plugin.")
                        .color(NamedTextColor.WHITE)

                    val footer = Component.text("===================")
                        .color(NamedTextColor.DARK_GREEN)
                        .decorate(TextDecoration.BOLD)

                    sender.sendMessage(header)
                    sender.sendMessage(versionMessage)
                    sender.sendMessage(authorMessage)
                    sender.sendMessage(description)
                    sender.sendMessage(footer)
                    return true
                }
                "sec" -> {
                    sender.sendMessage(Component.text("Oh, you found me! Good job!"))
                    return true
                }

                "howtouse" -> {
                    if (sender is Player) {
                        val player: Player = sender

                        val headerComponent = LanguageManager.getMessage(player, "htu_header")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)

                        val hStartComponent = Component.text("=====")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)

                        val hEndComponent = Component.text("=====")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD)

                        sender.sendMessage(
                            hStartComponent.append(headerComponent).append(hEndComponent)
                        )

                        sender.sendMessage(
                            Component
                                .text(LanguageManager.getContentFromMessage(player, "htu_title"))
                                .color(NamedTextColor.YELLOW))

                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text(LanguageManager.getContentFromMessage(player, "htu_openboard"))
                                .color(NamedTextColor.GREEN)
                        )

                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text(LanguageManager.getContentFromMessage(player, "htu_newpost"))
                                .color(NamedTextColor.GREEN)
                        )

                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text(LanguageManager.getContentFromMessage(player, "htu_myposts"))
                                .color(NamedTextColor.GREEN)
                        )
                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text(LanguageManager.getContentFromMessage(player, "htu_posts"))
                                .color(NamedTextColor.GREEN)
                        )

                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text(LanguageManager.getContentFromMessage(player, "htu_preview"))
                                .color(NamedTextColor.GREEN)
                        )

                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(
                            Component.text(LanguageManager.getContentFromMessage(player, "htu_previewclose"))
                                .color(NamedTextColor.GREEN)
                        )

                        sender.sendMessage(Component.text(""))

                        sender.sendMessage(Component.text("=====================================")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD))
                    }
                }

                else -> {
                    sender.sendMessage("Unknown subcommand. Usage: /bboard <openboard | newpost | myposts | posts>")
                }
            }
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        if (command.name.equals("bb", ignoreCase = true)) {
            if (args.size == 1) {
                val subCommands = listOf("openboard", "newpost", "myposts", "posts", "previewclose", "help", "about", "howtouse")
                return subCommands.filter { it.startsWith(args[0], ignoreCase = true) }
            }
        }
        return null
    }

    private fun checkDataFolderAndDataFile() {
        try {
            if (!dataFolder.exists()) {
                logger.info("Creating data folder for $name")
                dataFolder.mkdirs()
            }

            if (!dataFile.exists()) {
                logger.info("Creating data file for $name")
                dataFile.createNewFile()
            }
        } catch (e: IOException) {
            logger.error("Error creating data file for $name", e)
        } catch (e: Exception) {
            logger.error("Error creating data file for $name", e)
        }
    }
}
