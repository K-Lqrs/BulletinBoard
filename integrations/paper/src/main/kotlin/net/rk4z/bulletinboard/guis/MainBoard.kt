package net.rk4z.bulletinboard.guis

import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.manager.CommandManager.displayAbout
import net.rk4z.bulletinboard.manager.CommandManager.displayHelp
import net.rk4z.bulletinboard.manager.LanguageManager
import net.rk4z.bulletinboard.utils.CustomID
import net.rk4z.bulletinboard.utils.Main
import net.rk4z.igf.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.persistence.PersistentDataType

fun openMainBoard(player: Player) {
    val buttons = listOf(
        Button(10, Material.WRITABLE_BOOK, Main.Gui.Button.NEW_POST.translate(player), CustomID.NEW_POST.name),
        Button(12, Material.BOOK, Main.Gui.Button.ALL_POSTS.translate(player), CustomID.ALL_POSTS.name),
        Button(14, Material.WRITTEN_BOOK, Main.Gui.Button.MY_POSTS.translate(player), CustomID.MY_POSTS.name),
        Button(16, Material.CAULDRON, Main.Gui.Button.DELETED_POSTS.translate(player), CustomID.DELETED_POSTS.name),
        Button(29, Material.LECTERN, Main.Gui.Button.ABOUT_PLUGIN.translate(player), CustomID.ABOUT_PLUGIN.name),
        Button(31, Material.COMPARATOR, Main.Gui.Button.SETTINGS.translate(player), CustomID.SETTINGS.name),
        Button(33, Material.OAK_SIGN, Main.Gui.Button.HELP.translate(player), CustomID.HELP.name)
    )

    val listener = object : GUIListener {
        override fun onInventoryClick(event: InventoryClickEvent, gui: InventoryGUI) {
            event.isCancelled = true

            val item = event.currentItem ?: return
            val meta = item.itemMeta ?: return
            val customId = CustomID.fromString(meta.persistentDataContainer.get(IGF.key, PersistentDataType.STRING) ?: return)

            when (customId) {
                CustomID.NEW_POST -> openPostEditor(player)
                CustomID.ALL_POSTS -> openAllPosts(player)
                CustomID.MY_POSTS -> openMyPosts(player)
                CustomID.DELETED_POSTS -> openDeletedPosts(player)
                CustomID.ABOUT_PLUGIN -> {
                    gui.close()
                    displayAbout(player)
                }
                CustomID.SETTINGS -> TODO()
                CustomID.HELP -> {
                    gui.close()
                    displayHelp(player)
                }

                else -> { return }
            }
        }

        override fun onInventoryClose(event: InventoryCloseEvent, gui: InventoryGUI) {

        }
    }

    val gui = SimpleGUI(player)
        .setTitle(LanguageManager.getMessage(player, Main.Gui.Title.MAIN_BOARD))
        .setSize(45)
        .setBackground(Material.GRAY_STAINED_GLASS_PANE)
        .setItems(buttons)
        .setListener(listener)
        .build()

    gui.open()
}