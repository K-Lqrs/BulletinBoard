package net.rk4z.bulletinBoard.utils

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import java.util.*

@Serializable
data class Post(
    @Contextual
    var id: UUID,
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
    var posts: List<Post>
)

@Serializable
data class Permissions(
    val useClearCommand: List<@Contextual UUID>,
    val usePermissionCommand: List<@Contextual UUID>
)

@Serializable
data class Settings(
    val clearCommandMode: Int,
    val permissionCommandMode: Int
)

