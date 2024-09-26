package net.rk4z.bulletinboard.guis

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.manager.LanguageManager
import net.rk4z.bulletinboard.utils.*
import net.rk4z.bulletinboard.utils.igf.InventoryGUI
import net.rk4z.bulletinboard.utils.igf.SimpleGUI
import org.bukkit.Material
import org.bukkit.entity.Player

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

fun openPostEditorForEdit(player: Player, post: Post) {
    val state = player.getPlayerState()
    state.editDraft = EditPostData(
        id = post.id,
        title = post.title,
        content = post.content
    )

    val postEditor = createPostEditorInventory(
        player,
        post.title,
        post.content,
        LanguageManager.getMessage(player, Main.Gui.Title.POST_EDITOR_FOR_EDIT),
        CustomID.EDIT_POST_TITLE,
        CustomID.EDIT_POST_CONTENT,
        CustomID.CANCEL_EDIT,
        CustomID.SAVE_EDIT
    )

    postEditor.open()
}

private fun createPostEditorInventory(
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

    val postEditor = SimpleGUI(player)
        .setTitle(editorTitle)
        .setSize(27)
        .setBackground(Material.BLACK_STAINED_GLASS_PANE)
        .setItems(buttons)
        .build()

    return postEditor
}