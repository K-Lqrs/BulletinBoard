package net.rk4z.bulletinBoard.util

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.kyori.adventure.text.Component
import java.io.File
import java.util.UUID

data class PostDraft(
    val title: Component? = null,
    val content: Component? = null
)

@Serializable
data class Post(
    val id: String,
    @Serializable(with = ComponentSerializer::class)
    val title: Component,
    @Contextual
    val author: UUID,
    @Serializable(with = ComponentSerializer::class)
    val content: Component,
    val date: String
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

    fun loadFromFile(file: File): BulletinBoardData {
        return if (file.exists() && file.readText().isNotEmpty()) {
            try {
                json.decodeFromString(BulletinBoardData.serializer(), file.readText())
            } catch (e: Exception) {
                println("Error parsing JSON: ${e.message}")
                BulletinBoardData(players = emptyList(), posts = emptyList())
            }
        } else {
            println("Warning: JSON file is empty or does not exist.")
            BulletinBoardData(players = emptyList(), posts = emptyList())
        }
    }

    fun saveToFile(data: BulletinBoardData, file: File) {
        file.writeText(json.encodeToString(BulletinBoardData.serializer(), data))
    }
}