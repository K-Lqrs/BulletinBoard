package net.rk4z.bulletinBoard.listeners

import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.BulletinBoard.Companion.database
import net.rk4z.bulletinBoard.BulletinBoard.Companion.runTask
import net.rk4z.bulletinBoard.BulletinBoard.Companion.runTaskAsynchronous
import net.rk4z.bulletinBoard.managers.BBCommandManager
import net.rk4z.bulletinBoard.managers.BulletinBoardManager
import net.rk4z.bulletinBoard.managers.LanguageManager
import net.rk4z.bulletinBoard.utils.*
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent
import java.util.*

object BBListenerActions {
    private val p = BulletinBoard.instance
    private val bbCommand = BBCommandManager()

    internal fun handleMainBoardClick(player: Player, customId: String?) {
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

    internal fun handlePostsClick(player: Player, inventory: Component, customId: String?) {
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
            CustomID.RESTORE_POST.name -> BulletinBoardManager.openRestorePostSelection(player)
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

    internal fun handleSelectionClick(player: Player, inventory: Component, customId: String?, state: PlayerState) {
        val currentPage = customId?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0

        when (customId?.split(":")?.getOrNull(0)) {
            CustomID.PREV_PAGE.name -> openPage(player, inventory, currentPage - 1)
            CustomID.NEXT_PAGE.name -> openPage(player, inventory, currentPage + 1)
            CustomID.BACK_BUTTON.name -> {
                if (inventory == LanguageManager.getMessage(player, MessageKey.DELETE_POST_SELECTION) ||
                    inventory == LanguageManager.getMessage(player, MessageKey.EDIT_POST_SELECTION)
                ) {
                    BulletinBoardManager.openMyPosts(player)
                }
                if (inventory == LanguageManager.getMessage(player, MessageKey.DELETE_POST_PERMANENTLY_SELECTION)||
                    inventory == LanguageManager.getMessage(player, MessageKey.RESTORE_POST_SELECTION)) {
                    BulletinBoardManager.openDeletedPosts(player)
                }
            }
        }

        when (inventory) {
            LanguageManager.getMessage(player, MessageKey.DELETE_POST_PERMANENTLY_SELECTION) -> {
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
            LanguageManager.getMessage(player, MessageKey.EDIT_POST_SELECTION) -> {
                if (customId != null) {
                    val post = database.getPost(customId)
                    post?.let { BulletinBoardManager.openPostEditorForEdit(player, post) }
                    state.selectedEditingPostId = customId
                }
            }
            LanguageManager.getMessage(player, MessageKey.DELETE_POST_SELECTION) -> {
                if (customId != null) {
                    val post = database.getPost(customId)
                    post?.let { BulletinBoardManager.openConfirmation(player, ConfirmationType.DELETING_POST) }
                    state.selectedDeletingPostId = customId
                }
            }
            LanguageManager.getMessage(player, MessageKey.RESTORE_POST_SELECTION) -> {
                if (customId != null) {
                    val post = database.getDeletedPostsByID(customId)
                    post?.let {
                        database.restorePost(customId)
                        runTask(p) {
                            player.closeInventory()
                            player.sendMessage(LanguageManager.getMessage(player, MessageKey.POST_RESTORED))
                        }
                    }
                }
            }
        }
    }

    private fun openPage(player: Player, inventory: Component, page: Int) {
        when (inventory) {
            LanguageManager.getMessage(player, MessageKey.ALL_POSTS) -> BulletinBoardManager.openAllPosts(player, page)
            LanguageManager.getMessage(player, MessageKey.MY_POSTS) -> BulletinBoardManager.openMyPosts(player, page)
            LanguageManager.getMessage(player, MessageKey.DELETED_POSTS) -> BulletinBoardManager.openDeletedPosts(player, page)
            LanguageManager.getMessage(player, MessageKey.DELETE_POST_SELECTION) -> BulletinBoardManager.openDeletePostSelection(player, page)
            LanguageManager.getMessage(player, MessageKey.EDIT_POST_SELECTION) -> BulletinBoardManager.openEditPostSelection(player, page)
        }
    }

    internal fun handlePostEditorClick(player: Player, state: PlayerState, customId: String?) {
        when (customId) {
            CustomID.POST_TITLE.name -> beginInput(player, state, InputType.TITLE)
            CustomID.POST_CONTENT.name -> beginInput(player, state, InputType.CONTENT)
            CustomID.CANCEL_POST.name -> BulletinBoardManager.openConfirmation(player, ConfirmationType.CANCEL_POST)
            CustomID.SAVE_POST.name -> prepareSavePost(player, state)
        }
    }

    private fun beginInput(player: Player, state: PlayerState, inputType: InputType) {
        state.isInputting = true
        state.inputType = inputType
        runTask(p) {
            player.closeInventory()
        }
        player.sendMessage(
            LanguageManager.getMessage(
                player,
                if (inputType == InputType.TITLE) MessageKey.PLEASE_ENTER_TITLE else MessageKey.PLEASE_ENTER_CONTENT
            )
        )
    }

    private fun prepareSavePost(player: Player, state: PlayerState) {
        val draft = state.draft ?: PostDraft()
        val title = draft.title ?: LanguageManager.getMessage(player, MessageKey.NO_TITLE)
        val content = draft.content ?: LanguageManager.getMessage(player, MessageKey.NO_CONTENT)
        state.preview = Pair(title, content)
        BulletinBoardManager.openConfirmation(player, ConfirmationType.SAVE_POST)
    }

    internal fun handleSavePostConfirmation(player: Player, state: PlayerState, customId: String?) {
        if (customId == CustomID.CONFIRM_SAVE_POST.name) {
            val draft = state.draft
            if (draft == null) {
                savePostStateNull(player)
                state.clear()
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
                    state.preview = null
                    state.confirmationType = null
                }
                runTask(p) {
                    player.closeInventory()
                    player.sendMessage(LanguageManager.getMessage(player, MessageKey.POST_SAVED))
                }

            }
        }
        if (customId == CustomID.CANCEL_CONFIRM_SAVE_POST.name) {
            state.preview = null
            BulletinBoardManager.openPostEditor(player)
        }
    }

    internal fun handleDeletePostConfirmation(player: Player, customId: String?, state: PlayerState) {
        when (customId) {
            CustomID.CONFIRM_DELETE_POST.name -> {
                val selectedDeletingPostId = state.selectedDeletingPostId
                if (selectedDeletingPostId == null) {
                    deletePostStateNull(player)
                    return
                }
                database.deletePost(selectedDeletingPostId)
                runTask(p) {
                    player.closeInventory()
                    state.clear()
                    player.sendMessage(LanguageManager.getMessage(player, MessageKey.POST_DELETED))
                }
            }
        }
    }

    internal fun handleCancelPostConfirmation(player: Player, state: PlayerState, customId: String?) {
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

    internal fun handleDeletePostPermanentlyConfirmation(player: Player, state: PlayerState, customId: String?) {
        when (customId) {
            CustomID.CONFIRM_DELETE_POST_PERMANENTLY.name -> {
                val selectedDeletingPostId = state.selectedDeletingPostId
                if (selectedDeletingPostId == null) {
                    deletePostStateNull(player)
                    return
                }
                database.deletePostPermanently(selectedDeletingPostId)
                runTask(p) {
                    player.closeInventory()
                    state.clear()
                    player.sendMessage(LanguageManager.getMessage(player, MessageKey.POST_DELETED_PERMANENTLY))
                }
            }
            CustomID.CANCEL_DELETE_POST_PERMANENTLY.name -> {
                state.clear()
                BulletinBoardManager.openDeletedPosts(player)
            }
        }
    }

    internal fun updateDraft(
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
            val updatedDraft =
                if (isTitle) draft.copy(title = Component.text(message)) else draft.copy(content = Component.text(message))
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
            val updatedEditDraft = if (isTitle) editDraft.copy(title = Component.text(message)) else editDraft.copy(
                content = Component.text(message)
            )
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

    internal fun handlePostEditorClose(state: PlayerState) {
        when {
            state.isInputting == true -> state.isInputting = null
            state.isOpeningConfirmation == true -> state.isOpeningConfirmation = null
            state.isPreviewing == true -> state.isPreviewing = null
            else -> state.clear()
        }
    }

    internal fun handleConfirmationClose(state: PlayerState) {
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