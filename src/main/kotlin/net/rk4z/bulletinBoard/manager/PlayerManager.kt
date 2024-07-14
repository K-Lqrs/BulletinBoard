package net.rk4z.bulletinBoard.manager

import net.rk4z.beacon.EventHandler
import net.rk4z.beacon.IEventHandler
import net.rk4z.beacon.handler
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.ServerPlayerJoinEvent
import net.rk4z.bulletinBoard.util.JsonUtil
import net.rk4z.bulletinBoard.util.PlayerData

@Suppress("unused")
@EventHandler
class PlayerManager : IEventHandler {

    val onPlayerJoin = handler<ServerPlayerJoinEvent> { event ->
        val player = event.player
        val uuid = player.uniqueId

        BulletinBoard.instance.logger.info("Handling ServerPlayerJoinEvent for player: ${player.name}")

        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)

        try {
            if (data.players.none { it.uuid == uuid }) {
                val newPlayerData = PlayerData(uuid = uuid, posts = emptyList())
                val updatedPlayers = data.players + newPlayerData
                val updatedData = data.copy(players = updatedPlayers)
                JsonUtil.saveToFile(updatedData, BulletinBoard.instance.dataFile)
                BulletinBoard.instance.logger.info("New player data added for player: ${player.name}")
            } else {
                BulletinBoard.instance.logger.info("Player data already exists for player: ${player.name}")
            }
        } catch (e: Exception) {
            BulletinBoard.instance.logger.error("Error handling ServerPlayerJoinEvent for player: ${player.name}", e)
        }
    }
}
