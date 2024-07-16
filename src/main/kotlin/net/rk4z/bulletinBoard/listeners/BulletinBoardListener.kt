@file:Suppress("DEPRECATION")

package net.rk4z.bulletinBoard.listeners

import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.BulletinBoard.Companion.namespacedKey
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.displayPost
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.openAllPosts
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.openConfirmationScreen
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.openDeletePostSelection
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.openEditPostSelection
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.openMainBoard
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.openMyPosts
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.openPostEditor
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.openPreview
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.openDeleteConfirmationScreen
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.closePreview
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.openEditPostEditor
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.pendingConfirmations
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.pendingEdits
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.pendingDrafts
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.pendingInputs
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.pendingPreview
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.playerInputting
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.playerOpeningConfirmation
import net.rk4z.bulletinBoard.manager.BulletinBoardManager.playerPreviewing
import net.rk4z.bulletinBoard.manager.LanguageManager
import net.rk4z.bulletinBoard.util.JsonUtil
import net.rk4z.bulletinBoard.util.Post
import net.rk4z.bulletinBoard.util.PostDraft
import net.rk4z.bulletinBoard.util.BulletinBoardUtil.createCustomItem
import net.rk4z.bulletinBoard.util.BulletinBoardUtil.setGlassPane
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.persistence.PersistentDataType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("unused")
class BulletinBoardListener : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val clickedItem = event.currentItem ?: return
        val player = event.whoClicked as Player
        val inventoryTitle = event.view.title()
        val itemMeta = clickedItem.itemMeta ?: return

        if (clickedItem.type.isAir) {
            return
        }
        val customId = itemMeta.persistentDataContainer.get(namespacedKey, PersistentDataType.STRING)

        if (clickedItem.type == Material.BLACK_STAINED_GLASS_PANE) {
            event.isCancelled = true
            return
        }

        if (customId == "no_posts") {
            event.isCancelled = true
            return
        }

        when (inventoryTitle) {
            LanguageManager.getMessage(player, "main_board") -> {
                event.isCancelled = true
                when (customId) {
                    "new_post" -> openPostEditor(player)
                    "all_posts" -> openAllPosts(player)
                    "my_posts" -> openMyPosts(player)
                }
            }

            LanguageManager.getMessage(player, "all_posts"), LanguageManager.getMessage(player, "my_posts") -> {
                event.isCancelled = true
                val currentPage = customId?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0

                when (customId?.split(":")?.getOrNull(0)) {
                    "prev_page" -> {
                        if (inventoryTitle == LanguageManager.getMessage(player, "all_posts")) {
                            openAllPosts(player, currentPage - 1)
                        } else {
                            openMyPosts(player, currentPage - 1)
                        }
                    }

                    "next_page" -> {
                        if (inventoryTitle == LanguageManager.getMessage(player, "all_posts")) {
                            openAllPosts(player, currentPage + 1)
                        } else {
                            openMyPosts(player, currentPage + 1)
                        }
                    }

                    "back_button" -> openMainBoard(player)
                    "delete_post" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        openDeletePostSelection(player, posts)
                    }

                    "edit_post" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        openEditPostSelection(player, posts)
                    }

                    else -> {
                        if (customId != null) {
                            val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                            val post = data.posts.find { it.id == customId }
                            if (post != null) {
                                player.closeInventory()
                                displayPost(player, post)
                            }
                        }
                    }
                }
            }

            LanguageManager.getMessage(player, "post_editor") -> {
                event.isCancelled = true
                when (customId) {
                    "post_title" -> {
                        playerInputting[player.uniqueId] = true
                        player.closeInventory()
                        pendingInputs[player.uniqueId] = "title"
                        player.sendMessage(LanguageManager.getMessage(player, "please_enter_title"))
                    }

                    "post_content" -> {
                        playerInputting[player.uniqueId] = true
                        player.closeInventory()
                        pendingInputs[player.uniqueId] = "content"
                        player.sendMessage(LanguageManager.getMessage(player, "please_enter_content"))
                    }

                    "save_post" -> {
                        val draft = pendingDrafts[player.uniqueId] ?: PostDraft()
                        val title = draft.title ?: LanguageManager.getMessage(player, "no_title")
                        val content = draft.content ?: LanguageManager.getMessage(player, "no_content")
                        pendingPreview[player.uniqueId] = Pair(title, content)
                        openConfirmationScreen(player, "submit")
                    }

                    "cancel_post" -> openConfirmationScreen(player, "cancel")
                    "back_button" -> openMainBoard(player)
                }
            }

            LanguageManager.getMessage(player, "confirmation") -> {
                event.isCancelled = true
                when (customId) {
                    "confirm_yes" -> {
                        when (pendingConfirmations[player.uniqueId]) {
                            "submit" -> {
                                val draft = pendingDrafts[player.uniqueId] ?: PostDraft()

                                val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd_HH:mm:ss")
                                val currentDate = LocalDateTime.now().format(formatter)

                                val title = draft.title ?: LanguageManager.getMessage(player, "no_title")
                                val content = draft.content ?: LanguageManager.getMessage(player, "no_content")

                                val newPost = Post(
                                    id = UUID.randomUUID().toString(),
                                    title = title,
                                    author = player.uniqueId,
                                    content = content,
                                    date = currentDate
                                )

                                val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)

                                val updatedPosts = data.posts + newPost
                                val updatedPlayers = data.players.map {
                                    if (it.uuid == player.uniqueId) {
                                        it.copy(posts = it.posts + newPost.id)
                                    } else {
                                        it
                                    }
                                }

                                val updatedData = data.copy(posts = updatedPosts, players = updatedPlayers)
                                JsonUtil.saveToFile(updatedData, BulletinBoard.instance.dataFile)

                                player.sendMessage(
                                    LanguageManager.getMessage(
                                        player, "post_saved"
                                    )
                                        .append(title)
                                        .append(Component.text(", "))
                                        .append(content)
                                )

                                pendingDrafts.remove(player.uniqueId)
                            }

                            "cancel" -> {
                                pendingDrafts.remove(player.uniqueId)
                                player.closeInventory()
                                Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
                                    openMainBoard(player)
                                })
                            }
                        }
                        player.closeInventory()
                        pendingConfirmations.remove(player.uniqueId)
                    }

                    "confirm_no" -> {
                        player.closeInventory()
                        pendingConfirmations.remove(player.uniqueId)
                        playerOpeningConfirmation.remove(player.uniqueId)
                        openPostEditor(player)
                    }

                    "preview_of_post" -> {
                        val draft = pendingDrafts[player.uniqueId] ?: PostDraft()
                        val title = draft.title ?: LanguageManager.getMessage(player, "no_title")
                        val content = draft.content ?: LanguageManager.getMessage(player, "no_content")

                        player.closeInventory()
                        openPreview(player, title, content)
                    }
                }
            }

            LanguageManager.getMessage(player, "delete_confirmation") -> {
                event.isCancelled = true
                when (customId?.split(":")?.get(0)) {
                    "delete_confirm_yes" -> {
                        val postId = customId.split(":")[1]
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val updatedPosts = data.posts.filterNot { it.id == postId }
                        val updatedPlayers = data.players.map {
                            if (it.uuid == player.uniqueId) {
                                it.copy(posts = it.posts.filterNot { id -> id == postId })
                            } else {
                                it
                            }
                        }

                        val updatedData = data.copy(posts = updatedPosts, players = updatedPlayers)
                        JsonUtil.saveToFile(updatedData, BulletinBoard.instance.dataFile)

                        player.sendMessage(LanguageManager.getMessage(player, "post_deleted"))
                        player.closeInventory()
                        pendingDrafts.remove(player.uniqueId)
                        pendingConfirmations.remove(player.uniqueId)
                        openMyPosts(player)
                    }

                    "delete_confirm_no" -> {
                        player.closeInventory()
                        pendingConfirmations.remove(player.uniqueId)
                        playerOpeningConfirmation.remove(player.uniqueId)
                        openMyPosts(player)
                    }

                    "back_button" -> openMyPosts(player)
                }
            }

            LanguageManager.getMessage(player, "select_post_to_delete") -> {
                event.isCancelled = true
                val currentPage = customId?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0

                when (customId?.split(":")?.getOrNull(0)) {
                    "prev_page" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        openDeletePostSelection(player, posts, currentPage - 1)
                    }

                    "next_page" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        openDeletePostSelection(player, posts, currentPage + 1)
                    }

                    "back_button" -> openMyPosts(player)
                    else -> if (customId != null) {
                        openDeleteConfirmationScreen(player, customId)
                    }
                }
            }

            LanguageManager.getMessage(player, "select_post_to_edit") -> {
                event.isCancelled = true
                val currentPage = customId?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0

                when (customId?.split(":")?.getOrNull(0)) {
                    "prev_page" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        openEditPostSelection(player, posts, currentPage - 1)
                    }

                    "next_page" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        openEditPostSelection(player, posts, currentPage + 1)
                    }

                    "back_button" -> openMyPosts(player)
                    else -> if (customId != null) {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val post = data.posts.find { it.id == customId }
                        if (post != null) {
                            pendingEdits[player.uniqueId] = post
                            val draft = PostDraft(
                                title = post.title,
                                content = post.content
                            )
                            pendingDrafts[player.uniqueId] = draft
                            openPostEditor(player)
                        }
                    }
                }
            }

            LanguageManager.getMessage(player, "select_post_to_edit") -> {
                event.isCancelled = true
                val currentPage = customId?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0

                when (customId?.split(":")?.getOrNull(0)) {
                    "prev_page" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        openEditPostSelection(player, posts, currentPage - 1)
                    }

                    "next_page" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        openEditPostSelection(player, posts, currentPage + 1)
                    }

                    "back_button" -> openMyPosts(player)
                    else -> if (customId != null) {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val post = data.posts.find { it.id == customId }
                        if (post != null) {
                            pendingEdits[player.uniqueId] = post
                            pendingDrafts[player.uniqueId] = PostDraft(post.title, post.content)
                            openEditPostEditor(player, post)
                        }
                    }
                }
            }
        }

        @EventHandler
        fun onInventoryClose(event: InventoryCloseEvent) {
            val p = event.player as? Player ?: return
            val iTitle = event.view.title()

            when (iTitle) {
                LanguageManager.getMessage(p, "post_editor") -> {
                    if (playerPreviewing[p.uniqueId] == true) {
                        playerPreviewing.remove(p.uniqueId)
                    } else if (playerOpeningConfirmation[p.uniqueId] == true) {
                        playerOpeningConfirmation.remove(p.uniqueId)
                    } else if (playerInputting[p.uniqueId] == true) {
                        playerInputting.remove(p.uniqueId)
                    } else {
                        pendingDrafts.remove(p.uniqueId)
                    }
                }

                LanguageManager.getMessage(p, "confirmation") -> {
                    pendingConfirmations.remove(p.uniqueId)
                    playerOpeningConfirmation.remove(p.uniqueId)
                }
            }
        }

        @EventHandler
        fun onPlayerChat(event: AsyncPlayerChatEvent) {
            val p = event.player

            if (pendingInputs.containsKey(p.uniqueId)) {
                event.isCancelled = true
                val inputType = pendingInputs[p.uniqueId]
                val input = event.message
                val draft = pendingDrafts.getOrDefault(p.uniqueId, PostDraft())

                val updatedDraft = if (inputType == "title") {
                    draft.copy(title = Component.text(input))
                } else {
                    draft.copy(content = Component.text(input))
                }

                pendingDrafts[p.uniqueId] = updatedDraft

                val uTitle = updatedDraft.title ?: LanguageManager.getMessage(p, "title")
                val uContent = updatedDraft.content ?: LanguageManager.getMessage(p, "content")

                Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
                    val postEditor = Bukkit.createInventory(null, 27, LanguageManager.getMessage(p, "post_editor"))
                    setGlassPane(postEditor, 0..8)
                    setGlassPane(postEditor, 18..26)
                    postEditor.setItem(11, createCustomItem(Material.PAPER, uTitle, customId = "post_title"))
                    postEditor.setItem(15, createCustomItem(Material.BOOK, uContent, customId = "post_content"))
                    postEditor.setItem(
                        18,
                        createCustomItem(
                            Material.RED_WOOL,
                            LanguageManager.getMessage(p, "cancel_post"),
                            customId = "cancel_post"
                        )
                    )
                    postEditor.setItem(
                        26,
                        createCustomItem(
                            Material.GREEN_WOOL,
                            LanguageManager.getMessage(p, "save_post"),
                            customId = "save_post"
                        )
                    )
                    p.openInventory(postEditor)
                })
                pendingInputs.remove(p.uniqueId)
                p.sendMessage(LanguageManager.getMessage(p, "input_set").replaceText { text ->
                    if (inputType != null) {
                        text.matchLiteral("{inputType}").replacement(inputType)
                    }
                }.replaceText { text -> text.matchLiteral("{input}").replacement(input) })
            }
        }

        @EventHandler
        fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
            val p = event.player
            val command = event.message

            if (command.equals("/bb previewclose", ignoreCase = true)) {
                event.isCancelled = true
                closePreview(p)
            }
        }
    }
}