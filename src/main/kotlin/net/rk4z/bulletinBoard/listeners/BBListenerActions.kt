package net.rk4z.bulletinBoard.listeners

import net.rk4z.beacon.EventHandler
import net.rk4z.beacon.IEventHandler
import net.rk4z.beacon.Priority
import net.rk4z.beacon.handler
import net.rk4z.bulletinBoard.events.BulletinBoardClickEvent
import net.rk4z.bulletinBoard.events.BulletinBoardCommandPreprocessEvent
import net.rk4z.bulletinBoard.managers.BulletinBoardManager
import net.rk4z.bulletinBoard.managers.LanguageManager

@Suppress("unused", "UNUSED_VARIABLE")
@EventHandler
class BBListenerActions : IEventHandler {

    val onBBClick = handler<BulletinBoardClickEvent>(
        priority = Priority.HIGHEST
    ) {
        val player = it.player
        val event = it.event
        val state = it.state
        val customId = it.customId
        val inventory = it.inventoryTitle

        when (inventory) {
            LanguageManager.getMessage(player, "mainBoard") -> {
                event.isCancelled = true
                when (customId) {
                    "newPost" -> BulletinBoardManager.openPostEditor(player)
                    "allPosts" -> BulletinBoardManager.openAllPosts(player)
                    "myPosts" -> BulletinBoardManager.openMyPosts(player)
                    "deletedPosts" -> BulletinBoardManager.openDeletedPosts(player)
                    "aboutPlugin" -> BulletinBoardManager.performAbout(player)
                }
            }
        }
    }

    val onBBCommandPre = handler<BulletinBoardCommandPreprocessEvent>(
        priority = Priority.HIGHEST
    ) {
        val player = it.player
        val command = it.command
        val oEvent = it.event


    }
}