@file:Suppress("unused", "DuplicatedCode")

package net.rk4z.bulletinboard.guis

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.utils.*
import net.rk4z.igf.Button
import net.rk4z.igf.GUIListener
import net.rk4z.igf.IGF.key
import net.rk4z.igf.InventoryGUI
import net.rk4z.igf.SimpleGUI
import net.rk4z.s1.pluginBase.LanguageManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.persistence.PersistentDataType

fun openPostEditor(player: Player) {
    val state = player.getPlayerState()
    val draft = state.draft ?: PostDraft()
    val title = draft.title ?: LanguageManager.getMessage(player, Main.Gui.Other.NO_TITLE)
    val content = draft.content ?: LanguageManager.getMessage(player, Main.Gui.Other.NO_CONTENT)

    val postEditor = createPostEditorInventory(
        player,
        title,
        content,
        LanguageManager.getMessage(player, Main.Gui.Title.POST_EDITOR),
        CustomID.POST_TITLE,
        CustomID.POST_CONTENT,
        CustomID.CANCEL_POST,
        CustomID.SAVE_POST
    )

    postEditor.open()
}

fun openPostEditorForEdit(player: Player, post: Post?) {
    val state = player.getPlayerState()
    if (state.editDraft == null && post == null) {
        throw IllegalArgumentException("Post must not be null when editDraft is null")
    }
    post?.let {
        state.editDraft = EditPostData(
            id = it.id,
            title = it.title,
            content = it.content
        )
    }

    val postEditor = post?.let {
        createPostEditorInventory(
            player,
            it.title,
            it.content,
            LanguageManager.getMessage(player, Main.Gui.Title.POST_EDITOR_FOR_EDIT),
            CustomID.EDIT_POST_TITLE,
            CustomID.EDIT_POST_CONTENT,
            CustomID.CANCEL_EDIT,
            CustomID.SAVE_EDIT
        )
    } ?: return

    postEditor.open()
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
): InventoryGUI {
    val buttons = listOf(
        Button(11, Material.PAPER, title, titleCustomId.name),
        Button(15, Material.BOOK, content, contentCustomId.name),
        Button(19, Material.RED_WOOL, LanguageManager.getMessage(player, Main.Gui.Button.CANCEL_POST), cancelCustomId.name),
        Button(25, Material.GREEN_WOOL, LanguageManager.getMessage(player, Main.Gui.Button.SAVE_POST), saveCustomId.name)
    )

    val listener = object : GUIListener {
        override fun onInventoryClick(event: InventoryClickEvent, gui: InventoryGUI) {
            event.isCancelled = true

            val clickedItem = event.currentItem ?: return
            val meta = clickedItem.itemMeta ?: return
            val customId = meta.persistentDataContainer.get(key, PersistentDataType.STRING)
            val actionId = customId?.let { CustomID.fromString(it) } ?: return

            when (actionId) {
                CustomID.CANCEL_POST -> openCancelPostConfirmation(player)

                CustomID.SAVE_POST -> openSavePostConfirmation(player)

                CustomID.CANCEL_EDIT -> {
                    //TODO
                }

                CustomID.SAVE_EDIT -> {
                    //TODO
                }

                CustomID.EDIT_POST_TITLE -> {
                    gui.close()
                    val state = player.getPlayerState()
                    state.inputType = InputType.EDIT_TITLE
                    state.isInputting = true

                    player.sendMessage(LanguageManager.getMessage(player, Main.Message.ENTER_TITLE_EDIT))
                }

                CustomID.EDIT_POST_CONTENT -> {
                    gui.close()
                    val state = player.getPlayerState()
                    state.inputType = InputType.EDIT_CONTENT
                    state.isInputting = true

                    player.sendMessage(LanguageManager.getMessage(player, Main.Message.ENTER_CONTENT_EDIT))
                }

                CustomID.POST_TITLE -> {
                    gui.close()
                    val state = player.getPlayerState()
                    state.inputType = InputType.TITLE
                    state.isInputting = true

                    player.sendMessage(LanguageManager.getMessage(player, Main.Message.ENTER_TITLE))
                }

                CustomID.POST_CONTENT -> {
                    gui.close()
                    val state = player.getPlayerState()
                    state.inputType = InputType.CONTENT
                    state.isInputting = true

                    player.sendMessage(LanguageManager.getMessage(player, Main.Message.ENTER_CONTENT))
                }

                else -> {}
            }
        }

        override fun onInventoryClose(event: InventoryCloseEvent, gui: InventoryGUI) {
            if (event.reason == InventoryCloseEvent.Reason.PLAYER) {
                val state = player.getPlayerState()
                state.isInputting = null
                state.isEditInputting = null
                state.inputType = null
                state.editInputType = null
                state.isPreviewing = null
            }
        }

        override fun onInventoryOpen(event: InventoryOpenEvent, gui: InventoryGUI) {
            val state = player.getPlayerState()
            state.isInputting = null
            state.isEditInputting = null
            state.inputType = null
            state.editInputType = null
            state.isPreviewing = null
        }
    }

    val postEditor = SimpleGUI(player)
        .setTitle(editorTitle)
        .setSize(27)
        .setBackground(Material.GRAY_STAINED_GLASS_PANE)
        .setItems(buttons)
        .setListener(listener)
        .build()

    return postEditor
}