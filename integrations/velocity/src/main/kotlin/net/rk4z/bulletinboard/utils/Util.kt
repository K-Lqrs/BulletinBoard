@file:Suppress("unused")

package net.rk4z.bulletinboard.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.io.File

fun Map<String, Any>.getNullableString(key: String): String? =
    this[key]?.toString()?.takeIf { it.isNotBlank() }

fun Map<String, Any>.getNullableBoolean(key: String): Boolean? =
    this[key]?.toString()?.takeIf { it.isNotBlank() }?.toBooleanStrictOrNull()

fun Boolean?.isNullOrFalse(): Boolean {
    return this == null || this == false
}
fun File.notExists(): Boolean {
    return !this.exists()
}

//private val playerState = ConcurrentHashMap<UUID, PlayerState>()

fun Component.getContent(): String {
    return PlainTextComponentSerializer.plainText().serialize(this)
}
