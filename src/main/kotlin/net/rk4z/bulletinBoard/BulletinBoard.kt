package net.rk4z.bulletinBoard

import net.rk4z.bulletinBoard.utils.DataBase
import org.bukkit.plugin.java.JavaPlugin

class BulletinBoard : JavaPlugin() {
    companion object {
        lateinit var instance: BulletinBoard
            private set
    }

    private lateinit var database: DataBase

    override fun onLoad() {
        instance = this
        if (!dataFolder.exists()) {
            dataFolder.mkdir()
        }
        database = DataBase(this)
        database.connectToDatabase()
        database.createRequirementTables()
    }

    override fun onEnable() {

    }

    override fun onDisable() {
        database.closeConnection()
    }
}
