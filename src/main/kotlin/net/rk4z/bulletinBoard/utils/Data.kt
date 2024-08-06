package net.rk4z.bulletinBoard.utils

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
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

typealias Sable = java.io.Serializable

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
): Sable {
    override fun toString(): String = "($first, $second, $third, $fourth)"
    override fun hashCode(): Int {
        var result = first?.hashCode() ?: 0
        result = 31 * result + (second?.hashCode() ?: 0)
        result = 31 * result + (third?.hashCode() ?: 0)
        result = 31 * result + (fourth?.hashCode() ?: 0)
        return result
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other::class != this::class) return false

        other as Quadruple<*, *, *, *>

        if (first != other.first) return false
        if (second != other.second) return false
        if (third != other.third) return false
        if (fourth != other.fourth) return false

        return true
    }
}