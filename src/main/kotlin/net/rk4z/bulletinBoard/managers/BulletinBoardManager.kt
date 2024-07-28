package net.rk4z.bulletinBoard.managers

import net.rk4z.bulletinBoard.utils.BulletinBoardUtil.createCustomItem
import net.rk4z.bulletinBoard.utils.BulletinBoardUtil.setGlassPane
import net.rk4z.bulletinBoard.utils.PlayerState
import net.rk4z.bulletinBoard.utils.Quadruple
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object BulletinBoardManager {
    private val playerStates = ConcurrentHashMap<UUID, PlayerState>()

    fun getPlayerState(playerId: UUID): PlayerState {
        return playerStates.getOrPut(playerId) { PlayerState() }
    }

    fun openMainBoard(player: Player) {
        val mainBoard: Inventory = Bukkit.createInventory(null, 45, LanguageManager.getMessage(player, "mainBoard"))

        setGlassPane(mainBoard, 0..35)

        val buttons = listOf(
            Quadruple(10, Material.WRITABLE_BOOK, "newPost", "newPost"),
            Quadruple(12, Material.BOOK, "posts", "posts"),
            Quadruple(14, Material.WRITTEN_BOOK, "myPosts", "myPosts"),
            Quadruple(16, Material.FLINT_AND_STEEL, "deletedPosts", "deletedPosts")
        )

        buttons.forEach { (slot, material, messageKey, customId) ->
            mainBoard.setItem(
                slot,
                createCustomItem(
                    material,
                    LanguageManager.getMessage(player, messageKey),
                    customId = customId
                )
            )
        }


        player.openInventory(mainBoard)
    }


}