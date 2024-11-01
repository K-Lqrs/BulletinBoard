package net.rk4z.bulletinboard.utils

import net.rk4z.s1.pluginBase.MessageKey

@Suppress("unused")
enum class TitleType(val key: MessageKey) {
    ALL_POSTS(Main.Gui.Title.ALL_POSTS),
    MY_POSTS(Main.Gui.Title.MY_POSTS),
    DELETED_POSTS(Main.Gui.Title.DELETED_POSTS),
    DELETE_POST_SELECTION(Main.Gui.Title.DELETE_POST_SELECTION),
    DELETE_POST_ALL_PLAYER_SELECTION(Main.Gui.Title.DELETE_POST_ALL_PLAYER_SELECTION),
    DELETE_POST_PERMANENTLY_SELECTION(Main.Gui.Title.DELETE_POST_PERMANENTLY_SELECTION),
    EDIT_POST_SELECTION(Main.Gui.Title.EDIT_POST_SELECTION),
    RESTORE_POST_SELECTION(Main.Gui.Title.RESTORE_POST_SELECTION)
}