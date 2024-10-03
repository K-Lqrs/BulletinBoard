@file:Suppress("ClassName")

package net.rk4z.bulletinboard.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.rk4z.bulletinboard.manager.LanguageManager
import org.bukkit.entity.Player

sealed interface MessageKey {
    fun toComponent(): Component {
        return Component.text(this.javaClass.simpleName)
    }

    fun toTextComponent(): TextComponent {
        return Component.text(this.javaClass.simpleName)
    }

    fun translate(player: Player): Component {
        return LanguageManager.getMessage(player, this)
    }

    fun translateText(player: Player): String {
        return LanguageManager.getMessageFromContent(player, this)
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
            object POST_EDITOR_FOR_EDIT : Title()
            object MY_POSTS : Title()
            object ALL_POSTS : Title()
            object DELETED_POSTS : Title()

            object DELETE_POST_SELECTION : Title()
            object DELETE_POST_PERMANENTLY_SELECTION : Title()
            object EDIT_POST_SELECTION : Title()
            object RESTORE_POST_SELECTION : Title()
            object DELETE_POST_ALL_PLAYER_SELECTION : Title()

            object SAVE_POST_CONFIRMATION : Title()
            object CANCEL_POST_CONFIRMATION : Title()
            object DELETE_POST_CONFIRMATION : Title()
            object DELETE_POST_PERMANENTLY_CONFIRMATION : Title()
            object RESTORE_POST_CONFIRMATION : Title()
            object DELETE_POST_FROM_ALL_CONFIRMATION : Title()
            object SAVE_EDIT_CONFIRMATION : Title()
            object CANCEL_EDIT_CONFIRMATION : Title()
        }

        open class Button : Gui() {
            object NEW_POST : Button()
            object ALL_POSTS : Button()
            object MY_POSTS : Button()
            object DELETED_POSTS : Button()
            object ABOUT_PLUGIN : Button()
            object SETTINGS : Button()
            object HELP : Button()

            object EDIT_POST : Button()
            object DELETE_POST : Button()
            object DELETE_POST_FROM_ALL : Button()
            object RESTORE_POST : Button()
            object DELETE_POST_PERMANENTLY : Button()
            object DELETE_POST_OTHERS : Button()

            object SAVE_POST : Button()
            object CANCEL_POST : Button()

            object PREV_PAGE : Button()
            object NEXT_PAGE : Button()

            object BACK_BUTTON : Button()

            object CANCEL_CONFIRM_SAVE_POST : Button()
            object PREVIEW_POST : Button()
            object CONFIRM_SAVE_POST : Button()
            object CONTINUE_POST : Button()
            object CONFIRM_CANCEL_POST : Button()
            object CANCEL_DELETE_POST : Button()
            object CANCEL_DELETE_POST_FROM_ALL : Button()
            object CONFIRM_DELETE_POST : Button()
            object CONFIRM_DELETE_POST_FROM_ALL : Button()
            object CANCEL_DELETE_POST_PERMANENTLY : Button()
            object CONFIRM_DELETE_POST_PERMANENTLY : Button()
            object CANCEL_RESTORE_POST : Button()
            object CONFIRM_RESTORE_POST : Button()
        }

        open class Other : Gui() {
            object ANONYMOUS : Other()
            object NO_POSTS : Other()
            object NO_TITLE : Other()
            object NO_CONTENT : Other()
            object UNKNOWN_PLAYER : Other()
        }
    }

    open class Message : Main() {
        object AUTHOR_LABEL : Message()
        object TITLE_LABEL : Message()
        object CONTENT_LABEL : Message()
        object DATE_LABEL : Message()

        object ENTER_TITLE : Message()
        object ENTER_CONTENT : Message()

        object ENTER_TITLE_EDIT : Message()
        object ENTER_CONTENT_EDIT : Message()

        object WHEN_POST_DRAFT_NULL : Message()
        object POST_SAVED : Message()
        object POST_CANCELLED : Message()
        object POST_NOT_FOUND : Message()
        object POST_DELETED : Message()
    }

    open class Command : Main() {
        open class Message : Command() {
            object PLAYER_ONLY : Message()
            object UNKNOWN_COMMAND : Message()
        }

        open class Help : Command() {
            object HELP_HEADER : Help()
            object HELP_OPENBOARD : Help()
            object HELP_NEWPOST : Help()
            object HELP_MYPOSTS : Help()
            object HELP_POSTS : Help()
            object HELP_SETTINGS : Help()
            object HELP_DELETED_POSTS : Help()
            object HELP_PREVIEWCLOSE : Help()
        }
    }
}