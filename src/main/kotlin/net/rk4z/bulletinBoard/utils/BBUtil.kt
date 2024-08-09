package net.rk4z.bulletinBoard.utils

import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.BulletinBoard
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

@Suppress("DuplicatedCode", "unused")
object BBUtil {
    fun setGlassPane(board: Inventory, slots: IntRange) {
        val glassPane = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        val meta: ItemMeta = glassPane.itemMeta
        meta.displayName(Component.text(""))
        glassPane.itemMeta = meta
        for (i in slots) {
            board.setItem(i, glassPane)
        }
    }

    fun setGlassPane(board: Inventory, slots: Collection<Int>) {
        val glassPane = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        val meta: ItemMeta = glassPane.itemMeta
        meta.displayName(Component.text(""))
        glassPane.itemMeta = meta
        for (i in slots) {
            board.setItem(i, glassPane)
        }
    }

    fun createCustomItem(
        material: Material,
        name: Component,
        customModelData: Int? = null,
        customId: CustomID? = null
    ): ItemStack {
        val item = ItemStack(material)
        val meta: ItemMeta = item.itemMeta
        meta.displayName(name)
        if (customModelData != null) {
            meta.setCustomModelData(customModelData)
        }
        if (customId != null) {
            meta.persistentDataContainer.set(BulletinBoard.namespacedKey, PersistentDataType.STRING, customId.name)
        }
        item.itemMeta = meta
        return item
    }

    fun createCustomItem(
        material: Material,
        name: Component,
        customModelData: Int? = null,
        customId: String? = null
    ): ItemStack {
        val item = ItemStack(material)
        val meta: ItemMeta = item.itemMeta
        meta.displayName(name)
        if (customModelData != null) {
            meta.setCustomModelData(customModelData)
        }
        if (customId != null) {
            meta.persistentDataContainer.set(BulletinBoard.namespacedKey, PersistentDataType.STRING, customId)
        }
        item.itemMeta = meta
        return item
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


}