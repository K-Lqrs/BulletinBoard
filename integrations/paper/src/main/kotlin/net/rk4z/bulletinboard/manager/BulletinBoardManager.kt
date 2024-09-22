package net.rk4z.bulletinboard.manager

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.utils.CustomID
import net.rk4z.bulletinboard.utils.Main
import net.rk4z.igf.Button
import net.rk4z.igf.SimpleGUI
import org.bukkit.Material
import org.bukkit.entity.Player

class BulletinBoardManager {
    fun openMainBoard(player: Player) {
        val newPost = LanguageManager.getMessage(player, Main.Gui.Button.NEW_POST) ?: Main.Gui.Button.NEW_POST.toComponent()
        val allPosts = LanguageManager.getMessage(player, Main.Gui.Button.ALL_POSTS) ?: Main.Gui.Button.ALL_POSTS.toComponent()
        val myPosts = LanguageManager.getMessage(player, Main.Gui.Button.MY_POSTS) ?: Main.Gui.Button.MY_POSTS.toComponent()
        val deletedPosts = LanguageManager.getMessage(player, Main.Gui.Button.DELETED_POSTS) ?: Main.Gui.Button.DELETED_POSTS.toComponent()
        val aboutPlugin = LanguageManager.getMessage(player, Main.Gui.Button.ABOUT_PLUGIN) ?: Main.Gui.Button.ABOUT_PLUGIN.toComponent()
        val settings = LanguageManager.getMessage(player, Main.Gui.Button.SETTINGS) ?: Main.Gui.Button.SETTINGS.toComponent()
        val help = LanguageManager.getMessage(player, Main.Gui.Button.HELP) ?: Main.Gui.Button.HELP.toComponent()

        val buttons = listOf<Button>(
            Button(10, Material.WRITABLE_BOOK, newPost, CustomID.NEW_POST.name),
            Button(12, Material.BOOK, allPosts, CustomID.ALL_POSTS.name),
            Button(14, Material.WRITTEN_BOOK, myPosts, CustomID.MY_POSTS.name),
            Button(16, Material.CAULDRON, deletedPosts, CustomID.DELETED_POSTS.name),
            Button(29, Material.LECTERN, aboutPlugin, CustomID.ABOUT_PLUGIN.name),
            Button(31, Material.COMPARATOR, settings, CustomID.SETTINGS.name),
            Button(33, Material.OAK_SIGN, help, CustomID.HELP.name)
        )

        val gui = SimpleGUI(player, Component.text(""), 45)
            .setButtons(buttons)
            .build()

        gui.open()
    }
}