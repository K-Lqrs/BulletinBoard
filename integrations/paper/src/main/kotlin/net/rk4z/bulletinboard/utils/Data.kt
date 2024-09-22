@file:Suppress("ClassName", "unused")

package net.rk4z.bulletinboard.utils

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player
import java.util.Date
import java.util.UUID

sealed interface MessageKey {
    companion object {
    }

    fun toComponent(): Component {
        return Component.text(this.javaClass.simpleName)
    }

    fun toTextComponent(): TextComponent {
        return Component.text(this.javaClass.simpleName)
    }
}

open class System : MessageKey {
    open class Log : System() {
        object LOADING : Log()
        object ENABLING : Log()
        object DISABLING : Log()

        object CHECKING_UPDATE : Log()
        object ALL_VERSION_COUNT : Log()
        object NEW_VERSION_COUNT : Log()
        object VIEW_LATEST_VER : Log()
        object LATEST_VERSION_FOUND : Log()
        object YOU_ARE_USING_LATEST : Log()
        object FAILED_TO_CHECK_UPDATE : Log()
        object ERROR_WHILE_CHECKING_UPDATE : Log()

        open class Other : Log() {
            object UNKNOWN : Other()
            object UNKNOWN_ERROR : Other()
            object ERROR : Other()
        }
    }
}


//TODO: Add more keys and write translations to the language files
open class Main : MessageKey {
    open class Gui : Main() {
        open class Title : Gui() {
            object MAIN_BOARD : Title()
            object POST_EDITOR : Title()
        }

        open class Button : Gui() {
            object NEW_POST : Button()
            object ALL_POSTS : Button()
            object MY_POSTS : Button()
            object DELETED_POSTS : Button()
            object ABOUT_PLUGIN : Button()
            object SETTINGS : Button()
            object HELP : Button()
        }
    }
}


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
    NEW_POST,
    ALL_POSTS,
    MY_POSTS,
    DELETED_POSTS,
    ABOUT_PLUGIN,
    SETTINGS,
    HELP,
}

@Serializable
data class Post(
    @Contextual
    val id: ShortUUID,
    @Contextual
    val author: UUID,
    @Serializable(with = ComponentSerializer::class)
    val title: Component,
    @Serializable(with = ComponentSerializer::class)
    val content: Component,
    val isAnonymous: Boolean?,
    @Contextual
    val date: Date?
)

@Serializable
data class BulletinBoardData(
    val posts: List<Post>,
    @Deprecated("Only used to process old data files")
    val players: List<PlayerData> = emptyList(),
    val permissions: List<Permission> = emptyList(),
    val deletedPosts: List<Post> = emptyList()
)

@Serializable
@Deprecated("Only used to process old data files")
data class PlayerData(
    @Contextual
    val uuid: UUID,
    val posts: List<String>
)

@Serializable
data class Permission(
    @Contextual
    val uuid: UUID,
    val acquiredPermission: List<String>
)

