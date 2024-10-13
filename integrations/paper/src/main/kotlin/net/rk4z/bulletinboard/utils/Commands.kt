package net.rk4z.bulletinboard.utils

import net.kyori.adventure.text.Component
import net.rk4z.bulletinboard.guis.*
import net.rk4z.bulletinboard.manager.CommandManager.displayAbout
import net.rk4z.bulletinboard.manager.CommandManager.displayHelp
import net.rk4z.bulletinboard.BulletinBoard
import net.rk4z.bulletinboard.manager.LanguageManager
import org.bukkit.entity.Player

import java.util.*

typealias CommandExecute = (Player, Array<String>) -> Unit

enum class Commands(val execute: CommandExecute) {
    OPENBOARD({ player, _ -> openMainBoard(player) }),
    NEWPOST({ player, _ -> openPostEditor(player) }),
    ALLPOSTS({ player, _ -> openAllPosts(player) }),
    MYPOSTS({ player, _ -> openMyPosts(player) }),
    DELETEDPOSTS({ player, _ -> openDeletedPosts(player) }),
    PREVIEWCLOSE({ player, _ ->
        val state = player.getPlayerState()
        if (state.preview != null) {
            state.isPreviewing = null
            state.preview = null
            openPostEditor(player)
        } else {
            player.sendMessage(LanguageManager.getMessage(player, Main.Command.Message.NOT_PREVIEWING))
        }
    }),
    HELP({ player, _ -> displayHelp(player) }),
    ABOUT({ player, _ -> displayAbout(player) }),
    DEBUG({ player, _ -> player.getPlayerState().sendDebugMessage(player) }),
    INSERTDEBUGPOST({ player, args ->
        if (player.hasPermission("bulletinboard.post.debug")) {
            if (args.size > 1 && (args[1] == "0" || args[1] == "1")) {
                val isAnonymous = args[1] == "1"

                val post = Post(
                    id = ShortUUID.randomUUID(),
                    title = Component.text("Debug Post"),
                    content = Component.text("This is a debug post"),
                    author = player.uniqueId,
                    isAnonymous = isAnonymous,
                    date = Date()
                )
                BulletinBoard.dataBase.insertPost(post)
                player.sendMessage("Debug post inserted with isAnonymous set to $isAnonymous")
            } else {
                player.sendMessage("Usage: /insertdebugpost <0 or 1>")
            }
        } else {
            player.sendMessage("You do not have permission to use this command.")
        }
    }),
    RELOAD({ player, _ ->
        if (player.hasPermission("bulletinboard.reload")) {
            BulletinBoard.instance.reload(player)
        } else {
            player.sendMessage("You do not have permission to use this command.")
        }
    });

    companion object {
        fun fromString(name: String): Commands? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}
