@file:Suppress("ClassName", "unused")

package net.rk4z.bulletinboard.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player
import kotlin.reflect.full.memberProperties

//region Translation System
/**
 * This is a key interface for the translation of the Plugin.
 */
sealed interface MessageKey {
    companion object {
        fun fromString(className: String, enumName: String): MessageKey? {
            return when (className) {
                "System.Log" -> System.fromString(enumName)
                "Main.GUI" -> Main.fromString(enumName)
                else -> null
            }
        }
    }

    fun toComponent(): Component {
        return Component.text(this.javaClass.simpleName)
    }

    fun toTextComponent(): TextComponent {
        return Component.text(this.javaClass.simpleName)
    }
}

/**
 * This provides a translation of this Plugin, relevant to the console
 */
object System : MessageKey {
    //region Key List
    object Log : MessageKey {
        object LOADING : MessageKey
        object ENABLING : MessageKey
        object DISABLING : MessageKey

        object CHECKING_UPDATE : MessageKey
        object ALL_VERSION_COUNT : MessageKey
        object NEW_VERSION_COUNT : MessageKey
        object VIEW_LATEST_VER : MessageKey
        object LATEST_VERSION_FOUND : MessageKey
        object YOU_ARE_USING_LATEST : MessageKey
        object FAILED_TO_CHECK_UPDATE : MessageKey
        object ERROR_WHILE_CHECKING_UPDATE : MessageKey
        object LANGUAGE_FILE_NOT_FOUND : MessageKey

        object Other : MessageKey {
            object UNKNOWN : MessageKey
            object UNKNOWN_ERROR : MessageKey
            object ERROR : MessageKey
        }
    }
    //end region

    // Cache for the name to key mapping
    private val nameToKeyCache = LRUCache<String, MessageKey?>(75)

    fun fromString(enumName: String): MessageKey? {
        return nameToKeyCache.getOrPut(enumName.uppercase()) {
            System::class.memberProperties
                .firstOrNull { it.name.uppercase() == enumName.uppercase() }
                ?.get(System) as? MessageKey
        }
    }
}

/**
 * This includes translations of this Plugin, involving internal functions.
 */
object Main : MessageKey {
    //region Key List
    object Gui : MessageKey {
        object Title : MessageKey {
            object MAIN_BOARD : MessageKey
            object POST_EDITOR : MessageKey
        }

        object Button : MessageKey {
            object NEW_POST : MessageKey
            object ALL_POSTS : MessageKey
            object MY_POSTS : MessageKey
            object DELETED_POSTS : MessageKey
            object ABOUT_PLUGIN : MessageKey
            object SETTINGS : MessageKey
            object HELP : MessageKey
        }
    }
    //end region

    // I think Main has more keys than System, so We increased the cache size.
    private val nameToKeyCache = LRUCache<String, MessageKey?>(175)

    fun fromString(enumName: String): MessageKey? {
        return nameToKeyCache.getOrPut(enumName.uppercase()) {
            Main::class.memberProperties
                .firstOrNull { it.name.uppercase() == enumName.uppercase() }
                ?.get(Main) as? MessageKey
        }
    }
}
//endregion

typealias CommandExecute = (Player) -> Unit

enum class Commands(val execute: CommandExecute) {
    ;

    companion object {
        fun fromString(name: String): Commands? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}

enum class CustomID {
    //region Main.GUI.Button
    NEW_POST,
    ALL_POSTS,
    MY_POSTS,
    DELETED_POSTS,
    ABOUT_PLUGIN,
    SETTINGS,
    HELP,
}