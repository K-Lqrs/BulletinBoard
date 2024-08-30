package net.rk4z.bulletinboard

import net.rk4z.bulletinboard.manager.BBCommandManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

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

        val runTaskAsynchronous: TaskRunner = { plugin, runnable ->
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
        }
    }

    var isProxied: Boolean = false

    private val paperGlobal = dataFolder.resolve("paper-global.yml")
    private var config: FileConfiguration = YamlConfiguration.loadConfiguration(paperGlobal)
    private val bungeeCordEnabled = config.getBoolean("proxies.bungee-cord.online-mode", false)
    private val velocityEnabled = config.getBoolean("proxies.velocity.enabled", false)

    override fun onLoad() {
        if (bungeeCordEnabled || velocityEnabled) {
            isProxied = true
        }
        instance = this
    }

    override fun onEnable() {
        if (!isProxied) {
            val bbCommand = getCommand("bb")
            bbCommand?.setAliases(listOf("bulletinboard", "bborard"))
            bbCommand?.setExecutor(BBCommandManager())
        }
    }
}