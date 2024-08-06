package net.rk4z.bulletinBoard.utils

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.rk4z.bulletinBoard.BulletinBoard
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DuplicatedCode")
class DataBase(private val plugin: BulletinBoard) {
    private var connection: Connection? = null

    fun connectToDatabase(): Boolean {
        val url = "jdbc:sqlite:${plugin.dataFolder.absolutePath}/database.db"

        try {
            connection = DriverManager.getConnection(url)
            plugin.logger.info("Successfully connected to the SQLite database!")
            return true
        } catch (e: SQLException) {
            plugin.logger.error("Could not connect to the SQLite database!")
            e.printStackTrace()
            return false
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

        val createConfigTableSQL = """
            CREATE TABLE IF NOT EXISTS config (
                key TEXT PRIMARY KEY,
                value TEXT NOT NULL
            );
        """.trimIndent()

        try {
            connection?.createStatement()?.use { statement ->
                statement.execute(createPostsTableSQL)
                statement.execute(createPermissionsTableSQL)
                statement.execute(createDeletedPostsTableSQL)
                statement.execute(createConfigTableSQL)
                plugin.logger.info("Requirement tables created successfully!")
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not create requirement tables!")
            e.printStackTrace()
        }
    }

    fun closeConnection() {
        connection?.close()
        plugin.logger.info("Database connection closed.")
    }

    fun importDataFromJson(file: File) {
        val data = JsonUtil.loadFromFile(file)
        data.posts.forEach { post ->
            insertPost(post)
        }
        plugin.logger.info("Data imported from JSON to SQLite successfully!")
        setMigrationFlag()
    }

    private fun setMigrationFlag() {
        val insertSQL = "INSERT OR REPLACE INTO config (key, value) VALUES ('data_migrated', 'true')"
        try {
            connection?.prepareStatement(insertSQL)?.use { statement ->
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not set migration flag in config table!")
            e.printStackTrace()
        }
    }

    fun isDataMigrated(): Boolean {
        val selectSQL = "SELECT value FROM config WHERE key = 'data_migrated'"
        return try {
            connection?.prepareStatement(selectSQL)?.use { statement ->
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    resultSet.getString("value") == "true"
                } else {
                    false
                }
            } ?: false
        } catch (e: SQLException) {
            plugin.logger.error("Could not check migration flag in config table!")
            e.printStackTrace()
            false
        }
    }


//>-----------------------------------------------<\\

    fun insertPost(post: Post) {
        val insertSQL = "INSERT INTO posts (id, author, title, content, date) VALUES (?, ?, ?, ?, ?)"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        try {
            connection?.prepareStatement(insertSQL)?.use { statement ->
                statement.setString(1, post.id.toShortString())
                statement.setString(2, post.author.toString())
                statement.setString(3, GsonComponentSerializer.gson().serialize(post.title))
                statement.setString(4, GsonComponentSerializer.gson().serialize(post.content))
                statement.setString(5, dateFormat.format(post.date))
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not insert post into database!")
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
                        insertStatement.setString(5, post.date.toString())
                        insertStatement.executeUpdate()
                    }

                    connection?.prepareStatement(deleteSQL)?.use { deleteStatement ->
                        deleteStatement.setString(1, id.toShortString())
                        deleteStatement.executeUpdate()
                    }

                    plugin.logger.info("Post moved to deletedPosts and deleted from posts.")
                } else {
                    plugin.logger.warn("No post found with the given ID.")
                }
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not delete post from database!")
            e.printStackTrace()
        }
    }
}
