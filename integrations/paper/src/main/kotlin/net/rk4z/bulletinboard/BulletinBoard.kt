package net.rk4z.bulletinboard

import net.rk4z.bulletinboard.utils.getNullableBoolean
import net.rk4z.bulletinboard.utils.getNullableString
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import org.json.JSONArray
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale
import kotlin.io.path.notExists

typealias TaskRunner = (JavaPlugin, Runnable) -> Unit
typealias TaskRunnerWithDelay = (JavaPlugin, Runnable, Long) -> Unit
typealias TaskRunnerWithPeriod = (JavaPlugin, Runnable, Long, Long) -> Unit

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

    var isProxied: Boolean? = false

    val name = description.name
    val version = description.version
    val log: Logger = LoggerFactory.getLogger(BulletinBoard::class.java.simpleName)
    val configFile = dataFolder.resolve("config.yml").toPath()
    private lateinit var messages: Map<String, String>
    private val langDir: Path = dataFolder.toPath().resolve("lang")
    val yaml = Yaml()

    override fun onLoad() {
        log.info("Loading $name v$version")
        instance = getPlugin(BulletinBoard::class.java)
        key = NamespacedKey(this, ID)

        dataBase = DataBase(this)

        checkUpdate()

        if (dataFolder.toPath().notExists()) {
            dataFolder.mkdirs()
        }

        if (configFile.notExists()) {
            saveResource("config.yml", false)
        }

        loadConfig()

        if (dataBase.connectToDatabase()) {
            dataBase.createRequiredTables()
        }
    }

    override fun onEnable() {
        log.info("Enabling $name v$version")

        server.pluginManager.apply {

        }
    }

    override fun onDisable() {
        dataBase.closeConnection()
    }

    fun getSystemMessage(key: String): String {
        return messages[key] ?: "Missing message for key: $key"
    }

    private fun loadLanguage(locale: Locale) {
        val langFileName = "${locale.language}_${locale.country}.yml"
        val langFile = langDir.resolve(langFileName)

        if (Files.notExists(langFile)) {
            log.warn("Language file not found: $langFileName, loading default en_US.yml")
            messages = loadYamlFile(langDir.resolve("en_US.yml"))
        } else {
            messages = loadYamlFile(langFile)
        }
    }

    private fun loadYamlFile(file: Path): Map<String, String> {
        Files.newInputStream(file).use { inputStream ->
            return yaml.load(inputStream)
        }
    }

    private fun checkUpdate() {
        runTaskAsync(this) {
            log.info("Checking for updates...")
            try {
                val url = URI(MODRINTH_API_URL).toURL()
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()

                    val (latestVersion, versionCount, newerVersionCount) = extractVersionInfo(response)

                    log.info("There are $versionCount versions available.")

                    if (isVersionNewer(latestVersion, description.version)) {
                        log.info("There are $newerVersionCount newer versions available than your current version.")
                        log.info("A new version ($latestVersion) is available! You are using version ${description.version}.")

                        val downloadUrl = "https://modrinth.com/plugin/AfO6aot1/version/$latestVersion"
                        log.info("View the latest version here: $downloadUrl")
                    } else {
                        log.info("You are using the latest version of BulletinBoard.")
                    }
                } else {
                    log.warn("Failed to check for updates: ${connection.responseCode}")
                }
            } catch (e: Exception) {
                log.warn("Error while checking for updates: ${e.message}")
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