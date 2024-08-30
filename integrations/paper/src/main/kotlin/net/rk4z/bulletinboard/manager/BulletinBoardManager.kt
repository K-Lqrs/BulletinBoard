package net.rk4z.bulletinboard.manager

import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.BulletinBoard.Companion.runTask
import net.rk4z.bulletinboard.utils.BBUtil.addButtonsToInventory
import net.rk4z.bulletinboard.utils.BBUtil.setGlassPane
import net.rk4z.bulletinboard.utils.Button
import net.rk4z.bulletinboard.utils.CustomID
import net.rk4z.bulletinboard.utils.MessageKey
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object BulletinBoardManager {
    private val p = BulletinBoard.instance

    fun openMainBoard(player: Player) {
        val mainBoard: Inventory = Bukkit.createInventory(null, 45, LanguageManager.getMessage(player, MessageKey.MAIN_BOARD))

        mainBoard.setGlassPane(0..44)

        val buttons = listOf(
            Button(10, Material.WRITABLE_BOOK, MessageKey.NEW_POST, CustomID.NEW_POST),
            Button(12, Material.BOOK, MessageKey.ALL_POSTS, CustomID.ALL_POSTS),
            Button(14, Material.WRITTEN_BOOK, MessageKey.MY_POSTS, CustomID.MY_POSTS),
            Button(16, Material.CAULDRON, MessageKey.DELETED_POSTS, CustomID.DELETED_POSTS),
            Button(29, Material.LECTERN, MessageKey.ABOUT_PLUGIN, CustomID.ABOUT_PLUGIN),
            Button(31, Material.COMPARATOR, MessageKey.SETTINGS, CustomID.SETTINGS),
            Button(33, Material.OAK_SIGN, MessageKey.HELP, CustomID.HELP)
        )

        mainBoard.addButtonsToInventory(buttons, player)

        runTask(p) {
            player.openInventory(mainBoard)
        }
    }
}