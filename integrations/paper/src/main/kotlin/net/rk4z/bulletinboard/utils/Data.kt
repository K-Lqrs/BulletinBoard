@file:Suppress("ClassName", "unused")

package net.rk4z.bulletinboard.utils

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import java.util.Date
import java.util.UUID

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

