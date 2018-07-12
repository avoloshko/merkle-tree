val HEX_CHAR_TABLE = byteArrayOf('0'.toByte(), '1'.toByte(), '2'.toByte(), '3'.toByte(), '4'.toByte(), '5'.toByte(), '6'.toByte(), '7'.toByte(), '8'.toByte(), '9'.toByte(), 'a'.toByte(), 'b'.toByte(), 'c'.toByte(), 'd'.toByte(), 'e'.toByte(), 'f'.toByte())

fun bytesToHex(bytes: ByteArray): ByteArray {
    val hex = ByteArray(2 * bytes.size)
    for (i in bytes.indices) {
        val v = bytes[i].toInt() and 0xFF
        hex[i * 2] = HEX_CHAR_TABLE[v.ushr(4)]
        hex[i * 2 + 1] = HEX_CHAR_TABLE[v and 0xF]
    }
    return hex
}

fun bytesToString(bytes: ByteArray): String {
    var res = ""
    bytes.forEach {
        res += it.toChar()
    }
    return res
}

fun hexCharToByte(ch: Char): Byte {
    if (ch in '0'..'9') return (ch - '0').toByte()
    if (ch in 'A'..'F') return (ch - 'A' + 10).toByte()
    return if (ch in 'a'..'f') (ch - 'a' + 10).toByte() else -1
}

fun hexToBytes(bytes: ByteArray): ByteArray {
    if (bytes.size % 2 != 0)
        throw IllegalArgumentException("hexBinary needs to be even-length")

    val out = ByteArray(bytes.size / 2)

    var i = 0
    while (i < bytes.size) {
        val h = bytes[i].toInt()
        val l = bytes[i + 1].toInt()
        if (h == -1 || l == -1)
            throw IllegalArgumentException("contains illegal character for hexBinary")

        out[i / 2] = (h * 16 + l).toByte()
        i += 2
    }
    return out
}

fun getReverseHex(data: ByteArray): String {
    return bytesToString(bytesToHex(data.reverse()))
}

fun ByteArray.reverse(): ByteArray {
    var i = 0
    var j = size - 1
    var tmp: Byte

    while (j > i) {
        tmp = this[j]
        this[j] = this[i]
        this[i] = tmp
        j--
        i++
    }

    return this
}