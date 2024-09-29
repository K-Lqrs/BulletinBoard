package net.rk4z.bulletinboard.utils

enum class CustomID {
    NEW_POST,
    ALL_POSTS,
    MY_POSTS,
    DELETED_POSTS,
    ABOUT_PLUGIN,
    SETTINGS,
    HELP,

    EDIT_POST,
    DELETE_POST,
    RESTORE_POST,
    DELETE_POST_PERMANENTLY,
    DELETE_POST_OTHERS,

    NO_POSTS,

    POST_TITLE,
    POST_CONTENT,
    CANCEL_POST,
    SAVE_POST,
    EDIT_POST_TITLE,
    EDIT_POST_CONTENT,
    CANCEL_EDIT,
    SAVE_EDIT,

    PREV_PAGE,
    NEXT_PAGE,

    BACK_BUTTON,
    BACK_GROUND,
    ;

    companion object {
        fun fromString(name: String): CustomID? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}