package net.rk4z.bulletinboard

import net.rk4z.bulletinboard.manager.BBCommandManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.notExists

typealias TaskRunner = (JavaPlugin, Runnable) -> Unit

class BulletinBoard : JavaPlugin() {
    companion object {
        lateinit var instance: BulletinBoard
            private set
        lateinit var namespacedKey: NamespacedKey
            private set

        val runTask: TaskRunner = { plugin, runnable ->
            Bukkit.getScheduler().runTask(plugin, runnable)
        }

        val runTaskAsync: TaskRunner = { plugin, runnable ->
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
        }
    }
    val configFile: Path = dataFolder.resolve("config.yml").toPath()
    private val yaml = Yaml()
    val logger = LoggerFactory.getLogger(BulletinBoard::class.java.simpleName)

    var isProxied: Boolean = false

    override fun onLoad() {
        instance = this
        checkRequiredFilesAndDirectories()
        loadConfig()
    }

    override fun onEnable() {
        if (!isProxied) {
            val bbCommand = getCommand("bb")
            bbCommand?.setAliases(listOf("bulletinboard", "bborard"))
            bbCommand?.setExecutor(BBCommandManager())
        }
    }

    private fun checkRequiredFilesAndDirectories() {
        try {
            if (!Files.exists(dataFolder.toPath())) {
                logger.info("Creating config directory at $dataFolder")
                Files.createDirectories(dataFolder.toPath())
            }
            if (configFile.notExists()) {
                saveDefaultConfig()
            }
        } catch (e: SecurityException) {
            logger.error("Failed to create/check required files or directories due to security restrictions", e)
        } catch (e: IOException) {
            logger.error("Failed to create/check required files or directories due to an I/O error", e)
        } catch (e: Exception) {
            logger.error("An unexpected error occurred while creating/checking required files or directories", e)
        }
    }

    private fun loadConfig() {
        try {
            logger.info("Loading config file...")

            if (Files.notExists(configFile)) {
                logger.error("Config file not found at $configFile")
                return
            }

            Files.newInputStream(configFile).use { inputStream ->
                val config: Map<String, Any> = yaml.load(inputStream)

                isProxied = config.getOrDefault("isProxied", false) as Boolean
            }
        } catch (e: IOException) {
            logger.error("Failed to load config file", e)
        } catch (e: Exception) {
            logger.error("An unexpected error occurred while loading config:", e)
        }
    }
}