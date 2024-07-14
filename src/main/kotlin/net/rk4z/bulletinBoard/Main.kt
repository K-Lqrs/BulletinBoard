import java.security.MessageDigest

fun sha512Hash(input: String): String {
    val bytes = input.toByteArray()
    val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
    return digest.joinToString("") { "%02x".format(it) }
}

fun main() {
    val input = "Hello, World!"
    val hash = sha512Hash(input)
    println(hash)
}