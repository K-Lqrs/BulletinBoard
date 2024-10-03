package net.rk4z.bulletinboard.utils

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import java.util.*

interface Draft {
    var title: Component?
    var content: Component?
    var isAnonymous: Boolean?
}

data class PostDraft(
    override var title: Component? = null,
    override var content: Component? = null,
    override var isAnonymous: Boolean? = null
) : Draft

data class EditPostData(
    val id: ShortUUID? = null,
    override var title: Component? = null,
    override var content: Component? = null,
    override var isAnonymous: Boolean? = null
) : Draft {
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