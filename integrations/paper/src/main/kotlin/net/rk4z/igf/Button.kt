package net.rk4z.igf

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

data class Button(
    val slot: Int,
    val material: Material,
    val name: Component,
    val customId: String? = null,
    val key: NamespacedKey? = null
) {
    fun toItemStack(): ItemStack {
        return material.toItemStack(name, customId, key)
    }
}

fun Material.toItemStack(
    name: Component? = null,
    customId: String? = null,
    key: NamespacedKey? = null
): ItemStack {
    val itemStack = ItemStack(this)
    val meta: ItemMeta? = itemStack.itemMeta

    if (name != null) {
        meta?.displayName(name)
    }
    if (customId != null) {
        if (key != null) {
            meta?.persistentDataContainer?.set(key, PersistentDataType.STRING, customId)
        } else {
            throw IllegalArgumentException("Key must be set if customId is set")
        }
    }
    if (meta != null) {
        itemStack.itemMeta = meta
    }

    return itemStack
}