package net.rk4z.bulletinboard.manager

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.utils.CustomID
import net.rk4z.bulletinboard.utils.Main
import net.rk4z.bulletinboard.utils.getPlayerState
import net.rk4z.igf.BaseInventoryGUI
import net.rk4z.igf.Button
import net.rk4z.igf.IGF.logger
import net.rk4z.igf.SimpleGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.math.log

object BulletinBoardManager {
    var mainBoard: BaseInventoryGUI? = null

    fun openMainBoard(player: Player) {
        val newPost = LanguageManager.getMessage(player, Main.Gui.Button.NEW_POST)
        val allPosts = LanguageManager.getMessage(player, Main.Gui.Button.ALL_POSTS)
        val myPosts = LanguageManager.getMessage(player, Main.Gui.Button.MY_POSTS)
        val deletedPosts = LanguageManager.getMessage(player, Main.Gui.Button.DELETED_POSTS)
        val aboutPlugin = LanguageManager.getMessage(player, Main.Gui.Button.ABOUT_PLUGIN)
        val settings = LanguageManager.getMessage(player, Main.Gui.Button.SETTINGS)
        val help = LanguageManager.getMessage(player, Main.Gui.Button.HELP)

        val buttons = listOf<Button>(
            Button(10, Material.WRITABLE_BOOK, newPost, CustomID.NEW_POST.name),
            Button(12, Material.BOOK, allPosts, CustomID.ALL_POSTS.name),
            Button(14, Material.WRITTEN_BOOK, myPosts, CustomID.MY_POSTS.name),
            Button(16, Material.CAULDRON, deletedPosts, CustomID.DELETED_POSTS.name),
            Button(29, Material.LECTERN, aboutPlugin, CustomID.ABOUT_PLUGIN.name),
            Button(31, Material.COMPARATOR, settings, CustomID.SETTINGS.name),
            Button(33, Material.OAK_SIGN, help, CustomID.HELP.name)
        )

        if (mainBoard == null) {
            mainBoard = SimpleGUI(player, LanguageManager.getMessage(player, Main.Gui.Title.MAIN_BOARD), 45)
                .setBackground(Material.BLACK_STAINED_GLASS_PANE)
                .setButtons(buttons)
                .create()
                .build()
        }

        mainBoard?.open()
    }

    fun openPostEditor(player: Player) {
        val draft = player.getPlayerState().draft
        val title = draft?.title ?: LanguageManager.getMessage(player, Main.Gui.Other.NO_TITLE)
        val content = draft?.content ?: LanguageManager.getMessage(player, Main.Gui.Other.NO_CONTENT)

        val gui = createPostEditorInventory(
            player,
            title,
            content,
            LanguageManager.getMessage(player, Main.Gui.Title.POST_EDITOR),
            CustomID.POST_TITLE,
            CustomID.POST_CONTENT,
            CustomID.CANCEL_POST,
            CustomID.SAVE_POST
        )

        gui.open()
    }

    fun openPostEditorForEdit(player: Player, title: String, content: String) {
        val draft = player.getPlayerState().editDraft
        val title = draft?.title ?: LanguageManager.getMessage(player, Main.Gui.Other.NO_TITLE)
        val content = draft?.content ?: LanguageManager.getMessage(player, Main.Gui.Other.NO_CONTENT)

        val gui = createPostEditorInventory(
            player,
            title,
            content,
            LanguageManager.getMessage(player, Main.Gui.Title.POST_EDITOR),
            CustomID.EDIT_POST_TITLE,
            CustomID.EDIT_POST_CONTENT,
            CustomID.CANCEL_EDIT,
            CustomID.SAVE_EDIT
        )

        gui.open()
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
    ): BaseInventoryGUI {
        val buttons = listOf<Button>(
            Button(11, Material.PAPER, title, titleCustomId.name),
            Button(15, Material.BOOK, content, contentCustomId.name),
            Button(25, Material.GREEN_WOOL, LanguageManager.getMessage(player, Main.Gui.Button.SAVE_POST), saveCustomId.name),
            Button(19, Material.RED_WOOL, LanguageManager.getMessage(player, Main.Gui.Button.CANCEL_POST), cancelCustomId.name)
        )

        val postEditor = SimpleGUI(player, editorTitle, 27)
            .setBackground(Material.BLACK_STAINED_GLASS_PANE)
            .setButtons(buttons)
            .create()
            .build()

        return postEditor
    }
}