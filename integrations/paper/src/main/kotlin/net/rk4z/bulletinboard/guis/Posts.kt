@file:Suppress("unused")

package net.rk4z.bulletinboard.guis

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.manager.LanguageManager
import net.rk4z.bulletinboard.utils.*
import net.rk4z.igf.Button
import net.rk4z.igf.GUIListener
import net.rk4z.igf.InventoryGUI
import net.rk4z.igf.PaginatedGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.persistence.PersistentDataType

fun openMyPosts(player: Player, page: Int = 0) {
    val playerPosts = BulletinBoard.dataBase.getPostsByAuthor(player.uniqueId)
    openPostsInventory(player, TitleType.MY_POSTS, playerPosts, page)
}

fun openAllPosts(player: Player, page: Int = 0) {
    val posts = BulletinBoard.dataBase.getAllPosts()
    openPostsInventory(player, TitleType.ALL_POSTS, posts, page)
}

fun openDeletedPosts(player: Player, page: Int = 0) {
    val deletedPosts = BulletinBoard.dataBase.getDeletedPostByAuthor(player.uniqueId)
    openPostsInventory(player, TitleType.DELETED_POSTS, deletedPosts, page)
}

private fun openPostsInventory(player: Player, titleType: TitleType, posts: List<Post>, page: Int) {
    val title = titleType.key.toComponent()
    val middleRowSlots = listOf(10, 12, 14, 16)
    val postButtons = posts.mapIndexed { index, (postId, _, title, _, _) ->
        Button(middleRowSlots.getOrNull(index) ?: -1, Material.WRITTEN_BOOK, title, postId.toString())
    }

    val listener = object : GUIListener {
        override fun onInventoryClick(event: InventoryClickEvent, gui: InventoryGUI) {
            event.isCancelled = true
            val clickedItem = event.currentItem ?: return
            val meta = clickedItem.itemMeta ?: return
            val customId = meta.persistentDataContainer.get(BulletinBoard.key, PersistentDataType.STRING) ?: return

            when (customId) {
                "backButton" -> openMainBoard(player)
            }
        }

        override fun onInventoryClose(event: InventoryCloseEvent, gui: InventoryGUI) {}
    }

    val noPosts = Button(13, Material.PAPER, LanguageManager.getMessage(player, Main.Gui.Other.NO_POSTS), CustomID.NO_POSTS.name)

    val pageButtons = Pair(
        Button(18, Material.ARROW, LanguageManager.getMessage(player, Main.Gui.Button.PREV_PAGE)),
        Button(26, Material.ARROW, LanguageManager.getMessage(player, Main.Gui.Button.NEXT_PAGE))
    )

    val inventory = PaginatedGUI(player)
        .setSlotPositions(middleRowSlots)
        .setPageItems(postButtons)
        .setEmptyMessageButton(noPosts)
        .setPage(page)
        .setPageButtonsCustomID(CustomID.PREV_PAGE.name, CustomID.NEXT_PAGE.name)
        .setPageButtons(pageButtons.first, pageButtons.second)
        .setListener(listener)
        .setTitle(title)
        .setSize(27)
        .setBackground(Material.GRAY_STAINED_GLASS_PANE)
        .build()

    inventory.open()
}


private fun openPage(player: Player, inventory: Component, page: Int) {
    val myPostsTitle = LanguageManager.getMessage(player, Main.Gui.Title.MY_POSTS)
    val allPostsTitle = LanguageManager.getMessage(player, Main.Gui.Title.ALL_POSTS)
    val deletedPostsTitle = LanguageManager.getMessage(player, Main.Gui.Title.DELETED_POSTS)

    when (inventory) {
        myPostsTitle -> openMyPosts(player, page)
        allPostsTitle -> openAllPosts(player, page)
        deletedPostsTitle -> openDeletedPosts(player, page)
        else -> return
    }
}
