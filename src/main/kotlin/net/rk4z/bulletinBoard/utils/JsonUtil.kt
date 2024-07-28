package net.rk4z.bulletinBoard.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.kyori.adventure.text.Component
import net.rk4z.bulletinBoard.BulletinBoard
import java.io.File
import java.util.*

object JsonUtil {
    private val json = Json {
        prettyPrint = true
        serializersModule = SerializersModule {
            contextual(UUID::class, UUIDSerializer)
            contextual(Component::class, ComponentSerializer)
        }
    }

    private val logger = BulletinBoard.instance.logger

    fun loadFromFile(file: File): BulletinBoardData {
        return if (file.exists() && file.readText().isNotEmpty()) {
            try {
                json.decodeFromString(BulletinBoardData.serializer(), file.readText())
            } catch (e: Exception) {
                logger.info("Error parsing JSON: ${e.message}")
                BulletinBoardData(players = emptyList(), posts = emptyList())
            }
        } else {
            logger.info("Warning: JSON file is empty or does not exist.")
            BulletinBoardData(players = emptyList(), posts = emptyList())
        }
    }

    fun saveToFile(data: BulletinBoardData, file: File) {
        file.writeText(json.encodeToString(BulletinBoardData.serializer(), data))
    }
}