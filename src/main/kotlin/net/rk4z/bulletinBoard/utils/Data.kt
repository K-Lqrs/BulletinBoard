package net.rk4z.bulletinBoard.utils

import net.kyori.adventure.text.Component
import java.sql.Date
import java.util.UUID

data class Post(
    val id: ShortUUID,
    val author: UUID,
    val title: Component,
    val content: Component,
    val date: Date
)