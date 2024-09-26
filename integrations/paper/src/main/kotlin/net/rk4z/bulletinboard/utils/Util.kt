package net.rk4z.bulletinboard.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.manager.LanguageManager
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
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

data class Button(
    val slot: Int,
    val material: Material,
    val name: Component,
    val customId: String
)

fun Material.toItemStack(
    name: Component? = null,
    customId: String? = null
): ItemStack {
    val itemStack = ItemStack(this)
    val meta: ItemMeta? = itemStack.itemMeta

    if (name != null) {
        meta?.displayName(name)
    }
    if (customId != null) {
        meta?.persistentDataContainer?.set(BulletinBoard.key, PersistentDataType.STRING, customId)
    }
    if (meta != null) {
        itemStack.itemMeta = meta
    }

    return itemStack
}

fun Player.playSoundMaster(sound: Sound, volume: Float = 1.0f, pitch: Float = 1.0f) {
    this.playSound(this.location, sound, volume, pitch)
}

private val playerState = ConcurrentHashMap<UUID, PlayerState>()

fun Player.getPlayerState(): PlayerState {
    return playerState.computeIfAbsent(this.uniqueId) { PlayerState() }
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

    val authorComponent = {
        if (!post.isAnonymous!!) {
            LanguageManager.getMessage(player, Main.Gui.Message.AUTHOR_LABEL, post.author)
        } else {
            LanguageManager.getMessage(player, Main.Gui.Other.ANONYMOUS)
        }
    }

    val titleComponent = LanguageManager.getMessage(player, Main.Gui.Message.TITLE_LABEL, post.title)

    val contentComponent = LanguageManager.getMessage(player, Main.Gui.Message.CONTENT_LABEL, post.content)

    val dateComponent = LanguageManager.getMessage(
        player,
        Main.Gui.Message.DATE_LABEL,
        zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")).toString()
    )

    BulletinBoard.instance.runTask(BulletinBoard.instance) {
        player.closeInventory()

        val message = Component.text("---------------------------------", NamedTextColor.DARK_GRAY)
            .append(Component.newline())
            .append(titleComponent)
            .append(Component.newline())
            .append(contentComponent)
            .append(Component.newline())
            .append(authorComponent)
            .append(Component.newline())
            .append(dateComponent)
            .append(Component.newline())
            .append(Component.text("---------------------------------", NamedTextColor.DARK_GRAY))

        player.sendMessage(message)
    }
}
