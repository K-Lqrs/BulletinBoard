/*
 *     BulletinBoard - A simple bulletin board plugin
 *     Copyright (C) 2024 Ruxy
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.rk4z.bulletinBoard

import net.rk4z.beacon.EventBus
import net.rk4z.bulletinBoard.events.BulletinBoardOnCommandEvent
import net.rk4z.bulletinBoard.events.BulletinBoardOnTabCompleteEvent
import net.rk4z.bulletinBoard.listeners.BBListener
import net.rk4z.bulletinBoard.listeners.BBListenerActions
import net.rk4z.bulletinBoard.managers.BBCommandManager
import net.rk4z.bulletinBoard.utils.DataBase
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

@Suppress("DEPRECATION")
class BulletinBoard : JavaPlugin() {
    companion object {
        lateinit var instance: BulletinBoard
            private set
        lateinit var namespacedKey: NamespacedKey
            private set
        lateinit var database: DataBase
            private set

        private const val ID: String = "bulletinboard"

        val runTask: TaskRunner = { plugin, runnable ->
            Bukkit.getScheduler().runTask(plugin, runnable)
        }

        val runTaskAsynchronous: TaskRunner = { plugin, runnable ->
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
        }
    }

    val version: String = description.version
    val pluginDes: String? = description.description
    val author: MutableList<String> = description.authors
    val logger: Logger = LoggerFactory.getLogger(BulletinBoard::class.java.simpleName)
    val subCommands: MutableList<String> = mutableListOf(
        "openboard",
        "newpost",
        "posts",
        "myposts",
        "help",
        "about",
        "howtouse"
    )

    override fun onLoad() {
        instance = this
        namespacedKey = NamespacedKey(this, ID)
        EventBus.initialize()
        BBCommandManager()
        BBListenerActions()

        if (!dataFolder.exists()) {
            dataFolder.mkdir()
        }

        database = DataBase(this)

        if (database.connectToDatabase()) {
            database.createRequirementTables()
            if (!database.isDataMigrated()) {
                val jsonFile = File(dataFolder, "data.json")
                if (jsonFile.exists()) {
                    logger.info("Old data file is found. trying to import data from json....")
                    database.importDataFromJson(jsonFile)
                }
            }
        }
    }

    override fun onEnable() {
        server.pluginManager.apply {
            registerEvents(BBListener(), this@BulletinBoard)
        }
    }

    override fun onDisable() {
        database.closeConnection()
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): Boolean {
        return EventBus.postReturnable(BulletinBoardOnCommandEvent.get(sender, command, args))?: false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>?
    ): MutableList<String>? {
        return EventBus.postReturnable(BulletinBoardOnTabCompleteEvent.get(command, args))
    }
}

typealias TaskRunner = (JavaPlugin, Runnable) -> Unit
