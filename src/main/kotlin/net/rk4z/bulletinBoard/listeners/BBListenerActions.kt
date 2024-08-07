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
import net.rk4z.bulletinBoard.utils.EditPostData
import net.rk4z.bulletinBoard.utils.PostDraft
import org.bukkit.Bukkit

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
            LanguageManager.getMessage(player, "mainBoard") -> {
                when (customId) {
                    "newPost" -> {
                        event.isCancelled = true
                        BulletinBoardManager.openPostEditor(player)
                    }
//                    "allPosts" -> BulletinBoardManager.openAllPosts(player)
//                    "myPosts" -> BulletinBoardManager.openMyPosts(player)
//                    "deletedPosts" -> BulletinBoardManager.openDeletedPosts(player)
//                    "aboutPlugin" -> BulletinBoardManager.performAbout(player)
                }
            }

            LanguageManager.getMessage(player, "postEditor") -> {
                event.isCancelled = true
                when (customId) {
                    "postTitle" -> {
                        state.isInputting = true
                        runTask(p) {
                            player.closeInventory()
                        }
                        state.inputType = "title"
                        player.sendMessage(LanguageManager.getMessage(player, "pleaseEnterTitle"))
                    }

                    "postContent" -> {
                        state.isInputting = true
                        runTask(p) {
                            player.closeInventory()
                        }
                        state.inputType = "content"
                        player.sendMessage(LanguageManager.getMessage(player, "pleaseEnterContent"))
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

            val updatedDraft = if (inputType == "title") {
                draft.copy(title = Component.text(message))
            } else {
                draft.copy(content = Component.text(message))
            }

            state.draft = updatedDraft

            val uTitle = updatedDraft.title ?: LanguageManager.getMessage(player, "noTitle")
            val uContent = updatedDraft.content ?: LanguageManager.getMessage(player, "noContent")

            val postEditor = BulletinBoardManager.createPostEditorInventory(
                player,
                uTitle,
                uContent,
                LanguageManager.getMessage(player, "postEditor"),
                "postTitle",
                "postContent",
                "cancelPost",
                "savePost"
            )

            runTask(p) {
                player.openInventory(postEditor)
            }
            state.inputType = null
            player.sendMessage(
                LanguageManager.getMessage(player, "inputSet").replaceText { text ->
                    inputType?.let { s -> text.matchLiteral("{inputType}").replacement(s) }
                }.replaceText { text -> text.matchLiteral("{input}").replacement(message) }
            )
        } else if (state.editInputType != null) {
            event.isCancelled = true
            val inputType = state.editInputType
            val draft = state.editDraft ?: EditPostData()

            val updatedDraft = if (inputType == "title") {
                draft.copy(title = Component.text(message))
            } else {
                draft.copy(content = Component.text(message))
            }

            state.editDraft = updatedDraft

            val uTitle = updatedDraft.title ?: LanguageManager.getMessage(player, "noTitle")
            val uContent = updatedDraft.content ?: LanguageManager.getMessage(player, "noContent")

            val postEditor = BulletinBoardManager.createPostEditorInventory(
                player,
                uTitle,
                uContent,
                LanguageManager.getMessage(player, "postEditorForEdit"),
                "editPostTitle",
                "editPostContent",
                "cancelEdit",
                "saveEdit"
            )

            runTask(p) {
                player.openInventory(postEditor)
            }
            state.editInputType = null
            player.sendMessage(
                LanguageManager.getMessage(player, "inputSet").replaceText { text ->
                    inputType?.let { s -> text.matchLiteral("{inputType}").replacement(s) }
                }.replaceText { text -> text.matchLiteral("{input}").replacement(message) }
            )
        }
    }
}