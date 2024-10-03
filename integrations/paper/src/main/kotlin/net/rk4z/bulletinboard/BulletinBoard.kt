package net.rk4z.bulletinboard

import net.rk4z.bulletinboard.libs.Metrics
import net.rk4z.bulletinboard.listener.BBListener
import net.rk4z.bulletinboard.listener.ProxyBridger
import net.rk4z.bulletinboard.manager.CommandManager
import net.rk4z.bulletinboard.manager.LanguageManager
import net.rk4z.bulletinboard.utils.*
import net.rk4z.igf.IGF
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.*
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.io.path.notExists

typealias TaskRunner = (JavaPlugin, Runnable) -> Unit


@Suppress("unused", "DEPRECATION")
class BulletinBoard : JavaPlugin() {
    companion object {
        lateinit var instance: BulletinBoard
            private set
        lateinit var dataBase: DataBase
            private set
        lateinit var metrics: Metrics
            private set

        const val ID = "bulletinboard"
        const val MODRINTH_API_URL = "https://api.modrinth.com/v2/project/AfO6aot1/version"
        const val MODRINTH_DOWNLOAD_URL = "https://modrinth.com/plugin/AfO6aot1/versions/"

        val runTask: TaskRunner = { plugin, task -> Bukkit.getScheduler().runTask(plugin, task) }
        val runTaskAsync: TaskRunner = { plugin, task -> Bukkit.getScheduler().runTaskAsynchronously(plugin, task) }

        val log: Logger = LoggerFactory.getLogger(BulletinBoard::class.java.simpleName)
    }

    val version = description.version
    val authors: MutableList<String> = description.authors
    val pluginDes = description.description
    val isDebug: Boolean = true

    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var enableMetrics: Boolean? = true
    private var isProxied: Boolean? = false
    private var systemLang: String = Locale.getDefault().language
    private val configFile: Path = dataFolder.resolve("config.yml").toPath()
    private val langDir = dataFolder.resolve("lang")
    private val yaml = Yaml()
    private val availableLang = listOf("ja", "en")

    override fun onLoad() {
        instance = getPlugin(BulletinBoard::class.java)
        systemLang = Locale.getDefault().language
        initializeDirectories()
        if (!isDebug) {
            updateLanguageFilesIfNeeded()
        }
        loadLanguageFiles()
        log.info(LanguageManager.getSysMessage(System.Log.LOADING, name, version))
        dataBase = DataBase(this)
        checkUpdate()
        loadConfig()

        if (dataBase.connectToDatabase()) {
            dataBase.createRequiredTables()
            if (!dataBase.isDataMigrated()) {
                val jsonFile = File(dataFolder, "data.json")
                if (jsonFile.exists()) {
                    logger.info("Old data file is found. Trying to import data from JSON....")
                    dataBase.importDataFromJson(jsonFile)
                }
            }
        }
    }

    override fun onEnable() {
        log.info(LanguageManager.getSysMessage(System.Log.ENABLING, name, version))

        if (isProxied.isNullOrFalse()) {
            registerCommand(this)
            //TODO: Optimisation.
            server.messenger.registerIncomingPluginChannel(this, "$ID:main", ProxyBridger())
            server.messenger.registerOutgoingPluginChannel(this, "$ID:main")
        }

        if (enableMetrics.isNullOrFalse()) {
            metrics = Metrics(this, 23481)
        }

        IGF.init(this)
        IGF.setGlobalListener(BBListener())
        server.pluginManager.registerEvents(BBListener(), this)

        availableLang.forEach {
            LanguageManager.findMissingKeys(it)
        }
    }

    override fun onDisable() {
        log.info(LanguageManager.getSysMessage(System.Log.DISABLING, name, version))
        dataBase.closeConnection()
    }

    fun reload(player: Player) {
        log.info("Reloading language files...")
        loadLanguageFiles()
        availableLang.forEach {
            LanguageManager.findMissingKeys(it)
        }
        log.info("Language files reloaded successfully.")
        player.sendMessage("Language files reloaded.")

        log.info("Reloading configuration file...")
        loadConfig()
        log.info("Configuration file reloaded successfully.")
        player.sendMessage("Configuration file reloaded.")

        log.info("Reloading database connection...")
        dataBase.closeConnection()
        if (dataBase.connectToDatabase()) {
            dataBase.createRequiredTables()
            log.info("Database connection reloaded successfully.")
            player.sendMessage("Database connection reloaded.")
        } else {
            log.warn("Failed to reload database connection.")
            player.sendMessage("Failed to reload database connection.")
        }
    }

    private fun initializeDirectories() {
        if (dataFolder.toPath().notExists()) {
            dataFolder.mkdirs()
        }
        if (configFile.notExists()) {
            saveResource("config.yml", false)
        }
        if (!langDir.exists()) {
            langDir.mkdirs()
        }
        availableLang.forEach {
            val langFile = langDir.resolve("$it.yml")
            if (langFile.notExists()) {
                saveResource("lang/$it.yml", false)
            }
        }
    }

    private fun updateLanguageFilesIfNeeded() {
        availableLang.forEach { lang ->
            val langFile = File(langDir, "$lang.yml")
            val langResource = "lang/$lang.yml"

            getResource(langResource)?.use { resourceStream ->
                val resourceBytes = resourceStream.readBytes()

                val jarLangVersion = readLangVersion(resourceBytes.inputStream())
                val installedLangVersion = if (langFile.exists()) {
                    Files.newInputStream(langFile.toPath()).use { inputStream ->
                        readLangVersion(inputStream)
                    }
                } else {
                    "0"
                }

                if (isVersionNewer(jarLangVersion, installedLangVersion)) {
                    log.info("Replacing old $lang language file (version: $installedLangVersion) with newer version: $jarLangVersion")
                    resourceBytes.inputStream().use { byteArrayStream ->
                        Files.copy(
                            byteArrayStream,
                            langFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING
                        )
                    }
                }
            } ?: log.warn("Resource file '$langResource' not found in the Jar.")
        }
    }

    private fun loadLanguageFiles() {
        availableLang.forEach { lang ->
            val langFile = langDir.resolve("$lang.yml")
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

    private fun readLangVersion(stream: InputStream): String {
        return InputStreamReader(stream, StandardCharsets.UTF_8).use { reader ->
            val langData: Map<String, Any> = yaml.load(reader)
            langData["langVersion"]?.toString() ?: "0"
        }
    }

    private fun registerCommand(plugin: JavaPlugin) {
        val commandMapField = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
        commandMapField.isAccessible = true
        val commandMap = commandMapField.get(Bukkit.getServer()) as CommandMap

        val command = object : Command("bulletinboard") {
            override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
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
            log.info(LanguageManager.getSysMessage(System.Log.CHECKING_UPDATE))
            try {
                val url = URI(MODRINTH_API_URL).toURL()
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val (latestVersion, versionCount, newerVersionCount) = extractVersionInfo(response)

                    log.info(LanguageManager.getSysMessage(System.Log.ALL_VERSION_COUNT, versionCount))
                    if (isVersionNewer(latestVersion, description.version)) {
                        log.info(LanguageManager.getSysMessage(System.Log.NEW_VERSION_COUNT, newerVersionCount))
                        log.info(LanguageManager.getSysMessage(System.Log.LATEST_VERSION_FOUND, latestVersion, version))
                        val downloadUrl = "$MODRINTH_DOWNLOAD_URL/$latestVersion"
                        log.info(LanguageManager.getSysMessage(System.Log.VIEW_LATEST_VER, downloadUrl))
                    } else {
                        log.info(LanguageManager.getSysMessage(System.Log.YOU_ARE_USING_LATEST))
                    }
                } else {
                    log.warn(LanguageManager.getSysMessage(System.Log.FAILED_TO_CHECK_UPDATE, connection.responseCode))
                }
            } catch (e: Exception) {
                log.warn(LanguageManager.getSysMessage(System.Log.ERROR_WHILE_CHECKING_UPDATE, e.message ?: "Unknown error"))
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
            enableMetrics = config.getNullableBoolean("enableMetrics") ?: true
            isProxied = config.getNullableBoolean("isProxied") ?: false
        }
    }
}