package net.rk4z.bulletinBoard


import net.rk4z.beacon.Event
import net.rk4z.bulletinBoard.util.Post
import org.bukkit.entity.Player

class ServerPlayerJoinEvent(
    val player: Player
) : Event() {
    companion object {
        fun get(player: Player) : ServerPlayerJoinEvent {
            return ServerPlayerJoinEvent(player)
        }
    }
}

class BulletinPostEvent(
    val info: Post
) : Event() {
    companion object {
        fun get(info: Post) {
            BulletinPostEvent(info)
        }
    }
}