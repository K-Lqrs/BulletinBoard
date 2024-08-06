package net.rk4z.bulletinBoard.utils

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.rk4z.bulletinBoard.BulletinBoard
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DataBase(private val plugin: BulletinBoard) {
    private var connection: Connection? = null

    fun connectToDatabase() {
        val url = "jdbc:sqlite:${plugin.dataFolder.absolutePath}/database.db"

        try {
            connection = DriverManager.getConnection(url)
            plugin.logger.info("Successfully connected to the SQLite database!")
        } catch (e: SQLException) {
            plugin.logger.severe("Could not connect to the SQLite database!")
            e.printStackTrace()
        }
    }

    fun createPostsTable() {
        val createTableSQL = """
            CREATE TABLE IF NOT EXISTS posts (
                id TEXT PRIMARY KEY,
                author TEXT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                date DATE NOT NULL
            );
        """.trimIndent()

        try {
            connection?.createStatement()?.use { statement ->
                statement.execute(createTableSQL)
                plugin.logger.info("Posts table created successfully!")
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Could not create posts table!")
            e.printStackTrace()
        }
    }

    fun insertPost(post: Post) {
        val insertSQL = "INSERT INTO posts (id, author, title, content, date) VALUES (?, ?, ?, ?, ?)"
        try {
            connection?.prepareStatement(insertSQL)?.use { statement ->
                statement.setString(1, post.id.toShortString())
                statement.setString(2, post.author.toString())
                statement.setString(3, GsonComponentSerializer.gson().serialize(post.title))
                statement.setString(4, GsonComponentSerializer.gson().serialize(post.content))
                statement.setDate(5, post.date)
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Could not insert post into database!")
            e.printStackTrace()
        }
    }

    fun deletePost(id: ShortUUID) {
        val deleteSQL = "DELETE FROM posts WHERE id = ?"
        try {
            connection?.prepareStatement(deleteSQL)?.use { statement ->
                statement.setString(1, id.toShortString())
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Could not delete post from database!")
            e.printStackTrace()
        }
    }

    fun closeConnection() {
        connection?.close()
        plugin.logger.info("Database connection closed.")
    }
}
