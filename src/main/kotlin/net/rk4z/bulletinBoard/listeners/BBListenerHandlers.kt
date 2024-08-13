package net.rk4z.bulletinBoard.listeners

import net.rk4z.beacon.IEventHandler
import net.rk4z.beacon.Priority
import net.rk4z.beacon.handler
import net.rk4z.bulletinBoard.events.BulletinBoardClickEvent
import net.rk4z.bulletinBoard.events.BulletinBoardCloseEvent
import net.rk4z.bulletinBoard.events.BulletinBoardOnChatEvent
import net.rk4z.bulletinBoard.listeners.BBListenerActions.handleCancelPostConfirmation
import net.rk4z.bulletinBoard.listeners.BBListenerActions.handleConfirmationClose
import net.rk4z.bulletinBoard.listeners.BBListenerActions.handleDeletePostConfirmation
import net.rk4z.bulletinBoard.listeners.BBListenerActions.handleDeletePostPermanentlyConfirmation
import net.rk4z.bulletinBoard.listeners.BBListenerActions.handleMainBoardClick
import net.rk4z.bulletinBoard.listeners.BBListenerActions.handlePostEditorClick
import net.rk4z.bulletinBoard.listeners.BBListenerActions.handlePostEditorClose
import net.rk4z.bulletinBoard.listeners.BBListenerActions.handlePostEditorForEditClick
import net.rk4z.bulletinBoard.listeners.BBListenerActions.handlePostsClick
import net.rk4z.bulletinBoard.listeners.BBListenerActions.handleRestorePostConfirmation
import net.rk4z.bulletinBoard.listeners.BBListenerActions.handleSavePostConfirmation
import net.rk4z.bulletinBoard.listeners.BBListenerActions.handleSelectionClick
import net.rk4z.bulletinBoard.listeners.BBListenerActions.updateDraft
import net.rk4z.bulletinBoard.managers.LanguageManager
import net.rk4z.bulletinBoard.utils.InputType
import net.rk4z.bulletinBoard.utils.MessageKey

@Suppress("unused", "DuplicatedCode")
class BBListenerHandlers : IEventHandler {
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
            LanguageManager.getMessage(player, MessageKey.DELETED_POSTS), -> {
                event.isCancelled = true
                handlePostsClick(player, inventory, customId)
            }

            LanguageManager.getMessage(player, MessageKey.DELETE_POST_SELECTION),
            LanguageManager.getMessage(player, MessageKey.EDIT_POST_SELECTION),
            LanguageManager.getMessage(player, MessageKey.DELETE_POST_PERMANENTLY_SELECTION),
            LanguageManager.getMessage(player, MessageKey.RESTORE_POST_SELECTION)-> {
                event.isCancelled = true
                handleSelectionClick(player, inventory, customId, state)
            }

            LanguageManager.getMessage(player, MessageKey.POST_EDITOR) -> {
                event.isCancelled = true
                handlePostEditorClick(player, state, customId)
            }

            LanguageManager.getMessage(player, MessageKey.SAVE_POST_CONFIRMATION) -> {
                event.isCancelled = true
                handleSavePostConfirmation(player, state, customId)
            }

            LanguageManager.getMessage(player, MessageKey.DELETE_POST_CONFIRMATION) -> {
                event.isCancelled = true
                handleDeletePostConfirmation(player, customId, state)
            }

            LanguageManager.getMessage(player, MessageKey.CANCEL_POST_CONFIRMATION) -> {
                event.isCancelled = true
                handleCancelPostConfirmation(player, state, customId)
            }

            LanguageManager.getMessage(player, MessageKey.DELETE_POST_PERMANENTLY_CONFIRMATION) -> {
                event.isCancelled = true
                handleDeletePostPermanentlyConfirmation(player, state, customId)
            }

            LanguageManager.getMessage(player, MessageKey.RESTORE_POST_CONFIRMATION) -> {
                event.isCancelled = true
                handleRestorePostConfirmation(player, state, customId)
            }

            LanguageManager.getMessage(player, MessageKey.POST_EDITOR_FOR_EDIT) -> {
                event.isCancelled = true
                handlePostEditorForEditClick(player, state, customId)
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
            LanguageManager.getMessage(player, MessageKey.MAIN_BOARD) -> state.clear()

            LanguageManager.getMessage(player, MessageKey.POST_EDITOR) -> handlePostEditorClose(state)

            LanguageManager.getMessage(player, MessageKey.SAVE_POST_CONFIRMATION),
            LanguageManager.getMessage(player, MessageKey.CANCEL_POST_CONFIRMATION),
            LanguageManager.getMessage(player, MessageKey.DELETE_POST_CONFIRMATION),
            LanguageManager.getMessage(
                player,
                MessageKey.DELETE_POST_PERMANENTLY_CONFIRMATION
            ) -> handleConfirmationClose(state)
        }
    }
}
