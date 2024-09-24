package net.rk4z.bulletinboard.listener

import net.rk4z.bulletinboard.manager.BulletinBoardManager
import net.rk4z.bulletinboard.utils.CustomID
import net.rk4z.bulletinboard.utils.CustomID.*
import org.bukkit.entity.Player

object BBAction {
    fun handleMainBoardClick(player: Player, customId: CustomID?) {
        when (customId) {
            NEW_POST -> {
                BulletinBoardManager.openPostEditor(player)
            }

            ALL_POSTS -> TODO()
            MY_POSTS -> TODO()
            DELETED_POSTS -> TODO()
            ABOUT_PLUGIN -> TODO()
            SETTINGS -> TODO()
            HELP -> TODO()

            else -> {}
        }
    }
}