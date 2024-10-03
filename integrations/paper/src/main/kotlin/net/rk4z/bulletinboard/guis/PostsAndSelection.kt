@file:Suppress("unused")

package net.rk4z.bulletinboard.guis

import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.manager.LanguageManager
import net.rk4z.bulletinboard.utils.*
import net.rk4z.bulletinboard.utils.TitleType.*
import net.rk4z.igf.Button
import net.rk4z.igf.GUIListener
import net.rk4z.igf.IGF.key
import net.rk4z.igf.InventoryGUI
import net.rk4z.igf.PaginatedGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.persistence.PersistentDataType

fun openMyPosts(player: Player, page: Int = 0) {
    val playerPosts = BulletinBoard.dataBase.getPostsByAuthor(player.uniqueId)
    openPostsInventory(player, MY_POSTS, playerPosts, page)
}

fun openAllPosts(player: Player, page: Int = 0) {
    val posts = BulletinBoard.dataBase.getAllPosts()
    openPostsInventory(player, ALL_POSTS, posts, page)
}

fun openDeletedPosts(player: Player, page: Int = 0) {
    val deletedPosts = BulletinBoard.dataBase.getDeletedPostByAuthor(player.uniqueId)
    openPostsInventory(player, DELETED_POSTS, deletedPosts, page)
}

fun openEditPostSelection(player: Player, page: Int = 0) {
    val playerPosts = BulletinBoard.dataBase.getPostsByAuthor(player.uniqueId)
    openPostsInventory(player, EDIT_POST_SELECTION, playerPosts, page)
}

fun openDeletePostSelection(player: Player, page: Int = 0) {
    val playerPosts = BulletinBoard.dataBase.getPostsByAuthor(player.uniqueId)
    openPostsInventory(player, DELETE_POST_SELECTION, playerPosts, page)
}

fun openDeletePostFromAllPlayerSelection(player: Player, page: Int = 0) {
    val posts = BulletinBoard.dataBase.getAllPosts()
    openPostsInventory(player, DELETE_POST_ALL_PLAYER_SELECTION, posts, page)
}

fun openRestorePostSelection(player: Player, page: Int = 0) {
    val deletedPosts = BulletinBoard.dataBase.getDeletedPostByAuthor(player.uniqueId)
    openPostsInventory(player, RESTORE_POST_SELECTION, deletedPosts, page)
}

fun openDeletePostPermanentlySelection(player: Player, page: Int = 0) {
    val deletedPosts = BulletinBoard.dataBase.getDeletedPostByAuthor(player.uniqueId)
    openPostsInventory(player, DELETE_POST_PERMANENTLY_SELECTION, deletedPosts, page)
}

private fun openPostsInventory(player: Player, titleType: TitleType, posts: List<Post>, page: Int) {
    val title = titleType.key.translate(player)
    val buttons = mutableListOf<Button>()
    val middleRowSlots = listOf(10, 12, 14, 16)
    val postButtons = if (posts.isNotEmpty()) {
        posts.mapIndexed { index, (postId, _, title, _, _) ->
            Button(middleRowSlots.getOrNull(index) ?: -1, Material.WRITTEN_BOOK, title, postId.toString())
        }
    } else {
        emptyList()
    }

    val listener = object : GUIListener {
        override fun onInventoryClick(event: InventoryClickEvent, gui: InventoryGUI) {
            event.isCancelled = true
            val clickedItem = event.currentItem ?: return
            val meta = clickedItem.itemMeta ?: return
            val customId = meta.persistentDataContainer.get(key, PersistentDataType.STRING) ?: return
            val state = player.getPlayerState()

            when (gui.getTitle()!!) {
                Main.Gui.Title.MY_POSTS.translate(player),
                Main.Gui.Title.ALL_POSTS.translate(player),
                Main.Gui.Title.DELETED_POSTS.translate(player) -> {
                    when (customId) {
                        CustomID.BACK_BUTTON.name -> openMainBoard(player)
                        CustomID.DELETE_POST_FROM_ALL.name -> openDeletePostFromAllPlayerSelection(player)
                        CustomID.EDIT_POST.name -> openEditPostSelection(player)
                        CustomID.DELETE_POST.name -> openDeletePostSelection(player)

                        else -> {
                            val post = BulletinBoard.dataBase.getPost(customId)
                            displayPost(player, post)
                        }
                    }
                }

                Main.Gui.Title.DELETE_POST_ALL_PLAYER_SELECTION.translate(player) -> {
                    when (customId) {
                        CustomID.BACK_BUTTON.name -> openAllPosts(player)

                        else -> {
                            state.selectedDeletingPostId = customId
                            openDeletePostFromAllConfirmation(player)
                        }
                    }
                }

                Main.Gui.Title.EDIT_POST_SELECTION.translate(player) -> {
                    when (customId) {
                        CustomID.BACK_BUTTON.name -> openMyPosts(player)

                        else -> {
                            state.selectedEditingPostId = customId
                            val post = BulletinBoard.dataBase.getPost(customId)
                            openPostEditorForEdit(player, post)
                        }
                    }
                }

                Main.Gui.Title.DELETE_POST_SELECTION.translate(player) -> {
                    when (customId) {
                        CustomID.BACK_BUTTON.name -> openMyPosts(player)

                        else -> {
                            state.selectedDeletingPostId = customId
                            openDeletePostConfirmation(player)
                        }
                    }
                }

                Main.Gui.Title.RESTORE_POST_SELECTION.translate(player) -> {
                    when (customId) {
                        CustomID.BACK_BUTTON.name -> openDeletedPosts(player)

                        else -> {
                            state.selectedRestoringPostId = customId
                            openRestorePostConfirmation(player)
                        }
                    }
                }

                Main.Gui.Title.DELETE_POST_PERMANENTLY_SELECTION.translate(player) -> {
                    when (customId) {
                        CustomID.BACK_BUTTON.name -> openDeletedPosts(player)

                        else -> {
                            state.selectedDeletingPostId = customId
                            openDeletePostPermanentlyConfirmation(player)
                        }
                    }
                }

                else -> { return }
            }
        }

        override fun onInventoryOpen(event: InventoryOpenEvent, gui: InventoryGUI) {}

        override fun onInventoryClose(event: InventoryCloseEvent, gui: InventoryGUI) {}
    }

    val noPosts = Button(13, Material.PAPER, LanguageManager.getMessage(player, Main.Gui.Other.NO_POSTS), CustomID.NO_POSTS.name)
    buttons.add(Button(22, Material.BARRIER, LanguageManager.getMessage(player, Main.Gui.Button.BACK_BUTTON), CustomID.BACK_BUTTON.name))

    val pageButtons = Pair(
        Button(18, Material.ARROW, LanguageManager.getMessage(player, Main.Gui.Button.PREV_PAGE), CustomID.PREV_PAGE.name),
        Button(26, Material.ARROW, LanguageManager.getMessage(player, Main.Gui.Button.NEXT_PAGE), CustomID.NEXT_PAGE.name)
    )

    when (titleType) {
        ALL_POSTS -> {
            if (player.hasPermission("bulletinboard.post.delete.other")) {
                buttons.add(Button(20, Material.RED_WOOL, LanguageManager.getMessage(player, Main.Gui.Button.DELETE_POST_FROM_ALL), CustomID.DELETE_POST_FROM_ALL.name))
            }
        }
        MY_POSTS -> {
            buttons.addAll(listOf(
                Button(20, Material.WRITABLE_BOOK, Main.Gui.Button.EDIT_POST.translate(player), CustomID.EDIT_POST.name),
                Button(24, Material.CAULDRON, Main.Gui.Button.DELETE_POST.translate(player), CustomID.DELETE_POST.name)
            ))
        }
        DELETED_POSTS -> {
            buttons.addAll(listOf(
                Button(20, Material.RESPAWN_ANCHOR, Main.Gui.Button.RESTORE_POST.translate(player), CustomID.RESTORE_POST.name),
                Button(24, Material.LAVA_BUCKET, Main.Gui.Button.DELETE_POST_PERMANENTLY.translate(player), CustomID.DELETE_POST_PERMANENTLY.name)
            ))
        }

        else -> {
            // no additional buttons
        }
    }

    val inventory = PaginatedGUI(player)
        .setSlotPositions(middleRowSlots)
        .setPageItems(postButtons)
        .setItemsPerPage(middleRowSlots.size)
        .setEmptyMessageButton(noPosts)
        .setPage(page.coerceAtLeast(0))
        .setPageButtonsCustomID(CustomID.PREV_PAGE.name, CustomID.NEXT_PAGE.name)
        .setPageButtons(pageButtons.first, pageButtons.second)
        // End of the PaginatedGUI configuration
        .setSize(27)
        .setTitle(title)
        .setBackground(Material.GRAY_STAINED_GLASS_PANE)
        .setItems(buttons)
        .setListener(listener)
        .build()

    inventory.open()
}

