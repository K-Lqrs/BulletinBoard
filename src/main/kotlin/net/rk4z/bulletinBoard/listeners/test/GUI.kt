package net.rk4z.bulletinBoard.listeners.test

import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.managers.LanguageManager
import net.rk4z.bulletinBoard.managers.LanguageManager.messages
import net.rk4z.bulletinBoard.utils.BulletinBoardUtil.createCustomItem
import net.rk4z.bulletinBoard.utils.BulletinBoardUtil.setGlassPane
import net.rk4z.bulletinBoard.utils.Quadruple
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object GUI {
    private fun addLang() {
        val newMessages = mapOf(
            "ja" to mapOf(
                "secBoard" to "秘密のボード",

                "ores" to "鉱石",
                "enchantments" to "エンチャント",
                "enchantmentBooks" to "エンチャント本",
                "effects" to "エフェクト",

                "backpack" to "バックパック",
                "gamemodes" to "ゲームモード",
                "custom" to "カスタムアイテム"
            ),
            "en" to mapOf(
                "secBoard" to "Secret Board",

                "ores" to "Ores",
                "enchantments" to "Enchantments",
                "enchantmentBooks" to "Enchantment Books",
                "effects" to "Effects",

                "backpack" to "Backpack",
                "gamemodes" to "Gamemodes",
                "custom" to "Custom Items"
            )
        )

        for ((lang, entries) in newMessages) {
            val langMap = messages.getOrPut(lang) { mutableMapOf() }
            langMap.putAll(entries)
        }
    }

    fun openSecBoard(player: Player) {
        addLang()
        val secBoard: Inventory = Bukkit.createInventory(null, 45, LanguageManager.getMessage(player, "secBoard"))

        setGlassPane(secBoard, 0..44)

        val buttons = listOf(
            Quadruple(10, Material.IRON_INGOT, "ores", "ores"),
            Quadruple(12, Material.ANVIL, "enchantments", "enchantments"),
            Quadruple(14, Material.ENCHANTED_BOOK, "enchantmentBooks", "enchantmentBooks"),
            Quadruple(16, Material.POTION, "effects", "effects"),

            Quadruple(29, Material.CHEST, "backpack", "backpack"),
            Quadruple(31, Material.CRAFTING_TABLE, "gamemodes", "gamemodes"),
            Quadruple(33, Material.COMMAND_BLOCK, "custom", "custom")
        )

        buttons.forEach { (slot, material, key, customId) ->
            secBoard.setItem(
                slot,
                createCustomItem(
                    material,
                    LanguageManager.getMessage(player, key),
                    customId = customId
                )
            )
        }

        Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
            player.openInventory(secBoard)
        })
    }
}