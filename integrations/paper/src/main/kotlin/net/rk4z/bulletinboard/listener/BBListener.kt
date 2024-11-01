@file:Suppress("DuplicatedCode")

package net.rk4z.bulletinboard.listener

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.guis.openPostEditor
import net.rk4z.bulletinboard.guis.openPostEditorForEdit
import net.rk4z.bulletinboard.utils.*
import net.rk4z.bulletinboard.utils.InputType.*
import net.rk4z.igf.GUIListener
import net.rk4z.igf.InventoryGUI
import net.rk4z.s1.pluginBase.Executor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent

class BBListener : Listener, GUIListener {
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        val state = player.getPlayerState()

        val inputType = state.inputType ?: state.editInputType ?: return
        event.isCancelled = true

        val plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message())

        updateDraft(state, inputType, plainMessage)

        Executor.execute {
            if (state.editInputType != null) {
                openPostEditorForEdit(player, null)
            } else {
                openPostEditor(player)
            }
        }
    }

    private fun updateDraft(
        state: PlayerState,
        inputType: InputType,
        message: String
    ) {
        val isEditMode = state.inputType == null && state.editInputType != null

        val draft = if (isEditMode) state.editDraft ?: EditPostData() else state.draft ?: PostDraft()

        when (inputType) {
            TITLE -> draft.title = Component.text(message)
            CONTENT -> draft.content = Component.text(message)
            else -> {}
        }

        if (isEditMode) {
            state.editDraft = draft as EditPostData
            state.editInputType = null
        } else {
            state.draft = draft as PostDraft
            state.inputType = null
        }
    }

    override fun onInventoryClick(event: InventoryClickEvent, gui: InventoryGUI) {}
    override fun onInventoryOpen(event: InventoryOpenEvent, gui: InventoryGUI) {}
    override fun onInventoryClose(event: InventoryCloseEvent, gui: InventoryGUI) {
        val player = event.player as Player
        val state = player.getPlayerState()

        if (event.reason == InventoryCloseEvent.Reason.PLAYER) {
            state.clearAll()
        }
    }
}
