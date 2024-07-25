package net.rk4z.bulletinBoard.util

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.BulletinBoard
import java.io.File
import java.util.UUID

data class PlayerState(
    var inputType: String? = null,
    var draft: PostDraft? = null,
    var confirmationType: String? = null,
    var preview: Pair<Component, Component>? = null,
    var isPreviewing: Boolean = false,
    var isOpeningConfirmation: Boolean = false,
    var isInputting: Boolean = false,
    var editInputType: String? = null,
    var editDraft: EditPostData? = null,
    var isEditInputting: Boolean = false
)

data class EditPostData(
    val id: String? = null,
    @Serializable(with = ComponentSerializer::class)
    val title: Component? = null,
    @Serializable(with = ComponentSerializer::class)
    val content: Component? = null
) {
    fun toPost(author: UUID, date: String): Post {
        return Post(
            id = this.id ?: UUID.randomUUID().toString(),
            title = this.title ?: Component.text(""),
            author = author,
            content = this.content ?: Component.text(""),
            date = date
        )
    }
}

data class PostDraft(
    val title: Component? = null,
    val content: Component? = null
)

@Serializable
data class Post(
    var id: String,
    @Serializable(with = ComponentSerializer::class)
    var title: Component,
    @Contextual
    var author: UUID,
    @Serializable(with = ComponentSerializer::class)
    var content: Component,
    var date: String
)

@Serializable
data class PlayerData(
    @Contextual
    val uuid: UUID,
    val posts: List<String>
)

@Serializable
data class BulletinBoardData(
    val players: List<PlayerData>,
    val posts: List<Post>
)

object JsonUtil {
    private val json = Json {
        prettyPrint = true
        serializersModule = SerializersModule {
            contextual(UUID::class, UUIDSerializer)
            contextual(Component::class, ComponentSerializer)
        }
    }

    private val logger = BulletinBoard.instance.logger

    fun loadFromFile(file: File): BulletinBoardData {
        return if (file.exists() && file.readText().isNotEmpty()) {
            try {
                json.decodeFromString(BulletinBoardData.serializer(), file.readText())
            } catch (e: Exception) {
                logger.info("Error parsing JSON: ${e.message}")
                BulletinBoardData(players = emptyList(), posts = emptyList())
            }
        } else {
            logger.info("Warning: JSON file is empty or does not exist.")
            BulletinBoardData(players = emptyList(), posts = emptyList())
        }
    }

    fun saveToFile(data: BulletinBoardData, file: File) {
        file.writeText(json.encodeToString(BulletinBoardData.serializer(), data))
    }
}