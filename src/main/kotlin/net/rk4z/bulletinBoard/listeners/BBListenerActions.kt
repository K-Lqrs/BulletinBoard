package net.rk4z.bulletinBoard.listeners

import net.kyori.adventure.text.Component
import net.rk4z.beacon.EventHandler
import net.rk4z.beacon.IEventHandler
import net.rk4z.beacon.Priority
import net.rk4z.beacon.handler
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.BulletinBoard.Companion.runTask
import net.rk4z.bulletinBoard.events.BulletinBoardClickEvent
import net.rk4z.bulletinBoard.events.BulletinBoardOnChatEvent
import net.rk4z.bulletinBoard.managers.BulletinBoardManager
import net.rk4z.bulletinBoard.managers.LanguageManager
import net.rk4z.bulletinBoard.utils.*

@EventHandler
@Suppress("unused", "DuplicatedCode")
class BBListenerActions : IEventHandler {
    private val p = BulletinBoard.instance

    val onBBClick: Unit = handler<BulletinBoardClickEvent>(
        priority = Priority.HIGHEST
    ) {
        val player = it.player
        val event = it.event
        val state = it.state
        val customId = it.customId
        val inventory = it.inventoryTitle

        when (inventory) {
            LanguageManager.getMessage(player, MessageKey.MAIN_BOARD) -> {
                event.isCancelled = true
                when (customId) {
                    CustomID.NEW_POST.name -> BulletinBoardManager.openPostEditor(player)
//                    "allPosts" -> BulletinBoardManager.openAllPosts(player)
//                    "myPosts" -> BulletinBoardManager.openMyPosts(player)
//                    "deletedPosts" -> BulletinBoardManager.openDeletedPosts(player)
                    CustomID.ABOUT_PLUGIN.name -> BulletinBoardManager.performAbout(player)
                    CustomID.HELP.name -> BulletinBoardManager.performHelp(player)
                }
            }

            LanguageManager.getMessage(player, MessageKey.POST_EDITOR) -> {
                event.isCancelled = true
                when (customId) {
                    CustomID.POST_TITLE.name -> {
                        state.isInputting = true
                        runTask(p) {
                            player.closeInventory()
                        }
                        state.inputType = InputType.TITLE
                        player.sendMessage(LanguageManager.getMessage(player, MessageKey.PLEASE_ENTER_TITLE))
                    }

                    CustomID.POST_CONTENT.name -> {
                        state.isInputting = true
                        runTask(p) {
                            player.closeInventory()
                        }
                        state.inputType = InputType.CONTENT
                        player.sendMessage(LanguageManager.getMessage(player, MessageKey.PLEASE_ENTER_CONTENT))
                    }

                    "cancelPost" -> {

                    }
                }
            }
        }
    }

    val onChat: Unit = handler<BulletinBoardOnChatEvent>(
        priority = Priority.HIGHEST
    ) {
        val player = it.player
        val state = it.state
        val event = it.event
        val message = event.message

        if (state.inputType != null) {
            event.isCancelled = true
            val inputType = state.inputType
            val draft = state.draft ?: PostDraft()

            val updatedDraft = when (inputType) {
                InputType.TITLE -> {
                    draft.copy(title = Component.text(message))
                }
                InputType.CONTENT -> {
                    draft.copy(content = Component.text(message))
                }
                else -> draft
            }

            state.draft = updatedDraft

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
            state.inputType = null
            player.sendMessage(
                LanguageManager.getMessage(player, MessageKey.INPUT_SET).replaceText { text ->
                    inputType?.name?.lowercase()?.let { s -> text.matchLiteral("{inputType}").replacement(s) }
                }.replaceText { text -> text.matchLiteral("{input}").replacement(message) }
            )
        } else if (state.editInputType != null) {
            event.isCancelled = true
            val inputType = state.editInputType
            val draft = state.editDraft ?: EditPostData()

            val updatedDraft = when (inputType) {
                InputType.TITLE -> {
                    draft.copy(title = Component.text(message))
                }
                InputType.CONTENT -> {
                    draft.copy(content = Component.text(message))
                }
                else -> draft
            }

            state.editDraft = updatedDraft

            val uTitle = updatedDraft.title ?: LanguageManager.getMessage(player, MessageKey.NO_TITLE)
            val uContent = updatedDraft.content ?: LanguageManager.getMessage(player, MessageKey.NO_CONTENT)

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
            state.editInputType = null
            player.sendMessage(
                LanguageManager.getMessage(player, MessageKey.INPUT_SET).replaceText { text ->
                    inputType?.name?.lowercase()?.let { s -> text.matchLiteral("{inputType}").replacement(s) }
                }.replaceText { text -> text.matchLiteral("{input}").replacement(message) }
            )
        }
    }
}