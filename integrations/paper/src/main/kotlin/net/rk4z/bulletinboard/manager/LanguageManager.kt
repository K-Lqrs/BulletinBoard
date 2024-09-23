package net.rk4z.bulletinboard.manager

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.utils.Main
import net.rk4z.bulletinboard.utils.MessageKey
import net.rk4z.bulletinboard.utils.System
import org.bukkit.entity.Player
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@Suppress("unused", "UNCHECKED_CAST")
object LanguageManager {
    val messages: MutableMap<String, MutableMap<MessageKey, String>> = mutableMapOf()

    val topLevelObjects: Map<String, Any> = mapOf(
        "system" to System(),
        "main" to Main()
    )

    fun mapMessageKeys(clazz: KClass<out MessageKey>, currentPath: String = "", messageKeyMap: MutableMap<String, MessageKey>) {
        val className = clazz.simpleName?.lowercase() ?: return

        val fullPath = if (currentPath.isEmpty()) className else "$currentPath.$className"

        val objectInstance = clazz.objectInstance as? MessageKey
        if (objectInstance != null) {
            messageKeyMap[fullPath] = objectInstance
            if (BulletinBoard.instance.isDebug) BulletinBoard.instance.logger.info("Mapped class: $fullPath -> ${clazz.simpleName}")
        } else {
            if (BulletinBoard.instance.isDebug) BulletinBoard.instance.logger.warning("Object instance not found for class: ${clazz.simpleName}")
        }

        clazz.nestedClasses.forEach { nestedClass ->
            if (nestedClass.isSubclassOf(MessageKey::class)) {
                mapMessageKeys(nestedClass as KClass<out MessageKey>, fullPath, messageKeyMap)
            }
        }
    }

    fun processYamlAndMapMessageKeys(data: Map<String, Any>, messageMap: MutableMap<MessageKey, String>) {
        val messageKeyMap: MutableMap<String, MessageKey> = mutableMapOf()

        mapMessageKeys(System::class, "", messageKeyMap)
        mapMessageKeys(Main::class, "", messageKeyMap)

        processYamlData("", data, messageKeyMap, messageMap)
    }

    fun processYamlData(prefix: String, data: Map<String, Any>, messageKeyMap: Map<String, MessageKey>, messageMap: MutableMap<MessageKey, String>) {
        for ((key, value) in data) {
            val currentPrefix = if (prefix.isEmpty()) key else "$prefix.$key"
            if (BulletinBoard.instance.isDebug) BulletinBoard.instance.logger.info("Processing YAML path: $currentPrefix")

            when (value) {
                is String -> {
                    val messageKey = messageKeyMap[currentPrefix]
                    if (messageKey != null) {
                        messageMap[messageKey] = value
                        if (BulletinBoard.instance.isDebug) BulletinBoard.instance.logger.info("Mapped: $messageKey -> $value")
                    } else {
                        if (BulletinBoard.instance.isDebug) BulletinBoard.instance.logger.warning("MessageKey not found for path: $currentPrefix")
                    }
                }
                is Map<*, *> -> {
                    processYamlData(currentPrefix, value as Map<String, Any>, messageKeyMap, messageMap)
                }
            }
        }
    }


    private fun Player.getLanguage(): String {
        return this.locale().language ?: "en"
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
