package net.rk4z.bulletinboard

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.rk4z.bulletinboard.utils.ShortUUID
import net.rk4z.bulletinboard.utils.JsonUtil
import net.rk4z.bulletinboard.utils.Post
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone
import java.util.UUID

@Suppress("SqlNoDataSourceInspection", "SqlDialectInspection", "LoggingSimilarMessage", "DuplicatedCode")
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

        val createPostsTableSQL = """
            CREATE TABLE IF NOT EXISTS posts (
                id TEXT PRIMARY KEY,    
                author TEXT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                isAnonymous BOOLEAN NOT NULL DEFAULT FALSE,
                date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()

        val createDeletedPostsTableSQL = """
            CREATE TABLE IF NOT EXISTS posts (
                id TEXT PRIMARY KEY,    
                author TEXT NOT NULL,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                isAnonymous BOOLEAN NOT NULL DEFAULT FALSE,
                date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()

        val createConfigTableSQL = """
            CREATE TABLE IF NOT EXISTS config (
                key TEXT PRIMARY KEY,
                value TEXT NOT NULL
            );
        """.trimIndent()

        val createSettingsTableSQL ="""
            CREATE TABLE IF NOT EXISTS playerSettings (
                uuid TEXT NOT NULL,
                settingKey TEXT NOT NULL,
                settingValue TEXT NOT NULL,
                PRIMARY KEY (uuid, settingKey)
            );
        """.trimIndent()

        try {
            connection?.createStatement()?.use { statement ->
                statement.execute(createPostsTableSQL)
                statement.execute(createDeletedPostsTableSQL)
                statement.execute(createConfigTableSQL)
                statement.execute(createSettingsTableSQL)
                plugin.logger.info("Requirement tables created successfully!")
            }
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
            if (connection != null) {
                connection?.close()
                plugin.log.info("Successfully closed the SQLite database connection!")
            }
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

    fun importDataFromJson(file: File) {
        val data = JsonUtil.loadFromFile(file)
        data.posts.forEach { post ->
            insertPost(post, true)
        }
        plugin.log.info("Data imported from JSON to SQLite successfully!")
        setMigrationFlag()
    }

    private fun setMigrationFlag() {
        val insertSQL = "INSERT OR REPLACE INTO config (key, value) VALUES ('data_migrated', 'true')"
        try {
            connection?.prepareStatement(insertSQL)?.use { statement ->
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            plugin.log.error("Could not set migration flag in config table!")
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
            plugin.log.error("Could not check migration flag in config table!")
            e.printStackTrace()
            false
        }
    }

//>--------------------------------------------------------------------------------------------------<

    fun insertPost(post: Post, isOldData: Boolean = false) {
        val columns = mutableListOf("id", "author", "title", "content")
        val values = mutableListOf("?", "?", "?", "?")

        post.isAnonymous?.let {
            columns.add("isAnonymous")
            values.add("?")
        }

        if (isOldData) {
            columns.add("date")
            values.add("?")
        }

        val insertSQL = "INSERT INTO posts (${columns.joinToString(", ")}) VALUES (${values.joinToString(", ")})"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        try {
            connection?.prepareStatement(insertSQL)?.use { statement ->
                val idString: ShortUUID = if (isOldData) {
                    ShortUUID.fromString(post.id.toString())
                } else {
                    ShortUUID.fromShortString(post.id.toShortString())
                }

                statement.setString(1, idString.toShortString())
                statement.setString(2, post.author.toString())
                statement.setString(3, GsonComponentSerializer.gson().serialize(post.title))
                statement.setString(4, GsonComponentSerializer.gson().serialize(post.content))

                post.isAnonymous?.let {
                    statement.setBoolean(5, it)
                }

                if (isOldData) {
                    val dateIndex = if (post.isAnonymous != null) 6 else 5
                    statement.setString(dateIndex, dateFormat.format(post.date))
                }

                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            plugin.log.error("Could not insert post into database!")
            e.printStackTrace()
        }
    }

    fun deletePost(id: String) {
        val selectSQL = "SELECT * FROM posts WHERE id = ?"
        val deleteSQL = "DELETE FROM posts WHERE id = ?"
        val insertDeletedSQL = """
        INSERT INTO deletedPosts (id, author, title, content, isAnonymous, date) 
        VALUES (?, ?, ?, ?, ?, ?)
    """.trimIndent()

        try {
            connection?.prepareStatement(selectSQL)?.use { selectStatement ->
                selectStatement.setString(1, id)
                val resultSet = selectStatement.executeQuery()
                if (resultSet.next()) {
                    val postIdResult = resultSet.getString("id")
                    val postId = ShortUUID.fromShortString(postIdResult)

                    val dateString = resultSet.getString("date")
                    val parsedDate = ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME).toInstant()

                    val post = Post(
                        id = postId,
                        author = UUID.fromString(resultSet.getString("author")),
                        title = GsonComponentSerializer.gson().deserialize(resultSet.getString("title")),
                        content = GsonComponentSerializer.gson().deserialize(resultSet.getString("content")),
                        isAnonymous = resultSet.getBoolean("isAnonymous"),
                        date = Date.from(parsedDate)
                    )

                    connection?.prepareStatement(insertDeletedSQL)?.use { insertStatement ->
                        insertStatement.setString(1, post.id.toString())
                        insertStatement.setString(2, post.author.toString())
                        insertStatement.setString(3, GsonComponentSerializer.gson().serialize(post.title))
                        insertStatement.setString(4, GsonComponentSerializer.gson().serialize(post.content))
                        insertStatement.setBoolean(5, post.isAnonymous!!) // !! is safe here because we know it is not null
                        insertStatement.setString(6, dateString)
                        insertStatement.executeUpdate()
                    }

                    connection?.prepareStatement(deleteSQL)?.use { deleteStatement ->
                        deleteStatement.setString(1, id)
                        deleteStatement.executeUpdate()
                    }

                    plugin.log.info("Post moved to deletedPosts and deleted from posts.")
                } else {
                    plugin.log.warn("No post found with the given ID.")
                }
            }
        } catch (e: SQLException) {
            plugin.log.error("Could not delete post from database!")
            e.printStackTrace()
        }
    }

    fun getAllPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        val selectSQL = "SELECT * FROM posts"

        try {
            connection?.prepareStatement(selectSQL)?.use { statement ->
                val resultSet = statement.executeQuery()
                while (resultSet.next()) {
                    val postIdString = resultSet.getString("id")
                    val postId = ShortUUID.fromShortString(postIdString)

                    val rawDate = resultSet.getString("date")
                    val parsedDate = try {
                        Date.from(ZonedDateTime.parse(rawDate, DateTimeFormatter.ISO_DATE_TIME).toInstant())
                    } catch (_: Exception) {
                        val formatter = SimpleDateFormat("yyyy:MM:dd_HH:mm:ss")
                        formatter.parse(rawDate)
                    }

                    val post = Post(
                        id = postId,
                        author = UUID.fromString(resultSet.getString("author")),
                        title = GsonComponentSerializer.gson().deserialize(resultSet.getString("title")),
                        content = GsonComponentSerializer.gson().deserialize(resultSet.getString("content")),
                        isAnonymous = resultSet.getBoolean("isAnonymous"),
                        date = parsedDate
                    )
                    posts.add(post)
                }
            }
        } catch (e: SQLException) {
            plugin.log.error("Could not retrieve posts from database!")
            e.printStackTrace()
        }

        return posts
    }

    fun getPostsByAuthor(authorId: UUID): List<Post> {
        val posts = mutableListOf<Post>()
        val selectSQL = "SELECT * FROM posts WHERE author = ?"

        try {
            connection?.prepareStatement(selectSQL)?.use { statement ->
                statement.setString(1, authorId.toString())
                val resultSet = statement.executeQuery()
                while (resultSet.next()) {
                    val postIdString = resultSet.getString("id")
                    val postId = ShortUUID.fromShortString(postIdString)

                    val rawDate = resultSet.getString("date")
                    val parsedDate = try {
                        ZonedDateTime.parse(rawDate, DateTimeFormatter.ISO_DATE_TIME).toInstant()
                    } catch (_: Exception) {
                        val formatter = SimpleDateFormat("yyyy:MM:dd_HH:mm:ss")
                        formatter.parse(rawDate).toInstant()
                    }

                    val post = Post(
                        id = postId,
                        author = UUID.fromString(resultSet.getString("author")),
                        title = GsonComponentSerializer.gson().deserialize(resultSet.getString("title")),
                        content = GsonComponentSerializer.gson().deserialize(resultSet.getString("content")),
                        isAnonymous = resultSet.getBoolean("isAnonymous"),
                        date = Date.from(parsedDate)
                    )
                    posts.add(post)
                }
            }
        } catch (e: SQLException) {
            plugin.log.error("Could not retrieve posts from database!")
            e.printStackTrace()
        }

        return posts
    }

    fun getPost(id: String): Post? {
        val selectSQL = "SELECT * FROM posts WHERE id = ?"

        return try {
            connection?.prepareStatement(selectSQL)?.use { statement ->
                statement.setString(1, id)
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    val postIdString = resultSet.getString("id")
                    val postId = ShortUUID.fromShortString(postIdString)

                    val rawDate = resultSet.getString("date")
                    val parsedDate = try {
                        Date.from(ZonedDateTime.parse(rawDate, DateTimeFormatter.ISO_DATE_TIME).toInstant())
                    } catch (_: Exception) {
                        val formatter = SimpleDateFormat("yyyy:MM:dd_HH:mm:ss")
                        formatter.parse(rawDate)
                    }

                    Post(
                        id = postId,
                        author = UUID.fromString(resultSet.getString("author")),
                        title = GsonComponentSerializer.gson().deserialize(resultSet.getString("title")),
                        content = GsonComponentSerializer.gson().deserialize(resultSet.getString("content")),
                        isAnonymous = resultSet.getBoolean("isAnonymous"),
                        date = parsedDate
                    )
                } else {
                    null
                }
            }
        } catch (e: SQLException) {
            plugin.log.error("Could not retrieve post from database!")
            e.printStackTrace()
            null
        }
    }

    fun getDeletedPost(id: String): Post? {
        val selectSQL = "SELECT * FROM deletedPosts WHERE id = ?"

        return try {
            connection?.prepareStatement(selectSQL)?.use { statement ->
                statement.setString(1, id)
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    val postIdString = resultSet.getString("id")
                    val postId = ShortUUID.fromShortString(postIdString)

                    val rawDate = resultSet.getString("date")
                    val parsedDate = try {
                        Date.from(ZonedDateTime.parse(rawDate, DateTimeFormatter.ISO_DATE_TIME).toInstant())
                    } catch (_: Exception) {
                        val formatter = SimpleDateFormat("yyyy:MM:dd_HH:mm:ss")
                        formatter.parse(rawDate)
                    }

                    Post(
                        id = postId,
                        author = UUID.fromString(resultSet.getString("author")),
                        title = GsonComponentSerializer.gson().deserialize(resultSet.getString("title")),
                        content = GsonComponentSerializer.gson().deserialize(resultSet.getString("content")),
                        isAnonymous = resultSet.getBoolean("isAnonymous"),
                        date = parsedDate
                    )
                } else {
                    null
                }
            }
        } catch (e: SQLException) {
            plugin.log.error("Could not retrieve deleted post from database!")
            e.printStackTrace()
            null
        }
    }

    fun getDeletedPostByAuthor(authorId: UUID): List<Post> {
        val posts = mutableListOf<Post>()
        val selectSQL = "SELECT * FROM deletedPosts WHERE author = ?"

        try {
            connection?.prepareStatement(selectSQL)?.use { statement ->
                statement.setString(1, authorId.toString())
                val resultSet = statement.executeQuery()
                while (resultSet.next()) {
                    val postIdString = resultSet.getString("id")
                    val postId = ShortUUID.fromShortString(postIdString)

                    val rawDate = resultSet.getString("date")
                    val parsedDate = try {
                        ZonedDateTime.parse(rawDate, DateTimeFormatter.ISO_DATE_TIME).toInstant()
                    } catch (_: Exception) {
                        val formatter = SimpleDateFormat("yyyy:MM:dd_HH:mm:ss")
                        formatter.parse(rawDate).toInstant()
                    }

                    val post = Post(
                        id = postId,
                        author = UUID.fromString(resultSet.getString("author")),
                        title = GsonComponentSerializer.gson().deserialize(resultSet.getString("title")),
                        content = GsonComponentSerializer.gson().deserialize(resultSet.getString("content")),
                        isAnonymous = resultSet.getBoolean("isAnonymous"),
                        date = Date.from(parsedDate)
                    )
                    posts.add(post)
                }
            }
        } catch (e: SQLException) {
            plugin.log.error("Could not retrieve deleted posts from database!")
            e.printStackTrace()
        }

        return posts
    }
}