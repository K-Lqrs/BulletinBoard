@file:Suppress("MemberVisibilityCanBePrivate")

package net.rk4z.bulletinBoard

import net.rk4z.beacon.EventBus
import net.rk4z.bulletinBoard.events.BulletinBoardOnCommandEvent
import net.rk4z.bulletinBoard.events.BulletinBoardOnTabCompleteEvent
import net.rk4z.bulletinBoard.listeners.BBListener
import net.rk4z.bulletinBoard.listeners.BBListenerActions
import net.rk4z.bulletinBoard.listeners.test.Test
import net.rk4z.bulletinBoard.managers.BBCommandManager
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

@Suppress("DEPRECATION")
class BulletinBoard : JavaPlugin() {
    companion object {
        lateinit var instance: BulletinBoard
            private set
        lateinit var namespacedKey: NamespacedKey
            private set
        const val ID = "bulletinboard"
    }

    val logger: Logger = LoggerFactory.getLogger(this::class.java.simpleName)

    val author: List<String> = description.authors
    val version = description.version
    val pluginDes = description.description
    val dataFile: File = dataFolder.resolve("data.json")
    val subCommands = listOf(
        "openboard",
        "help"
    )

    override fun onLoad() {
        instance = this
        namespacedKey = NamespacedKey(instance, ID)
        checkDataFolderAndDataFile()
        EventBus.initialize()

        Test
        BBListenerActions()
        BBCommandManager()
    }

    override fun onEnable() {
        server.pluginManager.apply {
            registerEvents(BBListener(), this@BulletinBoard)
        }
    }

    override fun onDisable() {
        // Umm... in fact, I'm not doing anything.
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        return EventBus.postReturnable(BulletinBoardOnCommandEvent.get(sender, command, args))?: false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>?
    ): List<String>? {
        return EventBus.postReturnable(BulletinBoardOnTabCompleteEvent.get(command, args))
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