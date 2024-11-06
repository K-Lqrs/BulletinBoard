package net.rk4z.bulletinboard

import net.rk4z.bulletinboard.listener.BBListener
import net.rk4z.bulletinboard.listener.ProxyBridger
import net.rk4z.bulletinboard.manager.CommandManager
import net.rk4z.bulletinboard.utils.System
import net.rk4z.igf.IGF
import net.rk4z.s1.pluginBase.LanguageManager
import net.rk4z.s1.pluginBase.Logger
import net.rk4z.s1.pluginBase.PluginEntry
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

@Suppress("unused", "DEPRECATION")
class BulletinBoard : PluginEntry(
    "bulletinboard",
    "net.rk4z.bulletinboard",
    false,
    true,
    23481,
    true,
    "AfO6aot1",
    listOf("ja", "en"),
    true,
    true,
    true
) {
    companion object {
        lateinit var dataBase: DataBase
            private set

        fun get(): BulletinBoard {
            return get<BulletinBoard>()
        }
    }

    val version = description.version
    val authors: MutableList<String> = description.authors
    val pluginDes = description.description
    var fileConfigVer: Double? = null
    val configVer: Double = 1.0

    private var isProxied: Boolean? = false

    override fun onLoadPre() {
        dataBase = DataBase(this)
    }

    override fun onLoadPost() {
        LanguageManager.getSysMessage(System.Log.LOADING, name, version)

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

    override fun onEnablePre() {
        Logger.info(LanguageManager.getSysMessage(System.Log.ENABLING, name, version))
    }

    override fun onEnablePost() {
        registerCommand(this)

        server.messenger.registerIncomingPluginChannel(this, "$id:main", ProxyBridger())
        server.messenger.registerOutgoingPluginChannel(this, "$id:main")

        IGF.init(this)
        IGF.setGlobalListener(BBListener())
        server.pluginManager.registerEvents(BBListener(), this)

        availableLang?.let { al ->
            al.forEach {
                LanguageManager.findMissingKeys(it)
            }
        }
    }

    override fun onDisablePre() {
        Logger.info(LanguageManager.getSysMessage(System.Log.DISABLING, name, version))
    }

    override fun onDisablePost() {
        dataBase.closeConnection()
    }

    override fun onCheckUpdate() {
        Logger.info(LanguageManager.getSysMessage(System.Log.CHECKING_UPDATE))
    }

    override fun onAllVersionsRetrieved(versionCount: Int) {
        Logger.info(LanguageManager.getSysMessage(System.Log.ALL_VERSION_COUNT, versionCount.toString()))
    }

    override fun onNewVersionFound(latestVersion: String, newerVersionCount: Int) {
        Logger.info(LanguageManager.getSysMessage(System.Log.NEW_VERSION_COUNT, newerVersionCount.toString()))
        Logger.info(LanguageManager.getSysMessage(System.Log.LATEST_VERSION_FOUND, latestVersion, version))
        Logger.info(LanguageManager.getSysMessage(System.Log.VIEW_LATEST_VER, MODRINTH_DOWNLOAD_URL))
    }

    override fun onNoNewVersionFound() {
        Logger.info(LanguageManager.getSysMessage(System.Log.YOU_ARE_USING_LATEST))
    }

    override fun onUpdateCheckFailed(responseCode: Int) {
        Logger.warn(LanguageManager.getSysMessage(System.Log.FAILED_TO_CHECK_UPDATE, responseCode.toString()))
    }

    override fun onUpdateCheckError(e: Exception) {
        Logger.error(LanguageManager.getSysMessage(System.Log.ERROR_WHILE_CHECKING_UPDATE, e.message ?: LanguageManager.getSysMessage(System.Log.Other.UNKNOWN_ERROR)))
    }

    fun reload(player: Player) {
        Logger.info("Reloading language files...")
        loadLanguageFiles()
        this.availableLang?.let { al ->
            al.forEach {
                LanguageManager.findMissingKeys(it)
            }
        }
        Logger.info("Language files reloaded successfully.")
        player.sendMessage("Language files reloaded.")

        Logger.info("Reloading configuration file...")
        loadConfig()
        Logger.info("Configuration file reloaded successfully.")
        player.sendMessage("Configuration file reloaded.")

        Logger.info("Reloading database connection...")
        dataBase.closeConnection()
        if (dataBase.connectToDatabase()) {
            dataBase.createRequiredTables()
            Logger.info("Database connection reloaded successfully.")
            player.sendMessage("Database connection reloaded.")
        } else {
            Logger.warn("Failed to reload database connection.")
            player.sendMessage("Failed to reload database connection.")
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

            override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): List<String?> {
                return CommandManager.onTabComplete(sender, this, alias, args)
            }
        }

        command.aliases = listOf("bb")
        command.description = "BulletinBoard Main Command"
        commandMap.register(plugin.description.name, command)
    }

    private fun loadConfig() {
        enableMetrics = lc<Boolean>("enableMetrics") ?: false
        isProxied = lc<Boolean>("isProxied")
        fileConfigVer = lc<Double>("configVersion")
    }
}