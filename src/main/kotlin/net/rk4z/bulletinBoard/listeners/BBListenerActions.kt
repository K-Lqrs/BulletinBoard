@file:Suppress("DEPRECATION")

package net.rk4z.bulletinBoard.listeners

import net.kyori.adventure.text.Component
import net.rk4z.beacon.IEventHandler
import net.rk4z.beacon.Priority
import net.rk4z.beacon.handler
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.BulletinBoard.Companion.database
import net.rk4z.bulletinBoard.BulletinBoard.Companion.runTask
import net.rk4z.bulletinBoard.BulletinBoard.Companion.runTaskAsynchronous
import net.rk4z.bulletinBoard.events.BulletinBoardClickEvent
import net.rk4z.bulletinBoard.events.BulletinBoardCloseEvent
import net.rk4z.bulletinBoard.events.BulletinBoardOnChatEvent
import net.rk4z.bulletinBoard.managers.BBCommandManager
import net.rk4z.bulletinBoard.managers.BulletinBoardManager
import net.rk4z.bulletinBoard.managers.LanguageManager
import net.rk4z.bulletinBoard.utils.*
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*

@Suppress("unused", "DuplicatedCode")
class BBListenerActions : IEventHandler {
    private val p = BulletinBoard.instance
    private val bbCommand = BBCommandManager()

    val onBBClick = handler<BulletinBoardClickEvent>(
        priority = Priority.HIGHEST
    ) {
        val player = it.player
        val event = it.event
        val state = it.state
        val customId = it.customId
        when (val inventory = it.inventoryTitle) {
            LanguageManager.getMessage(player, MessageKey.MAIN_BOARD) -> {
                event.isCancelled = true
                handleMainBoardClick(player, customId)
            }

            LanguageManager.getMessage(player, MessageKey.MY_POSTS),
            LanguageManager.getMessage(player, MessageKey.ALL_POSTS),
            LanguageManager.getMessage(player, MessageKey.DELETED_POSTS) -> {
                event.isCancelled = true
                handlePostsClick(player, inventory, customId)
            }

            LanguageManager.getMessage(player, MessageKey.POST_EDITOR) -> {
                event.isCancelled = true
                handlePostEditorClick(player, state, customId)
            }

            LanguageManager.getMessage(player, MessageKey.SAVE_POST_CONFIRMATION) -> {
                event.isCancelled = true
                handleSavePostConfirmation(player, state, customId)
            }

            LanguageManager.getMessage(player, MessageKey.DELETE_POST_SELECTION) -> {
                event.isCancelled = true
                handleDeletePostSelection(player, customId, state)
            }

            LanguageManager.getMessage(player, MessageKey.DELETE_POST_CONFIRMATION) -> {
                event.isCancelled = true
                handleDeletePostConfirmation(player, customId, state)
            }

            LanguageManager.getMessage(player, MessageKey.CANCEL_POST_CONFIRMATION) -> {
                event.isCancelled = true
                handleCancelPostConfirmation(player, state, customId)
            }

            LanguageManager.getMessage(player, MessageKey.DELETE_POST_PERMANENTLY_SELECTION) -> {
                event.isCancelled = true
                handleDeletePostPermanentSelection(player, customId, state)
            }

            LanguageManager.getMessage(player, MessageKey.EDIT_POST_SELECTION) -> {
                event.isCancelled = true
                handleEditPostSelection(player, customId, state)
            }
        }
    }

    val onChat = handler<BulletinBoardOnChatEvent>(
        priority = Priority.LOWEST
    ) {
        val player = it.player
        val state = it.state
        val event = it.event

        if (state.inputType != null || state.editInputType != null) {
            event.isCancelled = true
            event.recipients.clear()

            when (val inputType = state.inputType ?: state.editInputType) {
                InputType.TITLE -> updateDraft(player, event, state, inputType, event.message, true)
                InputType.CONTENT -> updateDraft(player, event, state, inputType, event.message, false)
                else -> return@handler
            }
        }
    }

    val onInvClose = handler<BulletinBoardCloseEvent>(
        priority = Priority.LOWEST
    ) {
        val player = it.player
        val title = it.inventoryTitle
        val state = it.state

        when (title) {
            LanguageManager.getMessage(player, MessageKey.POST_EDITOR) -> handlePostEditorClose(state)

            LanguageManager.getMessage(player, MessageKey.SAVE_POST_CONFIRMATION),
            LanguageManager.getMessage(player, MessageKey.CANCEL_POST_CONFIRMATION),
            LanguageManager.getMessage(player, MessageKey.DELETE_POST_CONFIRMATION),
            LanguageManager.getMessage(player, MessageKey.DELETE_POST_PERMANENTLY_CONFIRMATION) -> handleConfirmationClose(state)
        }
    }

    private fun handleMainBoardClick(player: Player, customId: String?) {
        when (customId) {
            CustomID.NEW_POST.name -> BulletinBoardManager.openPostEditor(player)
            CustomID.ALL_POSTS.name -> BulletinBoardManager.openAllPosts(player)
            CustomID.MY_POSTS.name -> BulletinBoardManager.openMyPosts(player)
            CustomID.DELETED_POSTS.name -> BulletinBoardManager.openDeletedPosts(player)
            CustomID.ABOUT_PLUGIN.name -> {
                runTask(p) {
                    player.closeInventory()
                }
                bbCommand.displayAbout(player)
            }
            CustomID.HELP.name -> {
                runTask(p) {
                    player.closeInventory()
                }
                bbCommand.displayHelp(player)
            }
        }
    }

    private fun handlePostsClick(player: Player, inventory: Component, customId: String?) {
        val currentPage = customId?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0

        when (customId?.split(":")?.getOrNull(0)) {
            CustomID.PREV_PAGE.name -> openPage(player, inventory, currentPage - 1)
            CustomID.NEXT_PAGE.name -> openPage(player, inventory, currentPage + 1)
            CustomID.BACK_BUTTON.name -> {
                if (inventory == LanguageManager.getMessage(player, MessageKey.ALL_POSTS) ||
                    inventory == LanguageManager.getMessage(player, MessageKey.MY_POSTS) ||
                    inventory == LanguageManager.getMessage(player, MessageKey.DELETED_POSTS)
                ) {
                    BulletinBoardManager.openMainBoard(player)
                }
            }
            CustomID.DELETE_POST.name -> BulletinBoardManager.openDeletePostSelection(player)
            CustomID.DELETE_POST_PERMANENTLY.name -> BulletinBoardManager.openDeletePostPermanentlySelection(player)
            CustomID.EDIT_POST.name -> BulletinBoardManager.openEditPostSelection(player)
            else -> {
                if (inventory != LanguageManager.getMessage(player, MessageKey.DELETED_POSTS) && customId != null) {
                    val post = database.getPost(customId)
                    post?.let { BulletinBoardManager.displayPost(player, it) }
                } else if (inventory == LanguageManager.getMessage(player, MessageKey.DELETED_POSTS) && customId != null) {
                    val post = database.getDeletedPostsByID(customId)
                    post?.let { BulletinBoardManager.displayPost(player, it) }
                }
            }
        }
    }

    private fun openPage(player: Player, inventory: Component, page: Int) {
        when (inventory) {
            LanguageManager.getMessage(player, MessageKey.ALL_POSTS) -> BulletinBoardManager.openAllPosts(player, page)
            LanguageManager.getMessage(player, MessageKey.MY_POSTS) -> BulletinBoardManager.openMyPosts(player, page)
            LanguageManager.getMessage(
                player,
                MessageKey.DELETED_POSTS
            ) -> BulletinBoardManager.openDeletedPosts(player, page)
            else -> BulletinBoardManager.openDeletedPosts(player, page)
        }
    }

    private fun handlePostEditorClick(player: Player, state: PlayerState, customId: String?) {
        when (customId) {
            CustomID.POST_TITLE.name -> beginInput(player, state, InputType.TITLE)
            CustomID.POST_CONTENT.name -> beginInput(player, state, InputType.CONTENT)
            CustomID.CANCEL_POST.name -> BulletinBoardManager.openConfirmation(player, ConfirmationType.CANCEL_POST)
            CustomID.SAVE_POST.name -> prepareSavePost(player, state)
        }
    }

    private fun handleEditPostSelection(player: Player, customId: String?, state: PlayerState) {
        when (customId) {
            CustomID.BACK_BUTTON.name -> BulletinBoardManager.openMyPosts(player)
            else -> {
                if (customId != null) {
                    val post = database.getPost(customId)
                    post?.let { BulletinBoardManager.openPostEditorForEdit(player, post) }
                    state.selectedEditingPostId = customId
                }
            }
        }
    }

    private fun beginInput(player: Player, state: PlayerState, inputType: InputType) {
        state.isInputting = true
        state.inputType = inputType
        runTask(p) {
            player.closeInventory()
        }
        player.sendMessage(LanguageManager.getMessage(player, if (inputType == InputType.TITLE) MessageKey.PLEASE_ENTER_TITLE else MessageKey.PLEASE_ENTER_CONTENT))
    }

    private fun prepareSavePost(player: Player, state: PlayerState) {
        val draft = state.draft ?: PostDraft()
        val title = draft.title ?: LanguageManager.getMessage(player, MessageKey.NO_TITLE)
        val content = draft.content ?: LanguageManager.getMessage(player, MessageKey.NO_CONTENT)
        state.preview = Pair(title, content)
        BulletinBoardManager.openConfirmation(player, ConfirmationType.SAVE_POST)
    }

    private fun handleSavePostConfirmation(player: Player, state: PlayerState, customId: String?) {
        if (customId == CustomID.CONFIRM_SAVE_POST.name) {
            val draft = state.draft
            if (draft == null) {
                savePostStateNull(player)
            } else {
                val post = Post(
                    id = ShortUUID.randomUUID().toString(),
                    title = draft.title ?: LanguageManager.getMessage(player, MessageKey.NO_TITLE),
                    author = player.uniqueId,
                    content = draft.content ?: LanguageManager.getMessage(player, MessageKey.NO_CONTENT),
                    date = Date()
                )
                runTaskAsynchronous(p) {
                    database.insertPost(post)
                    state.draft = null
                }
                runTask(p) {
                    player.closeInventory()
                    player.sendMessage(LanguageManager.getMessage(player, MessageKey.POST_SAVED))
                }

            }
        }
    }

    private fun handleDeletePostSelection(player: Player, customId: String?, state: PlayerState) {
        when (customId) {
            CustomID.BACK_BUTTON.name -> BulletinBoardManager.openMyPosts(player)
            else -> {
                if (customId != null) {
                    val post = database.getPost(customId)
                    post?.let { BulletinBoardManager.openConfirmation(player, ConfirmationType.DELETING_POST) }
                    state.selectedDeletingPostId = customId
                }
            }
        }
    }

    private fun handleDeletePostPermanentSelection(player: Player, customId: String?, state: PlayerState) {
        when (customId) {
            CustomID.BACK_BUTTON.name -> BulletinBoardManager.openDeletedPosts(player)
            else -> {
                if (customId != null) {
                    val post = database.getDeletedPostsByID(customId)
                    post?.let {
                        BulletinBoardManager.openConfirmation(
                            player,
                            ConfirmationType.DELETING_POST_PERMANENTLY
                        )
                    }
                    state.selectedDeletingPostId = customId
                }
            }
        }
    }

    private fun handleDeletePostConfirmation(player: Player, customId: String?, state: PlayerState) {
        when (customId) {
            CustomID.CONFIRM_DELETE_POST.name -> {
                val selectedDeletingPostId = state.selectedDeletingPostId
                if (selectedDeletingPostId == null) {
                    deletePostStateNull(player)
                    return
                }
                database.deletePost(selectedDeletingPostId)
                state.selectedDeletingPostId = null
                runTask(p) {
                    player.closeInventory()
                    player.sendMessage(LanguageManager.getMessage(player, MessageKey.POST_DELETED))
                }
            }
        }
    }

    private fun handleCancelPostConfirmation(player: Player, state: PlayerState, customId: String?) {
        when (customId) {
            CustomID.CONFIRM_CANCEL_POST.name -> {
                state.draft = null
                runTask(p) {
                    player.closeInventory()
                    player.sendMessage(LanguageManager.getMessage(player, MessageKey.CANCELLED_POST))
                }
            }

            CustomID.CONTINUE_POST.name -> {
                BulletinBoardManager.openPostEditor(player)
            }
        }
    }

    private fun updateDraft(
        player: Player,
        event: AsyncPlayerChatEvent,
        state: PlayerState,
        inputType: InputType,
        message: String,
        isTitle: Boolean
    ) {
        event.isCancelled = true
        if (state.inputType != null) {
            val draft = state.draft ?: PostDraft()
            val updatedDraft = if (isTitle) draft.copy(title = Component.text(message)) else draft.copy(content = Component.text(message))
            state.draft = updatedDraft
            state.inputType = null

            val uTitle = updatedDraft.title ?: LanguageManager.getMessage(player, MessageKey.NO_TITLE)
            val uContent = updatedDraft.content ?: LanguageManager.getMessage(player, MessageKey.NO_CONTENT)

            val postEditor = BulletinBoardManager.createPostEditorInventory(
                player,
                uTitle,
                uContent,
                LanguageManager.getMessage(player, MessageKey.POST_EDITOR),
                CustomID.POST_TITLE,
                CustomID.POST_CONTENT,
                CustomID.CANCEL_POST,
                CustomID.SAVE_POST
            )

            runTask(p) {
                player.openInventory(postEditor)
            }
            player.sendMessage(
                LanguageManager.getMessage(player, MessageKey.INPUT_SET)
                    .replaceText { text -> text.matchLiteral("{inputType}").replacement(inputType.name.lowercase()) }
                    .replaceText { text -> text.matchLiteral("{input}").replacement(message) }
            )
        } else {
            val editDraft = state.editDraft ?: EditPostData()
            val updatedEditDraft = if (isTitle) editDraft.copy(title = Component.text(message)) else editDraft.copy(content = Component.text(message))
            state.editDraft = updatedEditDraft
            state.editInputType = null

            val uTitle = updatedEditDraft.title ?: LanguageManager.getMessage(player, MessageKey.NO_TITLE)
            val uContent = updatedEditDraft.content ?: LanguageManager.getMessage(player, MessageKey.NO_CONTENT)

            val postEditor = BulletinBoardManager.createPostEditorInventory(
                player,
                uTitle,
                uContent,
                LanguageManager.getMessage(player, MessageKey.POST_EDITOR_FOR_EDIT),
                CustomID.EDIT_POST_TITLE,
                CustomID.EDIT_POST_CONTENT,
                CustomID.CANCEL_EDIT,
                CustomID.SAVE_EDIT
            )

            runTask(p) {
                player.openInventory(postEditor)
            }
            player.sendMessage(
                LanguageManager.getMessage(player, MessageKey.INPUT_SET)
                    .replaceText { text -> text.matchLiteral("{inputType}").replacement(inputType.name.lowercase()) }
                    .replaceText { text -> text.matchLiteral("{input}").replacement(message) }
            )
        }
    }

    private fun handlePostEditorClose(state: PlayerState) {
        when {
            state.isInputting == true -> state.isInputting = null
            state.isOpeningConfirmation == true -> state.isOpeningConfirmation = null
            state.isPreviewing == true -> state.isPreviewing = null
            else -> state.clear()
        }
    }

    private fun handleConfirmationClose(state: PlayerState) {
        state.isOpeningConfirmation = null
        state.isChoosingConfirmationAnswer = null
    }

    private fun savePostStateNull(player: Player) {
        runTask(p) {
            player.closeInventory()
            player.sendMessage(LanguageManager.getMessage(player, MessageKey.WHEN_POST_DRAFT_NULL))
        }
    }

    private fun deletePostStateNull(player: Player) {
        runTask(p) {
            player.closeInventory()
            player.sendMessage(LanguageManager.getMessage(player, MessageKey.WHEN_DELETE_POST_NULL))
        }
    }
}
