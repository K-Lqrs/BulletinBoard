@file:Suppress("unused", "DEPRECATION")

package net.rk4z.bulletinBoard.manager

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object LanguageManager {
    private val messages = mapOf(
        "ja" to mapOf(
            //region GUI
            "main_board" to "メインボード",
            //endregion
        ),
        "en" to mapOf(
            //region GUI
            "main_board" to "Main Board",
            //endregion
        )
    )

    private fun getLanguage(player: Player): String {
        return player.locale.substring(0, 2)
    }

    // getMessage() returns a Component object
    fun getMessage(player: Player, key: String): Component {
        val languageCode = getLanguage(player)
        val message = messages[languageCode]?.get(key) ?: messages["en"]?.get(key) ?: key
        return Component.text(message)
    }

    // But getContentFromMessage() returns a String object
    fun getContentFromMessage(player: Player, key: String): String {
        val languageCode = getLanguage(player)
        val message = messages[languageCode]?.get(key) ?: messages["en"]?.get(key) ?: key
        return message
    }
}
