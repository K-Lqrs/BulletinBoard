@file:Suppress("ClassName", "unused")

package net.rk4z.bulletinboard.utils

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import java.util.Date
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

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
    ;

    companion object {
        fun fromString(name: String): CustomID? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}

//>--------------------------------------------------------------------------------------------------<\\

data class EditPostData(
    val id: ShortUUID? = null,
    val title: Component? = null,
    val content: Component? = null,
    val isAnonymous: Boolean? = null
) {
    fun toPost(author: UUID, date: Date): Post {
        return Post(
            id = this.id ?: ShortUUID.randomUUID(),
            title = this.title ?: Component.text(""),
            author = author,
            content = this.content ?: Component.text(""),
            isAnonymous = this.isAnonymous ?: false,
            date = date
        )
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

fun Player.getPlayerState(): PlayerState {
    return playerState.computeIfAbsent(this.uniqueId) { PlayerState() }
}

private val playerState = ConcurrentHashMap<UUID, PlayerState>()

// This method provides a way to clear all state of a player.
private fun PlayerState.clear() {
    this.draft = null
    this.editDraft = null
    this.selectedDeletingPostId = null
    this.selectedEditingPostId = null
    this.isInputting = null
    this.isEditInputting = null
    this.isPreviewing = null
    this.isOpeningConfirmation = null
    this.isChoosingConfirmationAnswer = null
    this.inputType = null
    this.editInputType = null
    this.confirmationType = null
    this.preview = null
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
    var inputType: InputType? = null,
    var editInputType: InputType? = null,
    var confirmationType: ConfirmationType? = null,
    var preview: Pair<Component, Component>? = null,
) {
    fun clearDraft() {
        draft = null
    }

    fun clearEditDraft() {
        editDraft = null
    }

    fun clearSelection() {
        selectedDeletingPostId = null
        selectedEditingPostId = null
        selectedRestoringPostId = null
    }

    fun clearInputFlags() {
        isInputting = null
        isEditInputting = null
        isPreviewing = null
    }

    fun clearConfirmation() {
        isOpeningConfirmation = null
        isChoosingConfirmationAnswer = null
        confirmationType = null
        preview = null
    }

    fun sendDebugMessage(player: Player) {
        val stateMessage = Component.text()
            .append(Component.text("Player State:", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.newline())
            .append(Component.text("Draft: ", NamedTextColor.GRAY))
            .append(Component.text(draft?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Edit Draft: ", NamedTextColor.GRAY))
            .append(Component.text(editDraft?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Selected Deleting Post ID: ", NamedTextColor.GRAY))
            .append(Component.text(selectedDeletingPostId ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Selected Editing Post ID: ", NamedTextColor.GRAY))
            .append(Component.text(selectedEditingPostId ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Selected Restoring Post ID: ", NamedTextColor.GRAY))
            .append(Component.text(selectedRestoringPostId ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Is Inputting: ", NamedTextColor.GRAY))
            .append(Component.text(isInputting?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Is Edit Inputting: ", NamedTextColor.GRAY))
            .append(Component.text(isEditInputting?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Is Previewing: ", NamedTextColor.GRAY))
            .append(Component.text(isPreviewing?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Is Opening Confirmation: ", NamedTextColor.GRAY))
            .append(Component.text(isOpeningConfirmation?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Is Choosing Confirmation Answer: ", NamedTextColor.GRAY))
            .append(Component.text(isChoosingConfirmationAnswer?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("Input Type: ", NamedTextColor.GRAY))
            .append(Component.text(inputType?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Edit Input Type: ", NamedTextColor.GRAY))
            .append(Component.text(editInputType?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Confirmation Type: ", NamedTextColor.GRAY))
            .append(Component.text(confirmationType?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Preview: ", NamedTextColor.GRAY))
            .append(preview?.let {
                Component.text(it.first.toString() + " | " + it.second.toString(), NamedTextColor.WHITE)
            } ?: Component.text("None", NamedTextColor.WHITE))

        player.sendMessage(stateMessage)
    }
}

enum class InputType {
    TITLE,
    CONTENT,
    EDIT_TITLE,
    EDIT_CONTENT,
}

enum class ConfirmationType {
    SAVE_POST,
    CANCEL_POST,
    DELETING_POST,
    DELETING_POST_PERMANENTLY,
    RESTORING_POST,
}
