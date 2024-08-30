package net.rk4z.bulletinboard.manager

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.utils.MessageKey
import org.bukkit.entity.Player

object LanguageManager {
    private val messages: MutableMap<String, MutableMap<MessageKey, String>> = mutableMapOf(
    )

    // getMessage() returns a Component object
    private fun getLanguage(player: Player): String {
        return player.locale().language
        //player.locale.substring(0, 2)
    }

    fun getMessage(player: Player, key: MessageKey): Component {
        val language = getLanguage(player)
        val message = messages[language]?.get(key) ?: messages["en"]?.get(key) ?: key.name
        return Component.text(message)
    }

    // getContentFromMessage() returns a String object
    fun getContentFromMessage(player: Player, key: MessageKey): String {
        val language = getLanguage(player)
        val message = messages[language]?.get(key) ?: messages["en"]?.get(key) ?: key.name
        return message
    }
}