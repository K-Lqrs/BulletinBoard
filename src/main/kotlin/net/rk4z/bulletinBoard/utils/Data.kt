package net.rk4z.bulletinBoard.utils

import net.kyori.adventure.text.Component
import java.io.Serializable
import java.sql.Date
import java.util.*

data class PlayerState(
    var draft: PostDraft? = null
)

data class PostDraft(
    val title: Component? = null,
    val content: Component? = null
)

data class Post(
    val id: ShortUUID,
    val author: UUID,
    val title: Component,
    val content: Component,
    val date: Date
)

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
): Serializable {
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