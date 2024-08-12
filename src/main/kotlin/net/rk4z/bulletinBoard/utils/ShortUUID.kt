package net.rk4z.bulletinBoard.utils

import java.util.*

@Suppress("unused")
class ShortUUID
private constructor(private val uuid: UUID) {

    companion object {
        /**
         * Generates a new [UUID] (36 characters) and returns it as a [ShortUUID] type.
         *
         * @return New 36-characters [UUID] of type [ShortUUID]
         */
        fun randomUUID(): ShortUUID {
            return ShortUUID(UUID.randomUUID())
        }

        /**
         * Returns the correct [UUID] (36 characters, follow the UUID format) of type String as a [ShortUUID]
         */
        fun fromString(uuidString: String): ShortUUID {
            return ShortUUID(UUID.fromString(uuidString))
        }

        /**
         * Returns a String type ShortUUID (22 characters) as a 32-character ShortUUID type.
         */
        fun fromShortString(shortString: String): ShortUUID {
            val bytes = Base64.getUrlDecoder().decode(shortString)
            val bb = java.nio.ByteBuffer.wrap(bytes)
            val high = bb.long
            val low = bb.long
            return ShortUUID(UUID(high, low))
        }

        /**
         * Convert a [UUID] type to ShortUUID type.
         */
        fun fromUUID(uuid: UUID): ShortUUID {
            return ShortUUID(uuid)
        }
    }

    /**
     * This is a wrapper method of [fromShortString]
     */
    fun fromShortUUIDString(shortUUIDString: String): ShortUUID {
        return fromShortString(shortUUIDString)
    }

    /**
     * Returns the [UUID] generated by [ShortUUID] as is.
     */
    fun toUUID(): UUID {
        return uuid
    }

    /**
     * Returns a 32-characters UUID in a shorter 22-character format.
     */
    fun toShortString(): String {
        val bb = java.nio.ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bb.array())
    }

    /**
     * Returns the generated 32-characters UUID as a String
     */
    override fun toString(): String {
        return uuid.toString()
    }
}
