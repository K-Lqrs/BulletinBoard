package net.rk4z.bulletinBoard.managers

import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.BulletinBoard.Companion.runTask
import net.rk4z.bulletinBoard.utils.*
import net.rk4z.bulletinBoard.utils.BBUtil.createCustomItem
import net.rk4z.bulletinBoard.utils.BBUtil.setGlassPane
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object BulletinBoardManager {
    private val playerState = ConcurrentHashMap<UUID, PlayerState>()
    private val p = BulletinBoard.instance

    fun getPlayerState(uuid: UUID): PlayerState {
        return playerState.getOrPut(uuid) { PlayerState() }
    }

    fun openMainBoard(player: Player) {
        val mainBoard: Inventory = Bukkit.createInventory(null, 45, LanguageManager.getMessage(player, MessageKey.MAIN_BOARD))

        setGlassPane(mainBoard, 0..44)

        val buttons = listOf(
            // The "Key" is used to get the message from the LanguageManager
            // Quadruple(slot, material, key, customId)
            Button(10, Material.WRITABLE_BOOK, MessageKey.NEW_POST, CustomID.NEW_POST),
            Button(12, Material.BOOK, MessageKey.ALL_POSTS, CustomID.ALL_POSTS),
            Button(14, Material.WRITTEN_BOOK, MessageKey.MY_POSTS, CustomID.MY_POSTS),
            Button(16, Material.FLINT_AND_STEEL, MessageKey.DELETED_POSTS, CustomID.DELETED_POSTS),
            Button(29, Material.LECTERN, MessageKey.ABOUT_PLUGIN, CustomID.ABOUT_PLUGIN),
            Button(31, Material.COMPARATOR, MessageKey.SETTINGS, CustomID.SETTINGS),
            Button(33, Material.OAK_SIGN, MessageKey.HELP, CustomID.HELP)
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

        runTask(p) {
            player.openInventory(mainBoard)
        }
    }

    fun openPostEditor(player: Player) {
        val state = getPlayerState(player.uniqueId)
        val draft = state.draft ?: PostDraft()
        val title = draft.title ?: LanguageManager.getMessage(player, MessageKey.NO_TITLE)
        val content = draft.content ?: LanguageManager.getMessage(player, MessageKey.NO_CONTENT)

        val postEditor = createPostEditorInventory(
            player,
            title,
            content,
            LanguageManager.getMessage(player, MessageKey.POST_EDITOR),
            CustomID.POST_TITLE,
            CustomID.POST_CONTENT,
            CustomID.CANCEL_POST,
            CustomID.SAVE_POST
        )

        runTask(p) {
            player.openInventory(postEditor)
        }
    }

    fun openConfirmation(player: Player, type: ConfirmationType) {
        val state = getPlayerState(player.uniqueId)
        state.isOpeningConfirmation = true
        state.confirmationType = type

        val title = when (type) {
            ConfirmationType.SAVE_POST -> {
                MessageKey.SAVE_POST_CONFIRMATION
            }
            ConfirmationType.CANCEL_POST -> {
                MessageKey.CANCEL_POST_CONFIRMATION
            }
        }

        val confirmation =
            Bukkit.createInventory(null, 27, LanguageManager.getMessage(player,title))

        setGlassPane(confirmation, 0..26)

        if (type == ConfirmationType.SAVE_POST) {
            val buttons = listOf(
                Button(11, Material.RED_WOOL, MessageKey.CANCEL_CONFIRM_SAVE_POST, CustomID.CANCEL_CONFIRM_SAVE_POST),
                Button(13, Material.BLUE_WOOL, MessageKey.PREVIEW_POST, CustomID.PREVIEW_POST),
                Button(15, Material.GREEN_WOOL, MessageKey.CONFIRM_SAVE_POST, CustomID.CONFIRM_SAVE_POST)
            )

            buttons.forEach { (slot, material, key, customId) ->
                confirmation.setItem(
                    slot,
                    createCustomItem(
                        material,
                        LanguageManager.getMessage(player, key),
                        customId = customId
                    )
                )
            }
        }

        if (type == ConfirmationType.CANCEL_POST) {
            val buttons = listOf(
                Button(11, Material.RED_WOOL, MessageKey.CONTINUE_POST, CustomID.CONTINUE_POST),
                Button(15, Material.GREEN_WOOL, MessageKey.CONFIRM_CANCEL_POST, CustomID.CONFIRM_CANCEL_POST)
            )

            buttons.forEach { (slot, material, key, customId) ->
                confirmation.setItem(
                    slot,
                    createCustomItem(
                        material,
                        LanguageManager.getMessage(player, key),
                        customId = customId
                    )
                )
            }
        }
    }

    fun performAbout(player: Player) {
        runTask(p) {
            player.closeInventory()
            player.performCommand("bb about")
        }
    }

    fun performHelp(player: Player) {
        runTask(p) {
            player.closeInventory()
            player.performCommand("bb help")
        }
    }

    fun createPostEditorInventory(
        player: Player,
        title: Component,
        content: Component,
        editorTitle: Component,
        titleCustomId: CustomID,
        contentCustomId: CustomID,
        cancelCustomId: CustomID,
        saveCustomId: CustomID
    ): Inventory {
        val postEditor = Bukkit.createInventory(null, 27, editorTitle)
        setGlassPane(postEditor, 0..26)
        postEditor.setItem(11, createCustomItem(Material.PAPER, title, customId = titleCustomId))
        postEditor.setItem(15, createCustomItem(Material.BOOK, content, customId = contentCustomId))
        postEditor.setItem(
            19,
            createCustomItem(Material.RED_WOOL, LanguageManager.getMessage(player, MessageKey.CANCEL_POST), customId = cancelCustomId)
        )
        postEditor.setItem(
            25,
            createCustomItem(Material.GREEN_WOOL, LanguageManager.getMessage(player, MessageKey.SAVE_POST), customId = saveCustomId)
        )
        return postEditor
    }

}