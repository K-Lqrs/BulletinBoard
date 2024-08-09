package net.rk4z.bulletinBoard.utils

import java.util.*

@Suppress("unused")
class ShortUUID
private constructor(private val uuid: UUID) {

    companion object {
        fun randomUUID(): ShortUUID {
            return ShortUUID(UUID.randomUUID())
        }

        fun fromString(uuidString: String): ShortUUID {
            return ShortUUID(UUID.fromString(uuidString))
        }

        fun fromShortString(shortString: String): ShortUUID {
            val bytes = Base64.getUrlDecoder().decode(shortString)
            val bb = java.nio.ByteBuffer.wrap(bytes)
            val high = bb.long
            val low = bb.long
            return ShortUUID(UUID(high, low))
        }

        fun fromUUID(uuid: UUID): ShortUUID {
            return ShortUUID(uuid)
        }
    }

    fun fromShortUUIDString(shortUUIDString: String): ShortUUID {
        return fromShortString(shortUUIDString)
    }

    fun toUUID(): UUID {
        return uuid
    }

    fun toShortString(): String {
        val bb = java.nio.ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bb.array())
    }

    override fun toString(): String {
        return uuid.toString()
    }
}
