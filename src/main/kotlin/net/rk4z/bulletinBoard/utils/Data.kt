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

    var inputType: String? = null,
    var editInputType: String? = null,
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
    val key: String,
    val customId: String
)