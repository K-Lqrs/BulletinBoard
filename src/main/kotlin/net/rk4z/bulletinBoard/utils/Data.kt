package net.rk4z.bulletinBoard.utils

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import java.util.*

data class PlayerState(
    var isInputting: Boolean = false
)

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
)