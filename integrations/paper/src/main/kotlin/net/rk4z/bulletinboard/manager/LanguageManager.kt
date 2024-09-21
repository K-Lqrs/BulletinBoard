package net.rk4z.bulletinboard.manager

import net.rk4z.bulletinboard.utils.MessageKey
import org.bukkit.entity.Player
import java.util.Locale

@Suppress("unused")
object LanguageManager {
    private val messages: MutableMap<String, MutableMap<MessageKey, String>> = mutableMapOf()

    fun loadLanguage(yamlData: Map<String, Any>, lang: String) {
        processMap(yamlData, lang, "")
    }

    private fun processMap(map: Map<String, Any>, lang: String, path: String) {
        map.forEach { (key, value) ->
            val newPath = if (path.isEmpty()) key else "$path.$key"

            if (value is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                processMap(value as Map<String, Any>, lang, newPath)
            } else if (value is String) {
                val messageKey = mapKey(newPath)
                if (messageKey != null) {
                    if (!messages.containsKey(lang)) {
                        messages[lang] = mutableMapOf()
                    }
                    messages[lang]?.put(messageKey, value)
                }
            }
        }
    }

    private fun mapKey(path: String): MessageKey? {
        val parts = path.split(".")

        val className = parts.dropLast(1).joinToString(".") {
            it.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase(Locale.getDefault())
                else char.toString()
            }
        }

        val enumName = parts.last().uppercase()

        return when (className) {
            "System.Log" -> MessageKey.System.Log.valueOf(enumName).let { MessageKey.System() }
            "Main.GUI" -> MessageKey.Main.GUI.valueOf(enumName).let { MessageKey.Main() }
            else -> null
        }
    }

    fun getMessage(player: Player, key: MessageKey) {

    }

    fun getSysMessage(loc: String, key: MessageKey) {

    }
}
