package net.rk4z.igf

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

data class Button(
    val slot: Int,
    val material: Material,
    val name: Component,
    val customId: String? = null,
) {
    fun toItemStack(): ItemStack {
        return material.toItemStack(name, customId)
    }
}

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
        meta?.persistentDataContainer?.set(IGF.key, PersistentDataType.STRING, customId)
    }
    if (meta != null) {
        itemStack.itemMeta = meta
    }

    return itemStack
}