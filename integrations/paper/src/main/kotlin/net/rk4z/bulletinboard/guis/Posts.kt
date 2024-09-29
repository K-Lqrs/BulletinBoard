package net.rk4z.bulletinboard.guis

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.manager.LanguageManager
import net.rk4z.bulletinboard.utils.*
import net.rk4z.bulletinboard.utils.igf.GUIListener
import net.rk4z.bulletinboard.utils.igf.InventoryGUI
import net.rk4z.bulletinboard.utils.igf.SimpleGUI
import org.bukkit.Material
import org.bukkit.Sound
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
    val middleRowSlots = listOf(10, 12, 14, 16)
    val itemsPerPage = middleRowSlots.size
    val totalPages = (posts.size + itemsPerPage - 1) / itemsPerPage
    val currentPage = page.coerceIn(0, if (totalPages == 0) 0 else totalPages - 1)
    val startIndex = currentPage * itemsPerPage
    val endIndex = (startIndex + itemsPerPage).coerceAtMost(posts.size)
    val title = titleType.key.toComponent()

    val buttons: MutableList<Button> = mutableListOf()

    buttons.add(Button(22, Material.BARRIER, LanguageManager.getMessage(player, Main.Gui.Button.BACK_BUTTON), CustomID.BACK_BUTTON.name))

    if (posts.isEmpty()) {
        val noPostsItem = Button(13, Material.PAPER, LanguageManager.getMessage(player, Main.Gui.Other.NO_POSTS), CustomID.NO_POSTS.name)
        buttons.add(noPostsItem)
    } else {
        posts.subList(startIndex, endIndex).forEachIndexed { index, (postId, _, title, _, _) ->
            val postButton = Button(middleRowSlots[index], Material.WRITTEN_BOOK, title, postId.toString())
            buttons.add(postButton)
        }
    }

    if (currentPage > 0) {
        val prevPageButton = Button(
            18,
            Material.ARROW,
            LanguageManager.getMessage(player, Main.Gui.Button.PREV_PAGE),
            "${CustomID.PREV_PAGE.name}:$currentPage"
        )
        buttons.add(prevPageButton)
    }

    if (currentPage < totalPages - 1) {
        val nextPageButton = Button(
            26,
            Material.ARROW,
            LanguageManager.getMessage(player, Main.Gui.Button.NEXT_PAGE),
            "${CustomID.NEXT_PAGE.name}:$currentPage"
        )
        buttons.add(nextPageButton)
    }

    when (titleType) {
        TitleType.MY_POSTS -> {
            buttons.addAll(listOf(
                Button(20, Material.WRITABLE_BOOK, LanguageManager.getMessage(player, Main.Gui.Button.EDIT_POST), CustomID.EDIT_POST.name),
                Button(24, Material.CAULDRON, LanguageManager.getMessage(player, Main.Gui.Button.DELETE_POST), CustomID.DELETE_POST.name)
            ))
        }
        TitleType.DELETED_POSTS -> {
            buttons.addAll(listOf(
                Button(20, Material.RESPAWN_ANCHOR, LanguageManager.getMessage(player, Main.Gui.Button.RESTORE_POST), CustomID.RESTORE_POST.name),
                Button(24, Material.LAVA_BUCKET, LanguageManager.getMessage(player, Main.Gui.Button.DELETE_POST_PERMANENTLY), CustomID
                    .DELETE_POST_PERMANENTLY.name)
            ))
        }
        TitleType.ALL_POSTS -> {
            val hasPermission = player.hasPermission("bulletinboard.post.delete.other")
            if (hasPermission) {
                buttons.add(Button(24, Material.CAULDRON, LanguageManager.getMessage(player, Main.Gui.Button.DELETE_POST_OTHERS), CustomID.DELETE_POST_OTHERS.name))
            }
        }
        TitleType.DELETE_POST_SELECTION,
        TitleType.DELETE_POST_PERMANENTLY_SELECTION,
        TitleType.EDIT_POST_SELECTION,
        TitleType.RESTORE_POST_SELECTION -> {
            // no additional buttons
        }
    }

    val listener = object : GUIListener {
        override fun onInventoryClick(event: InventoryClickEvent, gui: InventoryGUI) {
            event.isCancelled = true

            val inventoryTitle = event.view.title()
            val clickedItem = event.currentItem ?: return
            val meta = clickedItem.itemMeta ?: return
            val customId = meta.persistentDataContainer.get(BulletinBoard.key, PersistentDataType.STRING) ?: return

            val splitResult = customId.split(":")
            if (splitResult.size < 2 || splitResult[0].isEmpty() || splitResult[1].isEmpty()) return

            val actionId = splitResult[0]
            val pageId = splitResult[1]
            val currentP = pageId.toIntOrNull() ?: 0

            when (CustomID.fromString(actionId)) {
                CustomID.PREV_PAGE -> {
                    player.playSoundMaster(Sound.ITEM_BOOK_PAGE_TURN, 0.5f)
                    openPage(player, inventoryTitle, currentP - 1)
                }
                CustomID.NEXT_PAGE -> {
                    player.playSoundMaster(Sound.ITEM_BOOK_PAGE_TURN, 0.5f)
                    openPage(player, inventoryTitle, currentP + 1)
                }
                CustomID.BACK_BUTTON -> {
                    openMainBoard(player)
                }

                else -> {}
            }
        }

        override fun onInventoryClose(event: InventoryCloseEvent, gui: InventoryGUI) {}
    }

    val inventory = SimpleGUI(player)
        .setTitle(title)
        .setSize(27)
        .setBackground(Material.GRAY_STAINED_GLASS_PANE)
        .setListener(listener)
        .setItems(buttons)
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
