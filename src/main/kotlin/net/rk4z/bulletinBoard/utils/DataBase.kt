package net.rk4z.bulletinBoard.utils

import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.rk4z.bulletinBoard.BulletinBoard
import net.rk4z.bulletinBoard.utils.CustomID.Companion.dynamicIds
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("DuplicatedCode", "LoggingSimilarMessage", "SqlNoDataSourceInspection")
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
                isAnonymous BOOLEAN NOT NULL DEFAULT FALSE,
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
                isAnonymous BOOLEAN NOT NULL DEFAULT FALSE,
                date DATE NOT NULL
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
                statement.execute(createPermissionsTableSQL)
                statement.execute(createDeletedPostsTableSQL)
                statement.execute(createConfigTableSQL)
                statement.execute(createSettingsTableSQL)
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
            insertPost(post, true)
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

    fun insertPost(post: Post, isOldData: Boolean = false) {
        val insertSQL = "INSERT INTO posts (id, author, title, content, isAnonymous, date) VALUES (?, ?, ?, ?, ?, ?)"
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
                statement.setBoolean(5, post.isAnonymous)
                statement.setString(6, dateFormat.format(post.date))
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not insert post into database!")
            e.printStackTrace()
        }
    }

    fun deletePost(id: String) {
        val selectSQL = "SELECT * FROM posts WHERE id = ?"
        val deleteSQL = "DELETE FROM posts WHERE id = ?"
        val insertDeletedSQL = "INSERT INTO deletedPosts (id, author, title, content, date) VALUES (?, ?, ?, ?, ?)"

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
                        insertStatement.setBoolean(5, post.isAnonymous)
                        insertStatement.setString(6, dateString)
                        insertStatement.executeUpdate()
                    }

                    connection?.prepareStatement(deleteSQL)?.use { deleteStatement ->
                        deleteStatement.setString(1, id)
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

    fun deletePostPermanently(id: String) {
        val deleteSQL = "DELETE FROM deletedPosts WHERE id = ?"

        try {
            connection?.prepareStatement(deleteSQL)?.use { statement ->
                statement.setString(1, id)
                val rowsAffected = statement.executeUpdate()
                if (rowsAffected > 0) {
                    plugin.logger.info("Post with id $id deleted permanently.")
                } else {
                    plugin.logger.warn("No post found with the given ID.")
                }
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not delete post permanently from database!")
            e.printStackTrace()
        }
    }

    fun restorePost(id: String) {
        val selectSQL = "SELECT * FROM deletedPosts WHERE id = ?"
        val insertSQL = "INSERT INTO posts (id, author, title, content, isAnonymous, date) VALUES (?, ?, ?, ?, ?, ?)"  // isAnonymousを追加
        val deleteSQL = "DELETE FROM deletedPosts WHERE id = ?"

        try {
            connection?.prepareStatement(selectSQL)?.use { selectStatement ->
                selectStatement.setString(1, id)
                val resultSet = selectStatement.executeQuery()
                if (resultSet.next()) {
                    val postId = resultSet.getString("id")
                    val author = UUID.fromString(resultSet.getString("author"))
                    val title = GsonComponentSerializer.gson().deserialize(resultSet.getString("title"))
                    val content = GsonComponentSerializer.gson().deserialize(resultSet.getString("content"))
                    val isAnonymous = resultSet.getBoolean("isAnonymous")  // isAnonymousを取得
                    val rawDate = resultSet.getString("date")
                    val parsedDate = try {
                        ZonedDateTime.parse(rawDate, DateTimeFormatter.ISO_DATE_TIME).toInstant()
                    } catch (_: Exception) {
                        val formatter = SimpleDateFormat("yyyy:MM:dd_HH:mm:ss")
                        formatter.parse(rawDate).toInstant()
                    }

                    val zonedDateTime = ZonedDateTime.ofInstant(parsedDate, ZoneId.of("UTC"))
                    val formattedDate = DateTimeFormatter.ISO_DATE_TIME.format(zonedDateTime)

                    connection?.prepareStatement(insertSQL)?.use { insertStatement ->
                        insertStatement.setString(1, postId)
                        insertStatement.setString(2, author.toString())
                        insertStatement.setString(3, GsonComponentSerializer.gson().serialize(title))
                        insertStatement.setString(4, GsonComponentSerializer.gson().serialize(content))
                        insertStatement.setBoolean(5, isAnonymous)  // isAnonymousを挿入
                        insertStatement.setString(6, formattedDate)
                        insertStatement.executeUpdate()
                    }

                    connection?.prepareStatement(deleteSQL)?.use { deleteStatement ->
                        deleteStatement.setString(1, id)
                        deleteStatement.executeUpdate()
                    }

                    plugin.logger.info("Post with ID $id has been restored from deletedPosts to posts.")
                } else {
                    plugin.logger.warn("No post found with the given ID in deletedPosts.")
                }
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not restore post from deletedPosts to posts!")
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
                        date = parsedDate
                    )
                    posts.add(post)
                }
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not retrieve posts from database!")
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
                        date = Date.from(parsedDate)
                    )
                    posts.add(post)
                }
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not retrieve posts from database!")
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
                        date = parsedDate
                    )
                } else {
                    null
                }
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not retrieve post from database!")
            e.printStackTrace()
            null
        }
    }

    fun getDeletedPostsByAuthor(authorId: UUID): List<Post> {
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
                        date = Date.from(parsedDate)
                    )
                    posts.add(post)
                }
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not retrieve deleted posts from database!")
            e.printStackTrace()
        }

        return posts
    }

    fun getDeletedPostsByID(id: String): Post? {
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
                        date = parsedDate
                    )
                } else {
                    null
                }
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not retrieve deleted post from database!")
            e.printStackTrace()
            null
        }
    }

    private fun loadAllPostIds() {
        val selectSQL = "SELECT id FROM posts UNION SELECT id FROM deletedPosts"

        try {
            connection?.prepareStatement(selectSQL)?.use { statement ->
                val resultSet = statement.executeQuery()
                while (resultSet.next()) {
                    dynamicIds.add(resultSet.getString("id"))
                }
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not retrieve post ids from database!")
            e.printStackTrace()
        }
    }

    fun getAllIds(): Set<String> {
        loadAllPostIds()
        return CustomID.getAllEnumNames() + dynamicIds
    }

    fun getPlayerPermission(uuid: UUID): List<String> {
        val permissions = mutableListOf<String>()
        val selectSQL = "SELECT acquiredPermission FROM permissions WHERE uuid = ?"

        try {
            connection?.prepareStatement(selectSQL)?.use { statement ->
                statement.setString(1, uuid.toString())
                val resultSet = statement.executeQuery()
                while (resultSet.next()) {
                    permissions.add(resultSet.getString("acquiredPermission"))
                }
            }
        } catch (e: SQLException) {
            plugin.logger.error("Could not retrieve permissions from database!")
            e.printStackTrace()
        }

        return permissions
    }
}
