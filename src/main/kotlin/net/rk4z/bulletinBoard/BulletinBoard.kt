@file:Suppress("unused", "DEPRECATION", "MemberVisibilityCanBePrivate")

package net.rk4z.bulletinboard

import net.rk4z.bulletinboard.utils.Permissions
import net.rk4z.bulletinboard.utils.Settings
import org.bukkit.NamespacedKey
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
    val permissionFile: File = dataFolder.resolve("permission.json")
    val settingFile: File = dataFolder.resolve("setting.json")
    lateinit var permissions: Permissions
    lateinit var settings: Settings

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