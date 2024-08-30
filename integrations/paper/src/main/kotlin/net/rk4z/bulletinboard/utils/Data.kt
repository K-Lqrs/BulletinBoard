package net.rk4z.bulletinboard.utils

import net.rk4z.bulletinboard.manager.BulletinBoardManager
import net.rk4z.bulletinboard.utils.BBUtil.playSoundMaster
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player

enum class Commands(val execute: (Player) -> Unit) {
    OPENBOARD({ player ->
        player.playSoundMaster(Sound.BLOCK_ANVIL_PLACE, 0.7f, 2.0f)
        BulletinBoardManager.openMainBoard(player)
    });


    companion object {
        fun fromString(name: String): Commands? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}

enum class MessageKey {
    //region GUI
    MAIN_BOARD,
    //endregion

    //region MainBoard
    NEW_POST,
    ALL_POSTS,
    MY_POSTS,
    DELETED_POSTS,
    ABOUT_PLUGIN,
    SETTINGS,
    HELP,
    //endregion

    //region Usage
    USAGE_HEADER,
    USAGE_OPENBOARD,
    USAGE_NEWPOST,
    USAGE_MYPOSTS,
    USAGE_POSTS,
    USAGE_SETTINGS,
    USAGE_DELETED_POSTS,
    USAGE_PREVIEWCLOSE,
    //endregion
}

enum class CustomID {
    //region MainBoard
    NEW_POST,
    ALL_POSTS,
    MY_POSTS,
    DELETED_POSTS,
    ABOUT_PLUGIN,
    SETTINGS,
    HELP,
    //endregion
}

data class Button(
    val slot: Int,
    val item: Material,
    val key: MessageKey,
    val customId: CustomID
)