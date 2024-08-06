package net.rk4z.bulletinBoard.managers

import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.utils.BBUtil.createCustomItem
import net.rk4z.bulletinBoard.utils.BBUtil.setGlassPane
import net.rk4z.bulletinBoard.utils.PlayerState
import net.rk4z.bulletinBoard.utils.PostDraft
import net.rk4z.bulletinBoard.utils.Quadruple
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object BulletinBoardManager {
    private val playerState = ConcurrentHashMap<UUID, PlayerState>()

    fun getPlayerState(uuid: UUID): PlayerState {
        return playerState.getOrPut(uuid) { PlayerState() }
    }

    fun openMainBoard(player: Player) {
        val mainBoard: Inventory = Bukkit.createInventory(null, 45, LanguageManager.getMessage(player, "mainBoard"))

        setGlassPane(mainBoard, 0..44)

        val buttons = listOf(
            // The "Key" is used to get the message from the LanguageManager
            // Quadruple(slot, material, key, customId)
            Quadruple(10, Material.WRITABLE_BOOK, "newPost", "newPost"),
            Quadruple(12, Material.BOOK, "allPosts", "allPosts"),
            Quadruple(14, Material.WRITTEN_BOOK, "myPosts", "myPosts"),
            Quadruple(16, Material.FLINT_AND_STEEL, "deletedPosts", "deletedPosts"),
            Quadruple(29, Material.LECTERN, "aboutPlugin", "about"),
            Quadruple(31, Material.COMPARATOR, "settings", "settings"),
            Quadruple(33, Material.OAK_SIGN, "help", "help")
        )

        buttons.forEach { (slot, material, key, customId) ->
            mainBoard.setItem(
                slot,
                createCustomItem(
                    material,
                    LanguageManager.getMessage(player, key),
                    customId = customId
                )
            )
        }

        Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
            player.openInventory(mainBoard)
        })
    }

    fun openPostEditor(player: Player) {
        val state = getPlayerState(player.uniqueId)
        val draft = state.draft ?: PostDraft()
        val title = draft.title ?: LanguageManager.getMessage(player, "noTitle")
        val content = draft.content ?: LanguageManager.getMessage(player, "noContent")

        val postEditor = createPostEditorInventory(
            player,
            title,
            content,
            LanguageManager.getMessage(player, "postEditor"),
            "postTitle",
            "postContent",
            "cancelPost",
            "savePost"
        )

        Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
            player.openInventory(postEditor)
        })
    }

    fun createPostEditorInventory(
        player: Player,
        title: Component,
        content: Component,
        editorTitle: Component,
        titleCustomId: String,
        contentCustomId: String,
        cancelCustomId: String,
        saveCustomId: String
    ): Inventory {
        val postEditor = Bukkit.createInventory(null, 27, editorTitle)
        setGlassPane(postEditor, 0..26)
        postEditor.setItem(11, createCustomItem(Material.PAPER, title, customId = titleCustomId))
        postEditor.setItem(15, createCustomItem(Material.BOOK, content, customId = contentCustomId))
        postEditor.setItem(
            19,
            createCustomItem(Material.RED_WOOL, LanguageManager.getMessage(player, "cancelPost"), customId = cancelCustomId)
        )
        postEditor.setItem(
            25,
            createCustomItem(Material.GREEN_WOOL, LanguageManager.getMessage(player, "savePost"), customId = saveCustomId)
        )
        return postEditor
    }

}