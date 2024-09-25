package net.rk4z.bulletinboard.utils

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.BulletinBoard
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

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
    amount: Int = 1,
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

