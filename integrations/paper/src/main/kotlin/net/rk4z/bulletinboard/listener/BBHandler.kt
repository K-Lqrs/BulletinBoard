package net.rk4z.bulletinboard.listener
//
//import net.kyori.adventure.text.Component
//import net.rk4z.bulletinboard.listener.BBAction.handleMainBoardClick
//import net.rk4z.bulletinboard.listener.BBAction.handlePostsClick
//import net.rk4z.bulletinboard.manager.LanguageManager
//import net.rk4z.bulletinboard.utils.CustomID
//import net.rk4z.bulletinboard.utils.Main
//import net.rk4z.bulletinboard.utils.PlayerState
//import org.bukkit.entity.Player
//import org.bukkit.event.inventory.InventoryClickEvent
//
//object BBHandler {
//    fun onBBClick(
//        player: Player,
//        customId: CustomID?,
//        inventoryTitle: Component,
//        event: InventoryClickEvent
//    ) {
//        when (inventoryTitle) {
//            LanguageManager.getMessage(player, Main.Gui.Title.MAIN_BOARD) -> {
//                event.isCancelled = true
//                handleMainBoardClick(player, customId)
//            }
//
//            LanguageManager.getMessage(player, Main.Gui.Title.MY_POSTS),
//            LanguageManager.getMessage(player, Main.Gui.Title.ALL_POSTS,
//            LanguageManager.getMessage(player, Main.Gui.Title.DELETED_POSTS) -> {
//                event.isCancelled = true
//                handlePostsClick(player, inventoryTitle, customId)
//            }
////
////            LanguageManager.getMessage(player, DELETE_POST_SELECTION),
////            LanguageManager.getMessage(player, EDIT_POST_SELECTION),
////            LanguageManager.getMessage(player, DELETE_POST_PERMANENTLY_SELECTION),
////            LanguageManager.getMessage(player, RESTORE_POST_SELECTION)-> {
////                event.isCancelled = true
////                handleSelectionClick(player, inventoryTitle, customId, state)
////            }
////
////            LanguageManager.getMessage(player, MessageKey.POST_EDITOR) -> {
////                event.isCancelled = true
////                handlePostEditorClick(player, state, customId)
////            }
////
////            LanguageManager.getMessage(player, MessageKey.SAVE_POST_CONFIRMATION) -> {
////                event.isCancelled = true
////                handleSavePostConfirmation(player, state, customId)
////            }
////
////            LanguageManager.getMessage(player, MessageKey.DELETE_POST_CONFIRMATION) -> {
////                event.isCancelled = true
////                handleDeletePostConfirmation(player, customId, state)
////            }
////
////            LanguageManager.getMessage(player, MessageKey.CANCEL_POST_CONFIRMATION) -> {
////                event.isCancelled = true
////                handleCancelPostConfirmation(player, state, customId)
////            }
////
////            LanguageManager.getMessage(player, MessageKey.DELETE_POST_PERMANENTLY_CONFIRMATION) -> {
////                event.isCancelled = true
////                handleDeletePostPermanentlyConfirmation(player, state, customId)
////            }
////
////            LanguageManager.getMessage(player, MessageKey.RESTORE_POST_CONFIRMATION) -> {
////                event.isCancelled = true
////                handleRestorePostConfirmation(player, state, customId)
////            }
////
////            LanguageManager.getMessage(player, MessageKey.POST_EDITOR_FOR_EDIT) -> {
////                event.isCancelled = true
////                handlePostEditorForEditClick(player, state, customId)
////            }
//        }
//    }
//}