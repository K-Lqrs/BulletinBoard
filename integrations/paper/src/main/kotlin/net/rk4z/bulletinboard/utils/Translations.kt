@file:Suppress("ClassName")

package net.rk4z.bulletinboard.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

sealed interface MessageKey {
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
            object MY_POSTS : Title()
            object ALL_POSTS : Title()
            object DELETED_POSTS : Title()

        }

        open class Button : Gui() {
            object NEW_POST : Button()
            object ALL_POSTS : Button()
            object MY_POSTS : Button()
            object DELETED_POSTS : Button()
            object ABOUT_PLUGIN : Button()
            object SETTINGS : Button()
            object HELP : Button()

            object SAVE_POST : Button()
            object CANCEL_POST : Button()
        }

        open class Other : Gui() {
            object NO_TITLE : Other()
            object NO_CONTENT : Other()
        }
    }

    open class Command : Main() {
        open class Message : Command() {
            object PLAYER_ONLY : Message()
            object UNKNOWN_COMMAND : Message()
        }

        open class Help : Command() {
            object USAGE_HEADER : Help()
            object USAGE_OPENBOARD : Help()
            object USAGE_NEWPOST : Help()
            object USAGE_MYPOSTS : Help()
            object USAGE_POSTS : Help()
            object USAGE_SETTINGS : Help()
            object USAGE_DELETED_POSTS : Help()
            object USAGE_PREVIEWCLOSE : Help()
        }
    }
}