package net.rk4z.bulletinboard

import com.google.gson.JsonObject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import net.rk4z.bulletinboard.manager.BBCommandManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import javax.inject.Inject

@Plugin(
    id = "bulletinboard",
    name = "BulletinBoard",
    version = "1.0.0",
    description = "A simple bulletin board plugin",
    authors = ["Ruxy"]
)
class BulletinBoard @Inject constructor(
    private val server: ProxyServer,
    private val logger: Logger = LoggerFactory.getLogger(BulletinBoard::class.java.name),
    @DataDirectory private val dataFolder: Path
) {
    companion object {
        lateinit var instance: BulletinBoard
            private set
        val channel: MinecraftChannelIdentifier = MinecraftChannelIdentifier.create("bulletinboard", "main")
    }

    @Subscribe
    fun onProxyInitialize(event: ProxyInitializeEvent) {
        instance = this
        server.commandManager.register("bb", BBCommandManager())
    }

    fun sendJsonMessage(player: Player, event: String, data: JsonObject) {
        val message = JsonObject()
        message.addProperty("event", event)
        message.add("data", data)

        val messageBytes = message.toString().toByteArray(StandardCharsets.UTF_8)

        val serverOptional = player.currentServer
        if (serverOptional.isPresent) {
            val server = serverOptional.get().server
            val channel = MinecraftChannelIdentifier.create("bulletinboard", "main")
            server.sendPluginMessage(channel, messageBytes)
        } else {
            logger.error("Player ${player.username} is not connected to a server")
        }
    }
}