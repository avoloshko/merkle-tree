object SHA256Digest {

    /**
     * Private reused array for representing a block of 64 bytes.
     */
    private val block = ByteArray(64)

    /**
     * Private reused array for representing 64 32 bit words.
     */
    private val words = IntArray(64)

    /**
     * Method hashing the message according to the
     * SHA-256 specification.
     *
     * @param data The data message to be hashed.
     * @return The 256 bit hash represented as a byte array.
     */
    fun digest(data: ByteArray): ByteArray {
        val padded = padMessage(data)

        val hs = HS.copyOf()

        for (i in 0 until padded.size / 64) {
            val registers = hs.copyOf()

            copyBytes(padded, 64 * i, block, 0, 64)

            setupWords()

            for (j in 0..63) {
                iterate(registers, words, j)
            }

            for (j in 0..7) {
                hs[j] += registers[j]
            }
        }

        val hash = ByteArray(32)

        for (i in 0..7) {
            copyBytes(intToBytes(hs[i]), 0, hash, 4 * i, 4)
        }

        return hash
    }

    /**
     * Sets up the words. The first 16 words are filled with
     * a copy of the 64 bytes currently being processed in the
     * hash loop. The 64 - 16 words depend on these values.
     */
    private fun setupWords() {
        for (j in 0..15) {
            words[j] = 0
            for (m in 0..3) {
                words[j] = words[j] or ((block[j * 4 + m].toInt() and 0x000000FF) shl 24 - m * 8)

            }
        }

        for (j in 16..63) {
            val s0 = rotateRight(words[j - 15], 7) xor
                    rotateRight(words[j - 15], 18) xor
                    words[j - 15].ushr(3)

            val s1 = rotateRight(words[j - 2], 17) xor
                    rotateRight(words[j - 2], 19) xor
                    words[j - 2].ushr(10)

            words[j] = words[j - 16] + s0 + words[j - 7] + s1
        }
    }

    /**
     * Takes a byte array representing a message to be
     * hashed and pads it according to the SHA-256
     * specification.
     *
     * @param data The data message to be padded.
     * @return The resulting padded message.
     */
    private fun padMessage(data: ByteArray): ByteArray {
        val length = data.size
        val tail = length % 64
        val padding: Int

        if (64 - tail >= 9) {
            padding = 64 - tail
        } else {
            padding = 128 - tail
        }

        val pad = ByteArray(padding)
        pad[0] = 0x80.toByte()
        val bits = (length * 8).toLong()
        for (i in 0..7) {
            pad[pad.size - 1 - i] = (bits.ushr(8 * i) and 0xFF).toByte()
        }

        val output = ByteArray(length + padding)
        copyBytes(data, 0, output, 0, length)
        copyBytes(pad, 0, output, length, pad.size)

        return output
    }

    /**
     * Turns the provided integer into four bytes represented
     * as an array.
     *
     * @param i The integer to be converted.
     * @return The resulting byte array of size 4.
     */
    fun intToBytes(i: Int): ByteArray {
        val b = ByteArray(4)
        for (c in 0..3) {
            b[c] = (i.ushr(56 - 8 * c) and 0xff).toByte()
        }
        return b
    }

    /**
     * Initial H values. These are the first 32
     * bits of the fractional parts of the square
     * roots of the first eight primes.
     */
    private val HS = intArrayOf(0x6a09e667, -0x4498517b, 0x3c6ef372, -0x5ab00ac6, 0x510e527f, -0x64fa9774, 0x1f83d9ab, 0x5be0cd19)

    /**
     * Initial K values. These are the first 32
     * bits of the fractional parts of the cube root
     * of the first 64 primes.
     */
    private val KS = intArrayOf(0x428a2f98, 0x71374491, -0x4a3f0431, -0x164a245b, 0x3956c25b, 0x59f111f1, -0x6dc07d5c, -0x54e3a12b, -0x27f85568, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, -0x7f214e02, -0x6423f959, -0x3e640e8c, -0x1b64963f, -0x1041b87a, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da, -0x67c1aeae, -0x57ce3993, -0x4ffcd838, -0x40a68039, -0x391ff40d, -0x2a586eb9, 0x06ca6351, 0x14292967, 0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, -0x7e3d36d2, -0x6d8dd37b, -0x5d40175f, -0x57e599b5, -0x3db47490, -0x3893ae5d, -0x2e6d17e7, -0x2966f9dc, -0xbf1ca7b, 0x106aa070, 0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3, 0x748f82ee, 0x78a5636f, -0x7b3787ec, -0x7338fdf8, -0x6f410006, -0x5baf9315, -0x41065c09, -0x398e870e)

    /**
     * The iteration is called 64 times for every block to be encrypted.
     * It updates the registers which later are used to generate the
     * message hash.
     *
     * @param registers The registers used represented by an int array of size 8.
     * @param words     The words used represented by an int array of size 64.
     * @param j         The current index.
     */
    private fun iterate(registers: IntArray, words: IntArray, j: Int) {
        val S0 = rotateRight(registers[0], 2) xor
                rotateRight(registers[0], 13) xor
                rotateRight(registers[0], 22)

        val maj = registers[0] and registers[1] xor (registers[0] and registers[2]) xor (registers[1] and registers[2])

        val temp2 = S0 + maj

        val S1 = rotateRight(registers[4], 6) xor
                rotateRight(registers[4], 11) xor
                rotateRight(registers[4], 25)

        val ch = registers[4] and registers[5] xor (registers[4].inv() and registers[6])

        val temp1 = registers[7] + S1 + ch + KS[j] + words[j]

        registers[7] = registers[6]
        registers[6] = registers[5]
        registers[5] = registers[4]
        registers[4] = registers[3] + temp1
        registers[3] = registers[2]
        registers[2] = registers[1]
        registers[1] = registers[0]
        registers[0] = temp1 + temp2
    }
}

fun rotateRight(i: Int, distance: Int): Int {
    return i.ushr(distance) or (i shl -distance)
}

fun copyBytes(src: ByteArray, srcPos: Int,
              dest: ByteArray, destPos: Int,
              length: Int) {
    for (i in 0 until length) {
        dest.set(destPos + i, src.get(srcPos + i))
    }
}
