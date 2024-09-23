package net.rk4z.bulletinboard

import net.rk4z.bulletinboard.listener.BBListener
import net.rk4z.bulletinboard.manager.CommandManager
import net.rk4z.bulletinboard.manager.LanguageManager
import net.rk4z.bulletinboard.utils.MessageKey
import net.rk4z.bulletinboard.utils.System
import net.rk4z.bulletinboard.utils.getNullableBoolean
import net.rk4z.igf.IGF
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.io.path.notExists

typealias TaskRunner = (JavaPlugin, Runnable) -> Unit
typealias TaskRunnerWithDelay = (JavaPlugin, Runnable, Long) -> Unit
typealias TaskRunnerWithPeriod = (JavaPlugin, Runnable, Long, Long) -> Unit
typealias BukkitTaskRunner = (BukkitRunnable) -> Unit

@Suppress("unused", "DEPRECATION")
class BulletinBoard : JavaPlugin() {
    companion object {
        lateinit var instance: BulletinBoard
            private set
        lateinit var key: NamespacedKey
            private set
        lateinit var dataBase: DataBase
            private set

        const val ID = "bulletinboard"
        const val MODRINTH_API_URL = "https://api.modrinth.com/v2/project/AfO6aot1/version"
    }

    val runTask : TaskRunner = { plugin, task -> Bukkit.getScheduler().runTask(plugin, task) }
    val runTaskAsync : TaskRunner = { plugin, task -> Bukkit.getScheduler().runTaskAsynchronously(plugin, task) }
    val runTaskLater : TaskRunnerWithDelay = { plugin, task, delay -> Bukkit.getScheduler().runTaskLater(plugin, task, delay) }
    val runTaskTimer : TaskRunnerWithPeriod = { plugin, task, delay, period -> Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period) }
    val bukkitRunTask : BukkitTaskRunner = { task -> task.runTask(this) }
    val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    var isProxied: Boolean? = false
    // If you want to debug the plugin, set this to true
    val isDebug: Boolean = false

    var systemLang: String = Locale.getDefault().language

    val version = description.version
    val log: Logger = LoggerFactory.getLogger(BulletinBoard::class.java.simpleName)
    val configFile = dataFolder.resolve("config.yml").toPath()
    val langDir = dataFolder.resolve("lang")
    val yaml = Yaml()
    val availableLang = listOf(
        "ja",
        "en",
    )

    override fun onLoad() {
        instance = getPlugin(BulletinBoard::class.java)
        key = NamespacedKey(this, ID)
        systemLang = Locale.getDefault().language

        if (dataFolder.toPath().notExists()) {
            dataFolder.mkdirs()
        }

        if (configFile.notExists()) {
            saveResource("config.yml", false)
        }

        if (!langDir.exists()) {
            langDir.mkdirs()
        }

        availableLang.forEach { lang ->
            val langFile = File(langDir, "$lang.yml")
            if (!langFile.exists()) {
                saveResource("lang/$lang.yml", false)
                if (isDebug) log.info("Copied default $lang language file from Jar.")
            }
        }

        availableLang.forEach { lang ->
            val langFile = langDir.resolve("$lang.yml")

            if (lang == "ja") {
                if (Files.exists(langFile.toPath())) {
                    Files.newInputStream(langFile.toPath()).use { inputStream ->
                        InputStreamReader(inputStream, StandardCharsets.UTF_8).use { reader ->
                            val data: Map<String, Any> = yaml.load(reader)
                            val messageMap: MutableMap<MessageKey, String> = mutableMapOf()

                            LanguageManager.processYamlAndMapMessageKeys(data, messageMap)

                            LanguageManager.messages[lang] = messageMap
                        }
                    }
                } else {
                    log.warn("Japanese language file for '$lang' not found.")
                }
            } else {
                if (Files.exists(langFile.toPath())) {
                    Files.newBufferedReader(langFile.toPath(), StandardCharsets.UTF_8).use { reader ->
                        val data: Map<String, Any> = yaml.load(reader)
                        val messageMap: MutableMap<MessageKey, String> = mutableMapOf()

                        LanguageManager.processYamlAndMapMessageKeys(data, messageMap)

                        LanguageManager.messages[lang] = messageMap
                    }
                } else {
                    log.warn("Language file for '$lang' not found.")
                }
            }
        }

        log.info(LanguageManager.getSysMessage(systemLang, System.Log.LOADING, name, version))

        dataBase = DataBase(this)

        checkUpdate()

        loadConfig()

        if (dataBase.connectToDatabase()) {
            dataBase.createRequiredTables()
            if (!dataBase.isDataMigrated()) {
                val jsonFile = File(dataFolder, "data.json")
                if (jsonFile.exists()) {
                    logger.info("Old data file is found. trying to import data from json....")
                    dataBase.importDataFromJson(jsonFile)
                }
            }
        }
    }

    override fun onEnable() {
        log.info(LanguageManager.getSysMessage(systemLang, System.Log.ENABLING, name, version))

        IGF.init(this, key)
        IGF.setGlobalListener(BBListener())

        registerCommand("bulletinboard", this)
    }

    override fun onDisable() {
        log.info(LanguageManager.getSysMessage(systemLang, System.Log.DISABLING, name, version))

        dataBase.closeConnection()
    }

    private fun registerCommand(commandName: String, plugin: JavaPlugin) {
        val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        commandMapField.isAccessible = true
        val commandMap = commandMapField.get(Bukkit.getServer()) as CommandMap

        val command = object : Command(commandName) {
            override fun execute(sender: CommandSender, label: String, args: Array<out String>): Boolean {
                return CommandManager.onCommand(sender, this, label, args)
            }

            override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>?): List<String?> {
                return CommandManager.onTabComplete(sender, this, alias, args)
            }
        }

        command.aliases = listOf("bb")
        command.description = "BulletinBoard Main Command"

        commandMap.register(plugin.description.name, command)
    }

    private fun checkUpdate() {
        executor.execute {
            log.info(LanguageManager.getSysMessage(systemLang, System.Log.CHECKING_UPDATE))
            try {
                val url = URI(MODRINTH_API_URL).toURL()
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()

                    val (latestVersion, versionCount, newerVersionCount) = extractVersionInfo(response)

                    log.info(LanguageManager.getSysMessage(systemLang, System.Log.ALL_VERSION_COUNT, versionCount))

                    if (isVersionNewer(latestVersion, description.version)) {
                        log.info(LanguageManager.getSysMessage(systemLang, System.Log.NEW_VERSION_COUNT, newerVersionCount))
                        log.info(LanguageManager.getSysMessage(systemLang, System.Log.LATEST_VERSION_FOUND, latestVersion, version))

                        val downloadUrl = "https://modrinth.com/plugin/AfO6aot1/version/$latestVersion"
                        log.info(LanguageManager.getSysMessage(systemLang, System.Log.VIEW_LATEST_VER, downloadUrl))
                    } else {
                        log.info(LanguageManager.getSysMessage(systemLang, System.Log.YOU_ARE_USING_LATEST))
                    }
                } else {
                    log.warn(LanguageManager.getSysMessage(systemLang, System.Log.FAILED_TO_CHECK_UPDATE, connection.responseCode))
                }
            } catch (e: Exception) {
                val unknownError = LanguageManager.getSysMessage(systemLang, System.Log.Other.UNKNOWN_ERROR)
                log.warn(LanguageManager.getSysMessage(systemLang, System.Log.ERROR_WHILE_CHECKING_UPDATE, e.message ?: unknownError))
            }
        }
    }

    private fun isVersionNewer(version1: String, version2: String): Boolean {
        val v1Parts = version1.split(".").map { it.toIntOrNull() ?: 0 }
        val v2Parts = version2.split(".").map { it.toIntOrNull() ?: 0 }

        val maxLength = maxOf(v1Parts.size, v2Parts.size)
        val v1Padded = v1Parts + List(maxLength - v1Parts.size) { 0 }
        val v2Padded = v2Parts + List(maxLength - v2Parts.size) { 0 }

        for (i in 0 until maxLength) {
            if (v1Padded[i] > v2Padded[i]) return true
            if (v1Padded[i] < v2Padded[i]) return false
        }

        return false
    }

    private fun extractVersionInfo(response: String): Triple<String, Int, Int> {
        val jsonArray = JSONArray(response)
        var latestVersion = ""
        var latestDate = ""
        val versionCount = jsonArray.length()
        var newerVersionCount = 0

        for (i in 0 until jsonArray.length()) {
            val versionObject = jsonArray.getJSONObject(i)
            val versionNumber = versionObject.getString("version_number")
            val releaseDate = versionObject.getString("date_published")

            if (isVersionNewer(versionNumber, description.version)) {
                newerVersionCount++
            }

            if (releaseDate > latestDate) {
                latestDate = releaseDate
                latestVersion = versionNumber
            }
        }

        return Triple(latestVersion, versionCount, newerVersionCount)
    }

    private fun loadConfig() {
        Files.newInputStream(configFile).use { inputStream ->
            val config: Map<String, Any> = yaml.load(inputStream)

            isProxied = config.getNullableBoolean("isProxied") ?: false
        }
    }
}