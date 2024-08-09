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
import net.rk4z.bulletinBoard.managers.BulletinBoardManager
import net.rk4z.bulletinBoard.managers.LanguageManager
import net.rk4z.bulletinBoard.utils.*
import org.bukkit.entity.Player
import java.util.*

@Suppress("unused", "DuplicatedCode")
class BBListenerActions : IEventHandler {
    private val p = BulletinBoard.instance

    val onBBClick = handler<BulletinBoardClickEvent>(priority = Priority.HIGHEST) {
        val player = it.player
        val event = it.event
        val state = it.state
        val customId = it.customId
        val inventory = it.inventoryTitle

        event.isCancelled = true

        when (inventory) {
            LanguageManager.getMessage(player, MessageKey.MAIN_BOARD) -> handleMainBoardClick(player, customId)
            LanguageManager.getMessage(player, MessageKey.MY_POSTS),
            LanguageManager.getMessage(player, MessageKey.ALL_POSTS),
            LanguageManager.getMessage(player, MessageKey.DELETED_POSTS) -> handlePostsClick(player, inventory, customId)
            LanguageManager.getMessage(player, MessageKey.POST_EDITOR) -> handlePostEditorClick(player, state, customId)
            LanguageManager.getMessage(player, MessageKey.SAVE_POST_CONFIRMATION) -> handleSavePostConfirmation(player, state, customId)
            LanguageManager.getMessage(player, MessageKey.DELETE_POST_SELECTION) -> handleDeletePostSelection(player, customId)
        }
    }

    val onChat = handler<BulletinBoardOnChatEvent>(priority = Priority.HIGHEST) {
        val player = it.player
        val state = it.state
        val event = it.event

        event.isCancelled = true

        when (val inputType = state.inputType ?: state.editInputType) {
            InputType.TITLE -> updateDraft(player, state, inputType, event.message, true)
            InputType.CONTENT -> updateDraft(player, state, inputType, event.message, false)
            else -> return@handler
        }
    }

    val onInvClose = handler<BulletinBoardCloseEvent> {
        val player = it.player
        val title = it.inventoryTitle
        val state = it.state

        when (title) {
            LanguageManager.getMessage(player, MessageKey.POST_EDITOR) -> handlePostEditorClose(state)
            LanguageManager.getMessage(player, MessageKey.SAVE_POST_CONFIRMATION),
            LanguageManager.getMessage(player, MessageKey.CANCEL_POST_CONFIRMATION) -> clearConfirmationState(state)
        }
    }

    private fun handleMainBoardClick(player: Player, customId: String?) {
        when (customId) {
            CustomID.NEW_POST.name -> BulletinBoardManager.openPostEditor(player)
            CustomID.ALL_POSTS.name -> BulletinBoardManager.openAllPosts(player)
            CustomID.MY_POSTS.name -> BulletinBoardManager.openMyPosts(player)
            CustomID.DELETED_POSTS.name -> BulletinBoardManager.openDeletedPosts(player)
            CustomID.ABOUT_PLUGIN.name -> BulletinBoardManager.performAbout(player)
            CustomID.HELP.name -> BulletinBoardManager.performHelp(player)
        }
    }

    private fun handlePostsClick(player: Player, inventory: Component, customId: String?) {
        val currentPage = customId?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0

        when (customId?.split(":")?.getOrNull(0)) {
            CustomID.PREV_PAGE.name -> openPage(player, inventory, currentPage - 1)
            CustomID.NEXT_PAGE.name -> openPage(player, inventory, currentPage + 1)
            CustomID.BACK_BUTTON.name -> BulletinBoardManager.openMainBoard(player)
            CustomID.DELETE_POST.name -> BulletinBoardManager.openDeletePostSelection(player)
            else -> {
                if (inventory != LanguageManager.getMessage(player, MessageKey.DELETED_POSTS) && customId != null) {
                    val post = database.getPost(customId)
                    post?.let { BulletinBoardManager.displayPost(player, it) }
                } else if (inventory == LanguageManager.getMessage(player, MessageKey.DELETED_POSTS) && customId != null) {
                    val post = database.getDeletedPostsByID(customId)
                    post?.let { BulletinBoardManager.openConfirmation(player, ConfirmationType.DELETING_POST) }
                }
            }
        }
    }

    private fun openPage(player: Player, inventory: Component, page: Int) {
        when (inventory) {
            LanguageManager.getMessage(player, MessageKey.ALL_POSTS) -> BulletinBoardManager.openAllPosts(player, page)
            LanguageManager.getMessage(player, MessageKey.MY_POSTS) -> BulletinBoardManager.openMyPosts(player, page)
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

    private fun beginInput(player: Player, state: PlayerState, inputType: InputType) {
        state.isInputting = true
        state.inputType = inputType
        runTask(p) { player.closeInventory() }
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

    private fun handleDeletePostSelection(player: Player, customId: String?) {
        when (customId) {
            CustomID.BACK_BUTTON.name -> BulletinBoardManager.openMainBoard(player)
            else -> {
                if (customId != null) {
                    val post = database.getPost(customId)
                    post?.let { BulletinBoardManager.openConfirmation(player, ConfirmationType.DELETING_POST) }
                }
            }
        }
    }

    private fun updateDraft(player: Player, state: PlayerState, inputType: InputType, message: String, isTitle: Boolean) {
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
            state.isInputting -> state.isInputting = false
            state.isOpeningConfirmation -> state.isOpeningConfirmation = false
            state.isPreviewing -> state.isPreviewing = false
            else -> state.draft = null
        }
    }

    private fun clearConfirmationState(state: PlayerState) {
        state.confirmationType = null
        state.isOpeningConfirmation = false
    }

    private fun savePostStateNull(player: Player) {
        runTask(p) {
            player.closeInventory()
            player.sendMessage(LanguageManager.getMessage(player, MessageKey.WHEN_POST_DRAFT_NULL))
        }
    }
}
