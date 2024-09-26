package net.rk4z.bulletinboard.guis

import net.rk4z.bulletinboard.manager.LanguageManager
import net.rk4z.bulletinboard.utils.Button
import net.rk4z.bulletinboard.utils.CustomID
import net.rk4z.bulletinboard.utils.Main
import net.rk4z.bulletinboard.utils.igf.SimpleGUI
import org.bukkit.Material
import org.bukkit.entity.Player

fun openMainBoard(player: Player) {
    val newPost = LanguageManager.getMessage(player, Main.Gui.Button.NEW_POST)
    val allPosts = LanguageManager.getMessage(player, Main.Gui.Button.ALL_POSTS)
    val myPosts = LanguageManager.getMessage(player, Main.Gui.Button.MY_POSTS)
    val deletedPosts = LanguageManager.getMessage(player, Main.Gui.Button.DELETED_POSTS)
    val aboutPlugin = LanguageManager.getMessage(player, Main.Gui.Button.ABOUT_PLUGIN)
    val settings = LanguageManager.getMessage(player, Main.Gui.Button.SETTINGS)
    val help = LanguageManager.getMessage(player, Main.Gui.Button.HELP)

    val buttons = listOf(
        Button(10, Material.WRITABLE_BOOK, newPost, CustomID.NEW_POST.name),
        Button(12, Material.BOOK, allPosts, CustomID.ALL_POSTS.name),
        Button(14, Material.WRITTEN_BOOK, myPosts, CustomID.MY_POSTS.name),
        Button(16, Material.CAULDRON, deletedPosts, CustomID.DELETED_POSTS.name),
        Button(29, Material.LECTERN, aboutPlugin, CustomID.ABOUT_PLUGIN.name),
        Button(31, Material.COMPARATOR, settings, CustomID.SETTINGS.name),
        Button(33, Material.OAK_SIGN, help, CustomID.HELP.name)
    )

    val gui = SimpleGUI(player)
        .setTitle(LanguageManager.getMessage(player, Main.Gui.Title.MAIN_BOARD))
        .setSize(45)
        .setBackground(Material.BLACK_STAINED_GLASS_PANE)
        .setItems(buttons)
        .build()

    gui.open()
}