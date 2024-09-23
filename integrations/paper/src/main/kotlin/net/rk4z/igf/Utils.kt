package net.rk4z.igf

import net.kyori.adventure.text.Component
import net.rk4z.igf.IGF.namespacedKey
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

data class Button(
    val slot: Int,
    val material: Material,
    val name: Component,
    val customId: String
) {
    fun toItemStack(): ItemStack {
        val itemStack = ItemStack(material)
        val meta: ItemMeta? = itemStack.itemMeta

        meta?.let {
            it.displayName(name)
            it.persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, customId)
            itemStack.itemMeta = it
        }

        return itemStack
    }
}

fun Inventory.addButton(button: Button) {
    this.setItem(button.slot, createCustomItem(button.material, button.name, customId = button.customId))
}

fun createCustomItem(
    material: Material,
    name: Component,
    customId: String? = null
): ItemStack {
    val item = ItemStack(material)
    val meta: ItemMeta = item.itemMeta
    meta.displayName(name)
    if (customId != null) {
        meta.persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, customId)
    }
    item.itemMeta = meta
    return item
}