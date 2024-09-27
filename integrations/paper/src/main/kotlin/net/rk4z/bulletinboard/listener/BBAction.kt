package net.rk4z.bulletinboard.listener
//
//import net.kyori.adventure.text.Component
//import net.rk4z.bulletinboard.guis.openAllPosts
//import net.rk4z.bulletinboard.guis.openDeletedPosts
//import net.rk4z.bulletinboard.guis.openMyPosts
//import net.rk4z.bulletinboard.guis.openPostEditor
//import net.rk4z.bulletinboard.manager.CommandManager.displayHelp
//import net.rk4z.bulletinboard.manager.LanguageManager
//import net.rk4z.bulletinboard.utils.CustomID
//import net.rk4z.bulletinboard.utils.CustomID.*
//import net.rk4z.bulletinboard.utils.MessageKey
//import net.rk4z.bulletinboard.utils.playSoundMaster
//import org.bukkit.Sound
//import org.bukkit.entity.Player
//
//object BBAction {
//    fun handleMainBoardClick(player: Player, customId: CustomID?) {
//        when (customId) {
//            NEW_POST -> openPostEditor(player)
//            ALL_POSTS -> openAllPosts(player)
//            MY_POSTS -> openMyPosts(player)
//            DELETED_POSTS -> openDeletedPosts(player)
//            ABOUT_PLUGIN -> TODO()
//            SETTINGS -> TODO()
//            HELP -> displayHelp(player)
//
//            else -> {}
//        }
//    }
//
//    fun handlePostsClick(player: Player, inventory: Component, customId: String?)  {
//        val currentPage = customId?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0
//
//        when (customId?.split(":")?.getOrNull(0)) {
//            CustomID.PREV_PAGE.name -> {
//                player.playSoundMaster(Sound.ITEM_BOOK_PAGE_TURN, 0.5f)
//                openPage(player, inventory, currentPage - 1)
//            }
//            CustomID.NEXT_PAGE.name -> {
//                player.playSoundMaster(Sound.ITEM_BOOK_PAGE_TURN, 0.5f)
//                openPage(player, inventory, currentPage + 1)
//            }
//            CustomID.BACK_BUTTON.name -> {
//                if (inventory == LanguageManager.getMessage(player, MessageKey.ALL_POSTS) ||
//                    inventory == LanguageManager.getMessage(player, MessageKey.MY_POSTS) ||
//                    inventory == LanguageManager.getMessage(player, MessageKey.DELETED_POSTS)
//                ) {
//                    BulletinBoardManager.openMainBoard(player)
//                }
//            }
//
//            CustomID.DELETE_POST.name -> BulletinBoardManager.openDeletePostSelection(player)
//            CustomID.DELETE_POST_PERMANENTLY.name -> BulletinBoardManager.openDeletePostPermanentlySelection(player)
//            CustomID.RESTORE_POST.name -> BulletinBoardManager.openRestorePostSelection(player)
//            CustomID.EDIT_POST.name -> BulletinBoardManager.openEditPostSelection(player)
//            else -> {
//                if (inventory != LanguageManager.getMessage(player, MessageKey.DELETED_POSTS) && customId != null) {
//                    val post = database.getPost(customId)
//                    post?.let { BulletinBoardManager.displayPost(player, it) }
//                } else if (inventory == LanguageManager.getMessage(player, MessageKey.DELETED_POSTS) && customId != null) {
//                    val post = database.getDeletedPostsByID(customId)
//                    post?.let { BulletinBoardManager.displayPost(player, it) }
//                }
//            }
//        }
//    }
//}