package net.rk4z.bulletinboard.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

@Suppress("unused")
data class PlayerState(
    // the draft the player is currently working on
    var draft: PostDraft? = null,
    // the draft the player is currently editing
    var editDraft: EditPostData? = null,

    // the ID of the post the player is currently deleting
    var selectedDeletingPostId: String? = null,
    // the ID of the post the player is currently editing
    var selectedEditingPostId: String? = null,
    // the ID of the post the player is currently restoring
    var selectedRestoringPostId: String? = null,

    // when true, the player is inputting a something. the type is determined by the inputType
    var isInputting: Boolean? = null,
    // when true, the player is editing a something. the type is determined by the editInputType
    var isEditInputting: Boolean? = null,
    // when true, the player is previewing.
    var isPreviewing: Boolean? = null,
    // when true, the player is opening a confirmation screen.
    var isOpeningConfirmation: Boolean? = null,
    // when true, the player is choosing an answer in a confirmation screen.
    var isChoosingConfirmationAnswer: Boolean? = null,

    // the type of input the player is currently inputting (title/content)
    var inputType: InputType? = null,
    // the type of input the player is currently editing (title/content)
    var editInputType: InputType? = null,
    // the type of confirmation the player is currently opening
    var confirmationType: ConfirmationType? = null,

    var preview: Pair<Component, Component>? = null,
) {
    fun clearAll() {
        this.draft = null
        this.editDraft = null
        this.selectedDeletingPostId = null
        this.selectedEditingPostId = null
        this.isInputting = null
        this.isEditInputting = null
        this.isPreviewing = null
        this.isOpeningConfirmation = null
        this.isChoosingConfirmationAnswer = null
        this.inputType = null
        this.editInputType = null
        this.confirmationType = null
        this.preview = null
    }

    fun clearDraft() {
        draft = null
    }

    fun clearEditDraft() {
        editDraft = null
    }

    fun clearSelection() {
        selectedDeletingPostId = null
        selectedEditingPostId = null
        selectedRestoringPostId = null
    }

    fun clearInputFlags() {
        isInputting = null
        isEditInputting = null
        isPreviewing = null
    }

    fun clearConfirmation() {
        isOpeningConfirmation = null
        isChoosingConfirmationAnswer = null
        confirmationType = null
        preview = null
    }

    fun sendDebugMessage(player: Player) {
        val stateMessage = Component.text()
            .append(Component.text("Player State:", NamedTextColor.GOLD, TextDecoration.BOLD))
            .append(Component.newline())
            .append(Component.text("Draft: ", NamedTextColor.GRAY))
            .append(Component.text(draft?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Edit Draft: ", NamedTextColor.GRAY))
            .append(Component.text(editDraft?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Selected Deleting Post ID: ", NamedTextColor.GRAY))
            .append(Component.text(selectedDeletingPostId ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Selected Editing Post ID: ", NamedTextColor.GRAY))
            .append(Component.text(selectedEditingPostId ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Selected Restoring Post ID: ", NamedTextColor.GRAY))
            .append(Component.text(selectedRestoringPostId ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Is Inputting: ", NamedTextColor.GRAY))
            .append(Component.text(isInputting?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Is Edit Inputting: ", NamedTextColor.GRAY))
            .append(Component.text(isEditInputting?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Is Previewing: ", NamedTextColor.GRAY))
            .append(Component.text(isPreviewing?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Is Opening Confirmation: ", NamedTextColor.GRAY))
            .append(Component.text(isOpeningConfirmation?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Is Choosing Confirmation Answer: ", NamedTextColor.GRAY))
            .append(Component.text(isChoosingConfirmationAnswer?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.newline())
            .append(Component.text("Input Type: ", NamedTextColor.GRAY))
            .append(Component.text(inputType?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Edit Input Type: ", NamedTextColor.GRAY))
            .append(Component.text(editInputType?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Confirmation Type: ", NamedTextColor.GRAY))
            .append(Component.text(confirmationType?.toString() ?: "None", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Preview: ", NamedTextColor.GRAY))
            .append(preview?.let {
                Component.text(it.first.toString() + " | " + it.second.toString(), NamedTextColor.WHITE)
            } ?: Component.text("None", NamedTextColor.WHITE))

        player.sendMessage(stateMessage)
    }
}