@file:Suppress("DuplicatedCode")

package net.rk4z.bulletinBoard.utils

import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.BulletinBoard.Companion.namespacedKey
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

object BulletinBoardUtil {
    fun setGlassPane(board: Inventory, slots: IntRange) {
        val glassPane = ItemStack(Material.BLACK_STAINED_GLASS_PANE)
        val meta: ItemMeta = glassPane.itemMeta
        meta.displayName(Component.text(""))
        glassPane.itemMeta = meta
        for (i in slots) {
            board.setItem(i, glassPane)
        }
    }

    fun setGlassPane(board: Inventory, slots: List<Int>) {
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
        customId: String? = null
    ): ItemStack {
        val item = ItemStack(material)
        val meta: ItemMeta = item.itemMeta
        meta.displayName(name)
        if (customModelData != null) {
            meta.setCustomModelData(customModelData)
        }
        if (customId != null) {
            meta.persistentDataContainer.set(namespacedKey, PersistentDataType.STRING, customId)
        }
        item.itemMeta = meta
        return item
    }
}