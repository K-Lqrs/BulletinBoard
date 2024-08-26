package net.rk4z.bulletinBoard.utils

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.managers.BBCommandManager
import net.rk4z.bulletinBoard.managers.BulletinBoardManager
import net.rk4z.bulletinBoard.utils.BBUtil.playSoundMaster
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*

// This method provides a way to clear all state of a player.
private fun clearPlayerState(state: PlayerState) {
    state.draft = null
    state.editDraft = null
    state.selectedDeletingPostId = null
    state.selectedEditingPostId = null
    state.isInputting = null
    state.isEditInputting = null
    state.isPreviewing = null
    state.isOpeningConfirmation = null
    state.isChoosingConfirmationAnswer = null
    state.inputType = null
    state.editInputType = null
    state.confirmationType = null
    state.preview = null
}

data class PlayerState(
    var draft: PostDraft? = null,
    var editDraft: EditPostData? = null,

    var selectedDeletingPostId: String? = null,
    var selectedEditingPostId: String? = null,
    var selectedRestoringPostId: String? = null,

    var isInputting: Boolean? = null,
    var isEditInputting: Boolean? = null,
    var isPreviewing: Boolean? = null,
    var isOpeningConfirmation: Boolean? = null,
    var isChoosingConfirmationAnswer: Boolean? = null,
    var isAnonymous: Boolean? = null,

    var inputType: InputType? = null,
    var editInputType: InputType? = null,
    var confirmationType: ConfirmationType? = null,

    var preview: Pair<Component, Component>? = null,
) {
    fun clear() {
        clearPlayerState(this)
    }
}

data class PostDraft(
    val title: Component? = null,
    val content: Component? = null,
    val isAnonymous: Boolean? = null
)

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
    val isAnonymous: Boolean = false,
    @Contextual
    val date: Date
)

data class EditPostData(
    val id: ShortUUID? = null,
    val title: Component? = null,
    val content: Component? = null,
    val isAnonymous: Boolean = false // Add the isAnonymous field
) {
    fun toPost(author: UUID, date: Date): Post {
        return Post(
            id = this.id ?: ShortUUID.randomUUID(),
            title = this.title ?: Component.text(""),
            author = author,
            content = this.content ?: Component.text(""),
            isAnonymous = this.isAnonymous,
            date = date
        )
    }
}


@Serializable
data class BulletinBoardData(
    val posts: List<Post>,
    // It'd need to handle old JSON
    val players: List<PlayerData> = emptyList(),
    val permissions: List<Permission> = emptyList(),
    val deletedPosts: List<Post> = emptyList()
)

// It'd also need to handle old JSON
@Serializable
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

//>----------------------------------------------<\\

data class Button(
    val slot: Int,
    val item: Material,
    val key: MessageKey,
    val customId: CustomID
)

enum class CustomID {
    NEW_POST,
    ALL_POSTS,
    MY_POSTS,
    DELETED_POSTS,
    ABOUT_PLUGIN,
    SETTINGS,
    HELP,
    NO_POSTS,
    POST_TITLE,
    POST_CONTENT,
    CANCEL_POST,
    SAVE_POST,
    EDIT_POST_TITLE,
    EDIT_POST_CONTENT,
    CANCEL_EDIT,
    SAVE_EDIT,
    CONFIRM_SAVE_POST,
    CONFIRM_CANCEL_POST,
    PREVIEW_POST,
    CANCEL_CONFIRM_SAVE_POST,
    CONTINUE_POST,
    PREV_PAGE,
    NEXT_PAGE,
    BACK_BUTTON,
    EDIT_POST,
    DELETE_POST,
    RESTORE_POST,
    DELETE_POST_PERMANENTLY,
    CONFIRM_DELETE_POST,
    CANCEL_DELETE_POST,
    CANCEL_DELETE_POST_PERMANENTLY,
    CONFIRM_DELETE_POST_PERMANENTLY,
    CANCEL_RESTORE_POST,
    DELETE_POST_OTHERS,
    CONFIRM_RESTORE_POST,
    ANONYMOUS;

    companion object {
        val dynamicIds = mutableSetOf<String>()

        fun getAllEnumNames(): Set<String> = entries.map { it.name }.toSet()
    }
}

enum class InputType {
    TITLE,
    CONTENT
}

enum class ConfirmationType {
    SAVE_POST,
    CANCEL_POST,
    DELETING_POST,
    DELETING_POST_PERMANENTLY,
    RESTORING_POST,
}

enum class MessageKey {
    MAIN_BOARD,
    POST_EDITOR,
    POST_EDITOR_FOR_EDIT,
    ALL_POSTS,
    MY_POSTS,
    NEW_POST,
    DELETED_POSTS,
    ABOUT_PLUGIN,
    SETTINGS,
    HELP,
    SAVE_POST,
    CANCEL_POST,
    PLEASE_ENTER_TITLE,
    PLEASE_ENTER_CONTENT,
    INPUT_SET,
    NO_TITLE,
    NO_CONTENT,
    NO_POSTS,
    HTU_HEADER,
    HTU_OPENBOARD,
    HTU_NEWPOST,
    HTU_MYPOSTS,
    HTU_POSTS,
    HTU_PREVIEW,
    HTU_PREVIEW_CLOSE,
    USAGE_HEADER,
    USAGE_OPENBOARD,
    USAGE_NEWPOST,
    USAGE_MYPOSTS,
    USAGE_POSTS,
    USAGE_SETTINGS,
    USAGE_DELETED_POSTS,
    USAGE_PREVIEWCLOSE,
    SAVE_POST_CONFIRMATION,
    CANCEL_POST_CONFIRMATION,
    DELETE_POST_CONFIRMATION,
    CONFIRM_SAVE_POST,
    CANCEL_CONFIRM_SAVE_POST,
    CANCELLED_POST,
    PREVIEW_POST,
    CONTINUE_POST,
    CONFIRM_CANCEL_POST,
    WHEN_POST_DRAFT_NULL,
    WHEN_DELETE_POST_NULL,
    POST_SAVED,
    PREV_PAGE,
    NEXT_PAGE,
    BACK_BUTTON,
    EDIT_POST,
    DELETE_POST,
    TITLE_LABEL,
    CONTENT_LABEL,
    DATE_LABEL,
    AUTHOR_LABEL,
    RESTORE_POST,
    DELETE_POST_PERMANENTLY,
    DELETE_POST_PERMANENTLY_SELECTION,
    DELETE_POST_SELECTION,
    CANCEL_DELETE_POST,
    CONFIRM_DELETE_POST,
    POST_DELETED,
    DELETE_POST_PERMANENTLY_CONFIRMATION,
    CANCEL_DELETE_POST_PERMANENTLY,
    CONFIRM_DELETE_POST_PERMANENTLY,
    EDIT_POST_SELECTION,
    POST_DELETED_PERMANENTLY,
    RESTORE_POST_SELECTION,
    POST_RESTORED,
    CANCEL_RESTORE_POST,
    CONFIRM_RESTORE_POST,
    RESTORE_POST_CONFIRMATION,
    DELETE_POST_OTHERS,
    ANONYMOUS,
    ANONYMOUS_USER,
}

enum class TitleType(val key: MessageKey) {
    ALL_POSTS(MessageKey.ALL_POSTS),
    MY_POSTS(MessageKey.MY_POSTS),
    DELETED_POSTS(MessageKey.DELETED_POSTS),
    DELETE_POST_SELECTION(MessageKey.DELETE_POST_SELECTION),
    DELETE_POST_PERMANENTLY_SELECTION(MessageKey.DELETE_POST_PERMANENTLY_SELECTION),
    EDIT_POST_SELECTION(MessageKey.EDIT_POST_SELECTION),
    RESTORE_POST_SELECTION(MessageKey.RESTORE_POST_SELECTION)
}

enum class Commands(val execute: (Player) -> Unit) {
    OPENBOARD({ player ->
        BulletinBoardManager.openMainBoard(player)
        player.playSoundMaster(Sound.BLOCK_ANVIL_PLACE, 0.2f, 2.0f)
    }),
    NEWPOST({ player -> BulletinBoardManager.openPostEditor(player) }),
    MYPOSTS({ player -> BulletinBoardManager.openMyPosts(player) }),
    POSTS({ player -> BulletinBoardManager.openAllPosts(player) }),
    DELETEDPOSTS({ player -> BulletinBoardManager.openDeletedPosts(player) }),
    HELP({ player -> BBCommandManager().displayHelp(player) }),
    ABOUT({ player -> BBCommandManager().displayAbout(player)}),
    HOWTOUSE({ player -> BBCommandManager().displayHowToUse(player) });

    companion object {
        fun fromString(name: String): Commands? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}

enum class Settings {
    // 0 -> Normal (Just Close Inventory),
    // 1 -> Cancel Close,
    // 2 -> Show Confirmation GUI
    INVENTORY_CLOSE_ACTION_TYPE
}