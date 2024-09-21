@file:Suppress("ClassName", "unused")

package net.rk4z.bulletinboard.utils

import kotlin.reflect.full.memberProperties

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
        object MAIN_BOARD : MessageKey
        object POST_EDITOR : MessageKey
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