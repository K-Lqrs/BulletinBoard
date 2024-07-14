package net.rk4z.bulletinBoard.listeners

import net.rk4z.beacon.EventBus
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.ServerPlayerJoinEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        BulletinBoard.instance.logger.info("Player Joined")
        EventBus.postAsync(ServerPlayerJoinEvent.get(player))
    }
}