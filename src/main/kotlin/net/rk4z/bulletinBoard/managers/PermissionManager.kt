package net.rk4z.bulletinBoard.managers

import net.rk4z.bulletinBoard.BulletinBoard
import org.bukkit.entity.Player

object PermissionManager {
    fun hasPermission(player: Player, permission: String): Boolean {
        val dataBase = BulletinBoard.database
        val hadPermission = dataBase.getPlayerPermission(player.uniqueId)
        return hadPermission.contains(permission)
    }
}