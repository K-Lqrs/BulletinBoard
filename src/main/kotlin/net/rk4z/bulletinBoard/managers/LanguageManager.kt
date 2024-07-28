@file:Suppress("unused", "DEPRECATION")

package net.rk4z.bulletinBoard.managers

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.Locale

object LanguageManager {
    private val messages = mapOf(
        "ja" to mapOf(
            //region GUI
            "mainBoard" to "メインボード",
            //endregion

            //region Button
            "newPost" to "新規投稿",
            //endregion
        ),
        "en" to mapOf(
            //region GUI
            "mainBoard" to "Main Board",
            //endregion

            //region Button
            "newPost" to "New Post",
            //endregion
        )
    )

    private fun getLanguage(player: Player): String {
        return player.locale.substring(0, 2)
    }

    // getMessage() returns a Component object
    fun getMessage(player: Player, key: String): Component {
        val language = getLanguage(player)
        val message = messages[language]?.get(key) ?: messages["en"]?.get(key) ?: key
        return Component.text(message)
    }

    // getContentFromMessage() returns a String object
    fun getContentFromMessage(player: Player, key: String): String {
        val language = getLanguage(player)
        val message = messages[language]?.get(key) ?: messages["en"]?.get(key) ?: key
        return message
    }
}
