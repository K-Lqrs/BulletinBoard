package net.rk4z.bulletinboard

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DataBase(private val plugin: BulletinBoard) {
    private var connection: Connection? = null

    fun connectToDatabase(): Boolean {
        val url = "jdbc:sqlite:${plugin.dataFolder.absolutePath}/database.db"

        try {
            connection = DriverManager.getConnection(url)
            plugin.log.info("Successfully connected to the SQLite database!")
            return true
        } catch (e: SQLException) {
            plugin.log.error("Could not connect to the SQLite database!")
            e.printStackTrace()
            return false
        } catch (e: Exception) {
            plugin.log.error("An unknown error occurred while connecting to the SQLite database!")
            e.printStackTrace()
            return false
        }
    }

    fun createRequiredTables() {
        if (connection == null) {
            plugin.log.error("Could not create the required tables because the connection to the SQLite database is null!")
            return
        }

        try {

        } catch (e: SQLException) {
            plugin.log.error("Could not create the required tables!")
            e.printStackTrace()
        } catch (e: Exception) {
            plugin.log.error("An unknown error occurred while creating the required tables!")
            e.printStackTrace()
        }
    }

    fun closeConnection() {
        try {
            connection?.close()
        } catch (e: SQLException) {
            plugin.log.error("Could not close the SQLite database connection!")
            e.printStackTrace()
        } catch (e: Exception) {
            plugin.log.error("An unknown error occurred while closing the SQLite database connection!")
            e.printStackTrace()
        } finally {
            connection = null
        }
    }
}