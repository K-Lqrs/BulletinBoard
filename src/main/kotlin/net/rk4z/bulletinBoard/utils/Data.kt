package net.rk4z.bulletinBoard.utils

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.Material
import java.util.*

data class PlayerState(
    var draft: PostDraft? = null,
    var editDraft: EditPostData? = null,

    var isInputting: Boolean = false,
    var isOpeningConfirmation: Boolean = false,

    var inputType: InputType? = null,
    var editInputType: InputType? = null,
    var confirmationType: ConfirmationType? = null,

    var preview: Pair<Component, Component>? = null,
)

data class PostDraft(
    val title: Component? = null,
    val content: Component? = null
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
    @Contextual
    val date: Date
)

data class EditPostData(
    val id: ShortUUID? = null,
    val title: Component? = null,
    val content: Component? = null
) {
    fun toPost(author: UUID, date: Date): Post {
        return Post(
            id = this.id ?: ShortUUID.randomUUID(),
            title = this.title ?: Component.text(""),
            author = author,
            content = this.content ?: Component.text(""),
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
    CONTINUE_POST
}

enum class InputType {
    TITLE,
    CONTENT
}

enum class ConfirmationType {
    SAVE_POST,
    CANCEL_POST,
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
    CONFIRM_SAVE_POST,
    CANCEL_CONFIRM_SAVE_POST,
    PREVIEW_POST,
    CONTINUE_POST,
    CONFIRM_CANCEL_POST,
    WHEN_POST_DRAFT_NULL,
    POST_SAVED,
}