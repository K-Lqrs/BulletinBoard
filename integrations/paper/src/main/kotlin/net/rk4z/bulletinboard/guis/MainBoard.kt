package net.rk4z.bulletinboard.guis

import net.rk4z.bulletinboard.manager.CommandManager.displayAbout
import net.rk4z.bulletinboard.manager.CommandManager.displayHelp
import net.rk4z.bulletinboard.utils.CustomID
import net.rk4z.bulletinboard.utils.Main
import net.rk4z.bulletinboard.utils.getPlayerState
import net.rk4z.igf.*
import net.rk4z.s1.pluginBase.LanguageManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.persistence.PersistentDataType

/**
 * Open the main board for the player
 * @param player the player to open the main board for
 *
 * This function is called from the following places: [net.rk4z.bulletinboard.utils.Commands.OPENBOARD]
 */
fun openMainBoard(player: Player) {
    val buttons = listOf(
        Button(10, Material.WRITABLE_BOOK, Main.Gui.Button.NEW_POST.t(player), CustomID.NEW_POST.name),
        Button(12, Material.BOOK, Main.Gui.Button.ALL_POSTS.t(player), CustomID.ALL_POSTS.name),
        Button(14, Material.WRITTEN_BOOK, Main.Gui.Button.MY_POSTS.t(player), CustomID.MY_POSTS.name),
        Button(16, Material.CAULDRON, Main.Gui.Button.DELETED_POSTS.t(player), CustomID.DELETED_POSTS.name),
        Button(29, Material.LECTERN, Main.Gui.Button.ABOUT_PLUGIN.t(player), CustomID.ABOUT_PLUGIN.name),
        Button(31, Material.COMPARATOR, Main.Gui.Button.SETTINGS.t(player), CustomID.SETTINGS.name),
        Button(33, Material.OAK_SIGN, Main.Gui.Button.HELP.t(player), CustomID.HELP.name)
    )

    val listener = object : GUIListener {
        override fun onInventoryClick(event: InventoryClickEvent, gui: InventoryGUI) {
            event.isCancelled = true

            val item = event.currentItem ?: return
            val meta = item.itemMeta ?: return
            val customId = meta.persistentDataContainer.get(IGF.key, PersistentDataType.STRING)?.let { CustomID.fromString(it) } ?: return

            when (customId) {
                CustomID.NEW_POST -> openPostEditor(player)
                CustomID.ALL_POSTS -> openAllPosts(player)
                CustomID.MY_POSTS -> openMyPosts(player)
                CustomID.DELETED_POSTS -> openDeletedPosts(player)
                CustomID.ABOUT_PLUGIN -> {
                    gui.close()
                    displayAbout(player)
                }
                CustomID.SETTINGS -> openSetting(player)
                CustomID.HELP -> {
                    gui.close()
                    displayHelp(player)
                }

                else -> { return }
            }
        }

        override fun onInventoryOpen(event: InventoryOpenEvent, gui: InventoryGUI) {
            // when player open the main board, clear all states
            val state = player.getPlayerState()
            state.clearAll()
        }

        override fun onInventoryClose(event: InventoryCloseEvent, gui: InventoryGUI) {
            // when player close the main board, clear all states
            val state = player.getPlayerState()
            state.clearAll()
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