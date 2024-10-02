@file:Suppress("unused")

package net.rk4z.bulletinboard.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.BulletinBoard.Companion.runTask
import net.rk4z.bulletinboard.manager.LanguageManager
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

fun Map<String, Any>.getNullableString(key: String): String? =
    this[key]?.toString()?.takeIf { it.isNotBlank() }

fun Map<String, Any>.getNullableBoolean(key: String): Boolean? =
    this[key]?.toString()?.takeIf { it.isNotBlank() }?.toBooleanStrictOrNull()

fun Boolean?.isNullOrFalse(): Boolean {
    return this == null || this == false
}

fun Player.playSoundMaster(sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f) {
    this.playSound(this.location, sound, volume, pitch)
}

fun File.notExists(): Boolean {
    return !this.exists()
}

private val playerState = ConcurrentHashMap<UUID, PlayerState>()

fun Player.getPlayerState(): PlayerState {
    return playerState.computeIfAbsent(this.uniqueId) { PlayerState() }
}

fun Component.getContent(): String {
    return PlainTextComponentSerializer.plainText().serialize(this)
}

fun getPlayerTimeZone(player: Player): TimeZone {
    val country = player.locale().country.uppercase()
    val timeZoneId = countryTimeZones[country] ?: "UTC"
    return TimeZone.getTimeZone(timeZoneId)
}

private val countryTimeZones = mapOf(
    "US" to "America/New_York", // アメリカ
    "GB" to "Europe/London",    // イギリス
    "JP" to "Asia/Tokyo",       // 日本
    "AU" to "Australia/Sydney", // オーストラリア
    "DE" to "Europe/Berlin",    // ドイツ
    "FR" to "Europe/Paris",     // フランス
    "CA" to "America/Toronto",  // カナダ
    "CN" to "Asia/Shanghai",    // 中国
    "IN" to "Asia/Kolkata",     // インド
    "BR" to "America/Sao_Paulo",// ブラジル
    "ZA" to "Africa/Johannesburg", // 南アフリカ
    "NZ" to "Pacific/Auckland"  // ニュージーランド
)

fun displayPost(player: Player, post: Post) {
    val playerTimeZone = getPlayerTimeZone(player)
    // Date in Result can never be null
    val zonedDateTime = ZonedDateTime.ofInstant(post.date!!.toInstant(), playerTimeZone.toZoneId())
    val authorName =
            // First try. get from online player
           Bukkit.getPlayer(post.author)?.name
           // Second try. get from offline player
        ?: Bukkit.getOfflinePlayer(post.author).name
           // If a player is not found, just display "Unknown Player"
        ?: LanguageManager.getMessage(player, Main.Gui.Other.UNKNOWN_PLAYER)

    val authorComponent = if (!post.isAnonymous!!) {
        LanguageManager.getMessage(player, Main.Message.AUTHOR_LABEL, authorName)
    } else {
        LanguageManager.getMessage(player, Main.Gui.Other.ANONYMOUS)
    }

    val plainTitle = PlainTextComponentSerializer.plainText().serialize(post.title)
    val plainContent = PlainTextComponentSerializer.plainText().serialize(post.content)

    val titleComponent = LanguageManager.getMessage(player, Main.Message.TITLE_LABEL, plainTitle)

    val contentComponent = LanguageManager.getMessage(player, Main.Message.CONTENT_LABEL, plainContent)

    val dateComponent = LanguageManager.getMessage(
        player,
        Main.Message.DATE_LABEL,
        zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")).toString()
    )

    runTask(BulletinBoard.instance) {
        player.closeInventory()

        val message = Component.text("---------------------------------", NamedTextColor.DARK_GRAY) // 上の線
            .append(Component.newline())
            .append(titleComponent.color(NamedTextColor.WHITE)) // タイトルをデフォルトの白に設定（任意で他の色に変更可能）
            .append(Component.newline())
            .append(contentComponent.color(NamedTextColor.WHITE)) // 内容もデフォルトの白に設定
            .append(Component.newline())
            .append(authorComponent.color(NamedTextColor.WHITE)) // 作者情報の色も設定
            .append(Component.newline())
            .append(dateComponent.color(NamedTextColor.WHITE)) // 日付情報もデフォルトの色に設定
            .append(Component.newline())
            .append(Component.text("---------------------------------", NamedTextColor.DARK_GRAY)) // 下の線


        player.sendMessage(message)
    }
}
