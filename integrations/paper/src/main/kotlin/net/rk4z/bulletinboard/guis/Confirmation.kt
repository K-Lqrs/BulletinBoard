package net.rk4z.bulletinboard.guis

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.utils.*
import net.rk4z.bulletinboard.utils.ConfirmationType.*
import net.rk4z.bulletinboard.utils.ConfirmationType.CANCEL_EDIT
import net.rk4z.bulletinboard.utils.ConfirmationType.CANCEL_POST
import net.rk4z.bulletinboard.utils.ConfirmationType.DELETE_POST_FROM_ALL
import net.rk4z.bulletinboard.utils.ConfirmationType.SAVE_EDIT
import net.rk4z.bulletinboard.utils.ConfirmationType.SAVE_POST
import net.rk4z.bulletinboard.utils.CustomID.*
import net.rk4z.igf.*
import net.rk4z.s1.pluginBase.Executor
import net.rk4z.s1.pluginBase.LanguageManager
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

fun openSavePostConfirmation(player: Player) {
    val state = player.getPlayerState()
    state.preview =
        Pair(
            state.draft?.title ?: LanguageManager.getMessage(player, Main.Gui.Other.NO_TITLE),
            state.draft?.content ?: LanguageManager.getMessage(player, Main.Gui.Other.NO_CONTENT)
        )
    openConfirmationScreen(player, SAVE_POST)
}

fun openCancelPostConfirmation(player: Player) {
    openConfirmationScreen(player, CANCEL_POST)
}

fun openSaveEditConfirmation(player: Player) {
    openConfirmationScreen(player, SAVE_EDIT)
}

fun openCancelEditConfirmation(player: Player) {
    openConfirmationScreen(player, CANCEL_EDIT)
}

fun openDeletePostConfirmation(player: Player) {
    openConfirmationScreen(player, DELETING_POST)
}

fun openRestorePostConfirmation(player: Player) {
    openConfirmationScreen(player, RESTORING_POST)
}

fun openDeletePostFromAllConfirmation(player: Player) {
    openConfirmationScreen(player, DELETE_POST_FROM_ALL)
}

fun openDeletePostPermanentlyConfirmation(player: Player) {
    openConfirmationScreen(player, DELETING_POST_PERMANENTLY)
}

private fun openConfirmationScreen(player: Player, type: ConfirmationType) {
    val state = player.getPlayerState()
    state.isOpeningConfirmation = true
    state.confirmationType = type

    val title = when (type) {
        SAVE_POST -> Main.Gui.Title.SAVE_POST_CONFIRMATION
        CANCEL_POST -> Main.Gui.Title.CANCEL_POST_CONFIRMATION
        DELETING_POST -> Main.Gui.Title.DELETE_POST_CONFIRMATION
        DELETING_POST_PERMANENTLY -> Main.Gui.Title.DELETE_POST_PERMANENTLY_CONFIRMATION
        RESTORING_POST -> Main.Gui.Title.RESTORE_POST_CONFIRMATION
        DELETE_POST_FROM_ALL -> Main.Gui.Title.DELETE_POST_FROM_ALL_CONFIRMATION
        SAVE_EDIT -> Main.Gui.Title.SAVE_EDIT_CONFIRMATION
        CANCEL_EDIT -> Main.Gui.Title.CANCEL_EDIT_CONFIRMATION
    }.t(player)

    val buttons = when (type) {
        SAVE_POST -> listOf(
            Button(11, Material.RED_WOOL, Main.Gui.Button.CANCEL_CONFIRM_SAVE_POST.t(player), CANCEL_CONFIRM_SAVE_POST.name),
            Button(13, Material.BLUE_WOOL, Main.Gui.Button.PREVIEW_POST.t(player), PREVIEW_POST.name),
            Button(15, Material.GREEN_WOOL, Main.Gui.Button.CONFIRM_SAVE_POST.t(player), CONFIRM_SAVE_POST.name)
        )
        CANCEL_POST -> listOf(
            Button(11, Material.RED_WOOL, Main.Gui.Button.CONTINUE_POST.t(player), CONTINUE_POST.name),
            Button(15, Material.GREEN_WOOL, Main.Gui.Button.CONFIRM_CANCEL_POST.t(player), CONFIRM_CANCEL_POST.name)
        )
        DELETING_POST -> listOf(
            Button(11, Material.RED_WOOL, Main.Gui.Button.CANCEL_DELETE_POST.t(player), CANCEL_DELETE_POST.name),
            Button(15, Material.GREEN_WOOL, Main.Gui.Button.CONFIRM_DELETE_POST.t(player), CONFIRM_DELETE_POST.name)
        )
        DELETING_POST_PERMANENTLY -> listOf(
            Button(11, Material.RED_WOOL, Main.Gui.Button.CANCEL_DELETE_POST_PERMANENTLY.t(player), CANCEL_DELETE_POST_PERMANENTLY.name),
            Button(15, Material.GREEN_WOOL, Main.Gui.Button.CONFIRM_DELETE_POST_PERMANENTLY.t(player), CONFIRM_DELETE_POST_PERMANENTLY.name)
        )
        RESTORING_POST -> listOf(
            Button(11, Material.RED_WOOL, Main.Gui.Button.CANCEL_RESTORE_POST.t(player), CANCEL_RESTORE_POST.name),
            Button(15, Material.GREEN_WOOL, Main.Gui.Button.CONFIRM_RESTORE_POST.t(player), CONFIRM_RESTORE_POST.name)
        )
        DELETE_POST_FROM_ALL -> listOf(
            Button(11, Material.RED_WOOL, Main.Gui.Button.CANCEL_DELETE_POST_FROM_ALL.t(player), CANCEL_DELETE_POST_FROM_ALL.name),
            Button(15, Material.GREEN_WOOL, Main.Gui.Button.CONFIRM_DELETE_POST_FROM_ALL.t(player), CONFIRM_DELETE_POST_FROM_ALL.name)
        )
        SAVE_EDIT -> listOf(

        )
        CANCEL_EDIT -> listOf(

        )
    }

    //TODO()
    val listener = object : GUIListener {
        override fun onInventoryClick(event: InventoryClickEvent, gui: InventoryGUI) {
            event.isCancelled = true

            val item = event.currentItem ?: return
            val meta = item.itemMeta ?: return
            val customId = CustomID.fromString(meta.persistentDataContainer.get(IGF.key, PersistentDataType.STRING) ?: return)

            when (customId) {
                PREVIEW_POST -> {
                    state.isPreviewing = true

                    val dt = state.preview?.first?.getContent()
                    val dc = state.preview?.second?.getContent()

                    if (dt == null || dc == null) {
                        throw IllegalStateException("Preview data is null")
                    }

                    gui.close()
                    player.sendMessage(Component.text("-----[ Preview ]-----"))
                    player.sendMessage(LanguageManager.getMessage(player, Main.Message.TITLE_LABEL, dt))
                    player.sendMessage(LanguageManager.getMessage(player, Main.Message.CONTENT_LABEL, dc))
                    player.sendMessage(Component.text("--------------------"))
                }

                CONFIRM_SAVE_POST -> {
                    val draft = state.draft
                    if (draft == null) {
                        player.closeInventory()
                        player.sendMessage(LanguageManager.getMessage(player, Main.Message.WHEN_POST_DRAFT_NULL))
                        state.clearAll()
                    } else {
                        val post = Post(
                            id = ShortUUID.randomUUID(),
                            title = draft.title ?: LanguageManager.getMessage(player, Main.Gui.Other.NO_TITLE),
                            author = player.uniqueId,
                            content = draft.content ?: LanguageManager.getMessage(player, Main.Gui.Other.NO_CONTENT),
                            isAnonymous = draft.isAnonymous,
                            date = Date()
                        )

                        Executor.executeAsync {
                            BulletinBoard.dataBase.insertPost(post)
                        }

                        state.clearAll()
                        gui.close()

                        player.playSoundMaster(Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.5f)
                        player.sendMessage(LanguageManager.getMessage(player, Main.Message.POST_SAVED))
                    }
                }

                CONTINUE_POST -> {
                    state.isOpeningConfirmation = null
                    state.confirmationType = null
                    openPostEditor(player)
                }

                CONFIRM_CANCEL_POST -> {
                    state.clearAll()
                    gui.close()
                    player.sendMessage(LanguageManager.getMessage(player, Main.Message.POST_CANCELLED))
                }

                CustomID.DELETE_POST_FROM_ALL -> {
                    val id = state.selectedDeletingPostId ?: run {
                        player.sendMessage(LanguageManager.getMessage(player, Main.Message.POST_NOT_FOUND))
                        return
                    }

                    Executor.executeAsync {
                        BulletinBoard.dataBase.deletePost(id)
                    }

                    state.clearAll()
                    gui.close()
                    player.sendMessage(LanguageManager.getMessage(player, Main.Message.POST_DELETED))
                }

                CONFIRM_DELETE_POST_FROM_ALL -> {
                    val id = state.selectedDeletingPostId ?: run {
                        player.sendMessage(LanguageManager.getMessage(player, Main.Message.POST_NOT_FOUND))
                        return
                    }

                    Executor.executeAsync {
                        BulletinBoard.dataBase.deletePostFromAll(id)
                    }

                    state.clearAll()
                    gui.close()
                    player.sendMessage(LanguageManager.getMessage(player, Main.Message.POST_DELETED))
                }

                CANCEL_CONFIRM_SAVE_POST -> {
                    state.isOpeningConfirmation = null
                    state.confirmationType = null
                    openPostEditor(player)
                }

                else -> {}
            }
        }

        override fun onInventoryOpen(event: InventoryOpenEvent, gui: InventoryGUI) {}

        override fun onInventoryClose(event: InventoryCloseEvent, gui: InventoryGUI) {}
    }

    val gui = SimpleGUI(player)
        .setTitle(title)
        .setSize(27)
        .setItems(buttons)
        .setBackground(Material.BLACK_STAINED_GLASS_PANE)
        .setListener(listener)
        .build()

    gui.open()
}