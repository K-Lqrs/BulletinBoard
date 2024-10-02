package net.rk4z.bulletinboard.utils

/**
 * Enum class for different types of confirmation dialogs
 */
@Suppress("unused")
enum class ConfirmationType {
    SAVE_POST,
    CANCEL_POST,
    SAVE_EDIT,
    CANCEL_EDIT,
    DELETING_POST,
    DELETING_POST_PERMANENTLY,
    RESTORING_POST,
    DELETE_POST_FROM_ALL,
}
