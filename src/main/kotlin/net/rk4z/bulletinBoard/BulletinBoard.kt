@file:Suppress("DEPRECATION", "MemberVisibilityCanBePrivate")

package net.rk4z.bulletinBoard

import net.rk4z.bulletinBoard.managers.BBCommandManager
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

class BulletinBoard : JavaPlugin() {
    companion object {
        lateinit var instance: BulletinBoard
            private set
        lateinit var namespacedKey: NamespacedKey
            private set
    }

    val logger: Logger = LoggerFactory.getLogger(this::class.java.simpleName)

    val version = this.description.version

    val dataFile: File = dataFolder.resolve("data.json")

    override fun onLoad() {
        instance = this
        namespacedKey = NamespacedKey(instance, "bulletinboard")
        checkDataFolderAndDataFile()
    }

    override fun onEnable() {

    }

    override fun onDisable() {
        // Umm... in fact, I'm not doing anything.
    }

    val subCommands = listOf(
        "openboard",
        "help"
    )

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        return BBCommandManager.handleCommand(sender, command, args)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>?
    ): MutableList<String>? {
        if (command.name.equals("bb", ignoreCase = true)) {
            if (args?.size == 1) {
                return subCommands.filter { it.startsWith(args[0].lowercase()) }.toMutableList()
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