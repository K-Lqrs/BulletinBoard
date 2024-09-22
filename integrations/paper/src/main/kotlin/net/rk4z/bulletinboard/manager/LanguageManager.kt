package net.rk4z.bulletinboard.manager

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.utils.MessageKey
import org.bukkit.entity.Player
import java.util.Locale

@Suppress("unused", "UNCHECKED_CAST")
object LanguageManager {
    private val messages: MutableMap<String, MutableMap<MessageKey, String>> = mutableMapOf()

    fun loadLanguage(yamlData: Map<String, Any>, lang: String) {
        processMap(yamlData, lang, "")
    }

    private fun processMap(map: Map<String, Any>, lang: String, path: String) {
        map.forEach { (key, value) ->
            val newPath = if (path.isEmpty()) key else "$path.$key"

            when (value) {
                is Map<*, *> -> processMap(value as Map<String, Any>, lang, newPath)
                is String -> {
                    val messageKey = mapKey(newPath)
                    messageKey?.let {
                        messages.computeIfAbsent(lang) { mutableMapOf() }[messageKey] = value
                    }
                }
            }
        }
    }

    private fun mapKey(path: String): MessageKey? {
        val parts = path.split(".")

        if (parts.size < 2) return null

        val className = parts.dropLast(1).joinToString(".") {
            it.replaceFirstChar { char -> char.titlecase(Locale.getDefault()) }
        }
        val enumName = parts.last().uppercase()

        return MessageKey.fromString(className, enumName)
    }

    private fun Player.getLanguage(): String {
        return this.locale().language ?: "en"
        //player.locale.substring(0, 2)
    }

    fun getMessage(player: Player, key: MessageKey, vararg args: Any): Component {
        val lang = player.getLanguage()
        val message = messages[lang]?.get(key)

        val st = message?.let { String.format(it, *args) } ?: return key.toComponent()

        return Component.text(st)
    }

    fun getMessageFromContent(player: Player, key: MessageKey, vararg args: Any): String {
        val lang = player.getLanguage()
        val message = messages[lang]?.get(key)

        val st = message?.let { String.format(it, *args) } ?: return key.toTextComponent().content()

        return Component.text(st).content()
    }

    fun getSysMessage(lang: String, key: MessageKey, vararg args: Any): String {
        val message = messages[lang]?.get(key)

        val st = message?.let { String.format(it, *args) } ?: return key.toTextComponent().content()

        return st
    }

}
