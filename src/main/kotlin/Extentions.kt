fun String.hex(): String = bytesToString(bytesToHex(bytes()))

fun String.sha256(): String = bytesToString(bytesToHex(SHA256Digest.digest(bytes())))

fun String.characters(): CharArray = map { it }.toCharArray()

fun String.bytes(): ByteArray = map { it.toByte() }.toByteArray()