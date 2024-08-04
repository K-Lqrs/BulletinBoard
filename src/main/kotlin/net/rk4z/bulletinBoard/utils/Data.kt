package net.rk4z.bulletinBoard.utils

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import java.util.*

data class PlayerState(
    var editDraft: EditPostData? = null,
    var draft: PostDraft? = null,
)

@Serializable
data class Post(
    @Contextual
    var id: ShortUUID,
    @Serializable(with = ComponentSerializer::class)
    var title: Component,
    @Contextual
    var author: UUID,
    @Serializable(with = ComponentSerializer::class)
    var content: Component,
    var date: String
)

@Serializable
data class Comment(
    @Contextual
    var id: ShortUUID,
    @Contextual
    var postId: ShortUUID,
    @Contextual
    var author: UUID,
    @Serializable(with = ComponentSerializer::class)
    var content: Component,
    var date: String,
    val comments: List<@Contextual ShortUUID> = emptyList()
)

data class PostDraft(
    val title: Component? = null,
    val content: Component? = null
)

data class EditPostData(
    val id: ShortUUID? = null,
    @Serializable(with = ComponentSerializer::class)
    val title: Component? = null,
    @Serializable(with = ComponentSerializer::class)
    val content: Component? = null
) {
    fun toPost(author: UUID, date: String): Post {
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
data class Permission(
    @Contextual
    var uuid: UUID,
    var acquiredPermission: List<String>
)

@Serializable
data class BulletinBoardData(
    var posts: List<@Contextual Post>,
    var deleted: List<@Contextual Post>,
    var permission: List<@Contextual Permission>
)

typealias Sable = java.io.Serializable

/**
 * A generic quadruple.
 * @param A the first value type
 * @param B the second value type
 * @param C the third value type
 * @param D the fourth value type
 *
 * @see Triple
 * I want to use four values in a data class, but Kotlin
 * only has Triple for three values. So I made this.
 */
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