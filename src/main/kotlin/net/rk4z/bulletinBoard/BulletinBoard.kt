package net.rk4z.bulletinBoard

import net.rk4z.bulletinBoard.listeners.ChatListener
import net.rk4z.bulletinBoard.listeners.PlayerJoinListener
import net.rk4z.bulletinBoard.manager.BulletinBoardManager
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

@Suppress("unused", "MemberVisibilityCanBePrivate")
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

    val bulletinBoardManager = BulletinBoardManager()

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
            registerEvents(BulletinBoardManager(), this@BulletinBoard)
        }
        logger.info("$name v$version Enabled!")
    }

    override fun onDisable() {
        logger.info("$name v$version Disabled!")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name.equals("bboard", ignoreCase = true)) {
            if (args.isEmpty()) {
                sender.sendMessage("Usage: /bboard <subcommand>")
                return true
            }

            when (args[0].lowercase()) {
                "openboard" -> {
                    if (sender is Player) {
                        bulletinBoardManager.openMainBoard(sender)
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                    }
                }
                "newpost" -> {
                    if (sender is Player) {
                        bulletinBoardManager.openPostEditor(sender)
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                    }
                }
                "myposts" -> {
                    if (sender is Player) {
                        bulletinBoardManager.openMyPosts(sender)
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                    }
                }
                "posts" -> {
                    if (sender is Player) {
                        bulletinBoardManager.openAllPosts(sender)
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
                    }
                }
                "previewclose" -> {
                    if (sender is Player) {
                        bulletinBoardManager.closePreview(sender)
                        return true
                    } else {
                        sender.sendMessage("This command can only be used by players.")
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
        if (command.name.equals("bboard", ignoreCase = true)) {
            if (args.size == 1) {
                val subCommands = listOf("openboard", "newpost", "myposts", "posts", "previewclose")
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
