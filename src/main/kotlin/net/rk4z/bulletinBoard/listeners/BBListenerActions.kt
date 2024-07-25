@file:Suppress("DuplicatedCode")

package net.rk4z.bulletinBoard.listeners

import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.manager.BulletinBoardManager
import net.rk4z.bulletinBoard.manager.LanguageManager
import net.rk4z.bulletinBoard.util.*
import net.rk4z.bulletinBoard.util.BulletinBoardUtil.createCustomItem
import net.rk4z.bulletinBoard.util.BulletinBoardUtil.setGlassPane
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object BBListenerActions {
    fun handleInventoryClick(player: Player, customId: String?, inventoryTitle: Component, state: PlayerState, event: InventoryClickEvent) {
        when (inventoryTitle) {
            LanguageManager.getMessage(player, "main_board") -> {
                event.isCancelled = true
                when (customId) {
                    "new_post" -> BulletinBoardManager.openPostEditor(player)
                    "all_posts" -> BulletinBoardManager.openAllPosts(player)
                    "my_posts" -> BulletinBoardManager.openMyPosts(player)
                }
            }

            LanguageManager.getMessage(player, "all_posts"), LanguageManager.getMessage(player, "my_posts") -> {
                event.isCancelled = true
                val currentPage = customId?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0

                when (customId?.split(":")?.getOrNull(0)) {
                    "prev_page" -> {
                        if (inventoryTitle == LanguageManager.getMessage(player, "all_posts")) {
                            BulletinBoardManager.openAllPosts(player, currentPage - 1)
                        } else {
                            BulletinBoardManager.openMyPosts(player, currentPage - 1)
                        }
                    }

                    "next_page" -> {
                        if (inventoryTitle == LanguageManager.getMessage(player, "all_posts")) {
                            BulletinBoardManager.openAllPosts(player, currentPage + 1)
                        } else {
                            BulletinBoardManager.openMyPosts(player, currentPage + 1)
                        }
                    }

                    "back_button" -> BulletinBoardManager.openMainBoard(player)

                    "delete_post" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        BulletinBoardManager.Selections.openDeletePostSelection(player, posts)
                    }

                    "edit_post" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        BulletinBoardManager.Selections.openEditPostSelection(player, posts)
                    }

                    else -> {
                        if (customId != null) {
                            val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                            val post = data.posts.find { it.id == customId }
                            if (post != null) {
                                player.closeInventory()
                                BulletinBoardManager.displayPost(player, post)
                            }
                        }
                    }
                }
            }

            LanguageManager.getMessage(player, "post_editor") -> {
                event.isCancelled = true
                when (customId) {
                    "post_title" -> {
                        state.isInputting = true
                        player.closeInventory()
                        state.inputType = "title"
                        player.sendMessage(LanguageManager.getMessage(player, "please_enter_title"))
                    }

                    "post_content" -> {
                        state.isInputting = true
                        player.closeInventory()
                        state.inputType = "content"
                        player.sendMessage(LanguageManager.getMessage(player, "please_enter_content"))
                    }

                    "save_post" -> {
                        val draft = state.draft ?: PostDraft()
                        val title = draft.title ?: LanguageManager.getMessage(player, "no_title")
                        val content = draft.content ?: LanguageManager.getMessage(player, "no_content")
                        state.preview = Pair(title, content)
                        BulletinBoardManager.Confirmations.openConfirmationScreen(player, "submit")
                    }

                    "cancel_post" -> BulletinBoardManager.Confirmations.openConfirmationScreen(player, "cancel")
                }
            }

            LanguageManager.getMessage(player, "post_editor_for_edit") -> {
                event.isCancelled = true
                when (customId) {
                    "edit_post_title" -> {
                        state.isEditInputting = true
                        player.closeInventory()
                        state.editInputType = "title"
                        player.sendMessage(LanguageManager.getMessage(player, "please_enter_title_for_edit"))
                    }

                    "edit_post_content" -> {
                        state.isEditInputting = true
                        player.closeInventory()
                        state.editInputType = "content"
                        player.sendMessage(LanguageManager.getMessage(player, "please_enter_content_for_edit"))
                    }

                    "save_edit" -> {
                        val eDraft = state.editDraft ?: EditPostData()
                        val eTitle = eDraft.title ?: LanguageManager.getMessage(player, "no_title")
                        val eContent = eDraft.content ?: LanguageManager.getMessage(player, "no_content")
                        state.preview = Pair(eTitle, eContent)
                        BulletinBoardManager.Confirmations.openConfirmationScreen(player, "edit_submit")
                    }

                    "cancel_edit" -> BulletinBoardManager.Confirmations.openConfirmationScreen(player, "cancel")
                }
            }

            LanguageManager.getMessage(player, "confirmation") -> {
                event.isCancelled = true
                when (customId) {
                    "confirm_yes" -> {
                        when (state.confirmationType) {
                            "submit" -> {
                                saveNewPost(player)
                            }

                            "edit_submit" -> {
                                saveEditedPost(player)
                            }

                            "cancel" -> {
                                state.draft = null
                                player.closeInventory()
                                Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
                                    BulletinBoardManager.openMainBoard(player)
                                })
                            }
                        }
                        player.closeInventory()
                        state.confirmationType = null
                    }

                    "confirm_no" -> {
                        when (state.confirmationType) {
                            "submit" -> {
                                player.closeInventory()
                                state.confirmationType = null
                                state.isOpeningConfirmation = false
                                BulletinBoardManager.openPostEditor(player)
                            }

                            "edit_submit" -> {
                                player.closeInventory()
                                state.confirmationType = null
                                state.isOpeningConfirmation = false
                                val currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy:MM:dd_HH:mm:ss"))
                                BulletinBoardManager.openPostEditorForEdit(player, state.editDraft!!.toPost(player.uniqueId, currentDate))
                            }
                        }
                    }

                    "preview_of_post" -> {
                        val draft = state.draft ?: PostDraft()
                        val title = draft.title ?: LanguageManager.getMessage(player, "no_title")
                        val content = draft.content ?: LanguageManager.getMessage(player, "no_content")

                        player.closeInventory()
                        BulletinBoardManager.Previews.openPreview(player, title, content)
                    }

                    "preview_of_edit" -> {
                        val draft = state.editDraft ?: EditPostData()
                        val title = draft.title ?: LanguageManager.getMessage(player, "no_title")
                        val content = draft.content ?: LanguageManager.getMessage(player, "no_content")

                        player.closeInventory()
                        BulletinBoardManager.Previews.openPreview(player, title, content)
                    }
                }
            }

            LanguageManager.getMessage(player, "delete_confirmation") -> {
                event.isCancelled = true
                when (customId?.split(":")?.get(0)) {
                    "delete_confirm_yes" -> {
                        val postId = customId.split(":")[1]
                        deletePost(player, postId)
                    }

                    "delete_confirm_no" -> {
                        player.closeInventory()
                        state.confirmationType = null
                        state.isOpeningConfirmation = false
                        BulletinBoardManager.openMyPosts(player)
                    }

                    "back_button" -> BulletinBoardManager.openMyPosts(player)
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
                        BulletinBoardManager.Selections.openDeletePostSelection(player, posts, currentPage - 1)
                    }

                    "next_page" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        BulletinBoardManager.Selections.openDeletePostSelection(player, posts, currentPage + 1)
                    }

                    "back_button" -> BulletinBoardManager.openMyPosts(player)
                    else -> if (customId != null) {
                        BulletinBoardManager.Confirmations.openDeleteConfirmationScreen(player, customId)
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
                        BulletinBoardManager.Selections.openEditPostSelection(player, posts, currentPage - 1)
                    }

                    "next_page" -> {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        BulletinBoardManager.Selections.openEditPostSelection(player, posts, currentPage + 1)
                    }

                    "back_button" -> BulletinBoardManager.openMyPosts(player)
                    else -> if (customId != null) {
                        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
                        val playerData = data.players.find { it.uuid == player.uniqueId } ?: return
                        val posts = playerData.posts.mapNotNull { postId -> data.posts.find { it.id == postId } }
                        val post = posts.find { it.id == customId } ?: return
                        BulletinBoardManager.openPostEditorForEdit(player, post)
                    }
                }
            }
        }
    }

    fun handleInventoryClose(player: Player, inventoryTitle: Component, state: PlayerState) {
        when (inventoryTitle) {
            LanguageManager.getMessage(player, "post_editor"), LanguageManager.getMessage(player, "post_editor_for_edit") -> {
                if (state.isPreviewing) {
                    state.isPreviewing = false
                } else if (state.isOpeningConfirmation) {
                    state.isOpeningConfirmation = false
                } else if (state.isInputting) {
                    state.isInputting = false
                } else {
                    state.draft = null
                }
            }

            LanguageManager.getMessage(player, "confirmation") -> {
                state.confirmationType = null
                state.isOpeningConfirmation = false
            }
        }
    }

    fun handlePlayerChat(player: Player, message: String, state: PlayerState, event: AsyncPlayerChatEvent) {
        if (state.inputType != null) {
            event.isCancelled = true
            val inputType = state.inputType
            val draft = state.draft ?: PostDraft()

            val updatedDraft = if (inputType == "title") {
                draft.copy(title = Component.text(message))
            } else {
                draft.copy(content = Component.text(message))
            }

            state.draft = updatedDraft

            val uTitle = updatedDraft.title ?: LanguageManager.getMessage(player, "title")
            val uContent = updatedDraft.content ?: LanguageManager.getMessage(player, "content")

            Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
                val postEditor = Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "post_editor"))
                setGlassPane(postEditor, 0..8)
                setGlassPane(postEditor, 18..26)
                setGlassPane(postEditor, listOf(9, 10, 12, 13, 14, 16, 17))
                postEditor.setItem(11, createCustomItem(Material.PAPER, uTitle, customId = "post_title"))
                postEditor.setItem(15, createCustomItem(Material.BOOK, uContent, customId = "post_content"))
                postEditor.setItem(
                    19,
                    createCustomItem(
                        Material.RED_WOOL,
                        LanguageManager.getMessage(player, "cancel_post"),
                        customId = "cancel_post"
                    )
                )
                postEditor.setItem(
                    25,
                    createCustomItem(
                        Material.GREEN_WOOL,
                        LanguageManager.getMessage(player, "save_post"),
                        customId = "save_post"
                    )
                )
                player.openInventory(postEditor)
            })
            state.inputType = null
            player.sendMessage(
                LanguageManager.getMessage(player, "input_set").replaceText { text ->
                    inputType?.let { text.matchLiteral("{inputType}").replacement(it) }
                }.replaceText { text -> text.matchLiteral("{input}").replacement(message) }
            )
        }
        else if (state.editInputType != null) {
            event.isCancelled = true
            val inputType = state.editInputType
            val draft = state.editDraft ?: EditPostData()

            val updatedDraft = if (inputType == "title") {
                draft.copy(title = Component.text(message))
            } else {
                draft.copy(content = Component.text(message))
            }

            state.editDraft = updatedDraft

            val uTitle = updatedDraft.title ?: LanguageManager.getMessage(player, "title")
            val uContent = updatedDraft.content ?: LanguageManager.getMessage(player, "content")

            Bukkit.getScheduler().runTask(BulletinBoard.instance, Runnable {
                val postEditor = Bukkit.createInventory(null, 27, LanguageManager.getMessage(player, "post_editor_for_edit"))
                setGlassPane(postEditor, 0..8)
                setGlassPane(postEditor, 18..26)
                setGlassPane(postEditor, listOf(9, 10, 12, 13, 14, 16, 17))
                postEditor.setItem(11, createCustomItem(Material.PAPER, uTitle, customId = "edit_post_title"))
                postEditor.setItem(15, createCustomItem(Material.BOOK, uContent, customId = "edit_post_content"))
                postEditor.setItem(
                    19,
                    createCustomItem(
                        Material.RED_WOOL,
                        LanguageManager.getMessage(player, "cancel_edit"),
                        customId = "cancel_edit"
                    )
                )
                postEditor.setItem(
                    25,
                    createCustomItem(
                        Material.GREEN_WOOL,
                        LanguageManager.getMessage(player, "save_edit"),
                        customId = "save_edit"
                    )
                )
                player.openInventory(postEditor)
            })
            state.editInputType = null
            player.sendMessage(
                LanguageManager.getMessage(player, "input_set").replaceText { text ->
                    inputType?.let { text.matchLiteral("{inputType}").replacement(it) }
                }.replaceText { text -> text.matchLiteral("{input}").replacement(message) }
            )
        }
    }

    fun handlePlayerCommandPreprocess(player: Player, command: String, event: PlayerCommandPreprocessEvent) {
        if (command.equals("/bb previewclose", ignoreCase = true)) {
            event.isCancelled = true
            BulletinBoardManager.Previews.closePreview(player)
        }
    }

    private fun saveNewPost(player: Player) {
        val state = BulletinBoardManager.getPlayerState(player.uniqueId)
        val draft = state.draft ?: return

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
            ).append(title).append(Component.text(", ")).append(content)
        )

        state.draft = null
    }

    private fun saveEditedPost(player: Player) {
        val state = BulletinBoardManager.getPlayerState(player.uniqueId)
        val draft = state.editDraft ?: return
        val id = draft.id ?: return
        val title = draft.title ?: LanguageManager.getMessage(player, "no_title")
        val content = draft.content ?: LanguageManager.getMessage(player, "no_content")

        val data = JsonUtil.loadFromFile(BulletinBoard.instance.dataFile)
        val postIndex = data.posts.indexOfFirst { it.id == id }

        if (postIndex != -1) {
            val post = data.posts[postIndex]
            val updatedPost = post.copy(title = title, content = content)
            val updatedPosts = data.posts.toMutableList().apply {
                set(postIndex, updatedPost)
            }
            val updatedData = data.copy(posts = updatedPosts)
            JsonUtil.saveToFile(updatedData, BulletinBoard.instance.dataFile)

            player.sendMessage(
                LanguageManager.getMessage(
                    player, "post_edited"
                ).append(title).append(Component.text(", ")).append(content)
            )
        } else {
            player.sendMessage(LanguageManager.getMessage(player, "post_not_found"))
        }

        state.editDraft = null
    }

    private fun deletePost(player: Player, postId: String) {
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
        BulletinBoardManager.openMyPosts(player)
    }
}
