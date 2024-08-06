package net.rk4z.bulletinBoard.utils

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.rk4z.bulletinBoard.BulletinBoard
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

@Suppress("DuplicatedCode")
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

    fun createRequirementTables() {
        val createPostsTableSQL = """
            CREATE TABLE IF NOT EXISTS posts (
                id TEXT PRIMARY KEY,
                author TEXT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                date DATE NOT NULL
            );
        """.trimIndent()

        val createPermissionsTableSQL = """
            CREATE TABLE IF NOT EXISTS permissions (
                uuid TEXT PRIMARY KEY,
                acquiredPermission TEXT NOT NULL
            );
        """.trimIndent()

        val createDeletedPostsTableSQL = """
            CREATE TABLE IF NOT EXISTS deletedPosts (
                id TEXT PRIMARY KEY,
                author TEXT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                date DATE NOT NULL
            );
        """.trimIndent()

        try {
            connection?.createStatement()?.use { statement ->
                statement.execute(createPostsTableSQL)
                statement.execute(createPermissionsTableSQL)
                statement.execute(createDeletedPostsTableSQL)
                plugin.logger.info("Requirement tables created successfully!")
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Could not create requirement tables!")
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
        val selectSQL = "SELECT * FROM posts WHERE id = ?"
        val deleteSQL = "DELETE FROM posts WHERE id = ?"
        val insertDeletedSQL = "INSERT INTO deletedPosts (id, author, title, content, date) VALUES (?, ?, ?, ?, ?)"

        try {
            connection?.prepareStatement(selectSQL)?.use { selectStatement ->
                selectStatement.setString(1, id.toShortString())
                val resultSet = selectStatement.executeQuery()
                if (resultSet.next()) {
                    val post = Post(
                        id = id,
                        author = UUID.fromString(resultSet.getString("author")),
                        title = GsonComponentSerializer.gson().deserialize(resultSet.getString("title")),
                        content = GsonComponentSerializer.gson().deserialize(resultSet.getString("content")),
                        date = resultSet.getDate("date")
                    )

                    connection?.prepareStatement(insertDeletedSQL)?.use { insertStatement ->
                        insertStatement.setString(1, post.id.toShortString())
                        insertStatement.setString(2, post.author.toString())
                        insertStatement.setString(3, GsonComponentSerializer.gson().serialize(post.title))
                        insertStatement.setString(4, GsonComponentSerializer.gson().serialize(post.content))
                        insertStatement.setDate(5, post.date)
                        insertStatement.executeUpdate()
                    }

                    connection?.prepareStatement(deleteSQL)?.use { deleteStatement ->
                        deleteStatement.setString(1, id.toShortString())
                        deleteStatement.executeUpdate()
                    }

                    plugin.logger.info("Post moved to deletedPosts and deleted from posts.")
                } else {
                    plugin.logger.warning("No post found with the given ID.")
                }
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
