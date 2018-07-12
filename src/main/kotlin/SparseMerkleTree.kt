import kotlin.math.pow

class SparseMerkleTree(input: Map<Int, ByteArray>,
                       val depth: Int = 256,
                       val hash: (ByteArray) -> ByteArray = { data -> SHA256Digest.digest(data) }) {

    val root: ByteArray
    private val defaultNodes: Array<ByteArray>
    private val tree: Array<Map<Int, ByteArray>>

    init {
        // validate data
        val max = 2.0.pow(depth - 1).toInt()
        if (input.size > max) {
            throw IndexOutOfBoundsException("There are too many leaves for the tree to build")
        }
        for (idx in input.keys) {
            if (idx < 0 || idx >= max) {
                throw IndexOutOfBoundsException()
            }
        }

        defaultNodes = defaultNodes(depth)
        if (input.isNotEmpty()) {
            tree = createTree(input, depth)
            root = tree.last().values.last()
        } else {
            tree = emptyArray()
            root = defaultNodes.last()
        }
    }

    private fun defaultNodes(depth: Int): Array<ByteArray> {
        val res = mutableListOf(ByteArray(32))
        for (level in 1 until depth) {
            val prev = res[level - 1]
            res += hash(prev + prev)
        }
        return res.toTypedArray()
    }

    private fun createTree(input: Map<Int, ByteArray>, depth: Int): Array<Map<Int, ByteArray>> {
        val tree = mutableListOf(input)
        var treeLevel = input.toSortedMap()
        for (level in 0 until (depth - 1)) {
            val nextLevel = mutableMapOf<Int, ByteArray>()
            var prevIndex = -1
            for ((index, value) in treeLevel) {
                if (index % 2 == 0) {
                    nextLevel[index / 2] = hash(value + defaultNodes[level])
                } else {
                    if (index == prevIndex + 1) {
                        nextLevel[index / 2] = hash(treeLevel[prevIndex]!! + value)
                    } else {
                        nextLevel[index / 2] = hash(defaultNodes[level] + value)
                    }
                }
                prevIndex = index
            }
            tree.add(nextLevel)
            treeLevel = nextLevel.toSortedMap()
        }

        return tree.toTypedArray()
    }

    fun proofForIndex(idx: Int): List<ByteArray> {
        var index = idx
        val proof = mutableListOf<ByteArray>()

        for (level in 0 until (depth - 1)) {
            val siblingIndex = if (index % 2 == 0) index + 1 else index - 1
            index /= 2
            if (tree[level].containsKey(siblingIndex)) {
                proof += tree[level][siblingIndex]!!
            } else {
                proof += defaultNodes[level]
            }
        }
        return proof
    }

    fun verifyProof(proof: List<ByteArray>, root: ByteArray, leaf: ByteArray, idx: Int): Boolean {
        var computedHash = leaf
        var i = idx
        for (proofElement in proof) {
            if (i % 2 == 0) {
                computedHash = hash(computedHash + proofElement)
            } else {
                computedHash = hash(proofElement + computedHash)
            }
            i /= 2;
        }

        return byteArrayComparator.compare(computedHash, root) == 0
    }

    companion object {
        private val byteArrayComparator = Comparator<ByteArray> { a, b ->
            var res = 0
            for (i in 0 until a.size) {
                val cmp = a[i].toChar().compareTo(b[i].toChar())
                if (cmp != 0) {
                    res = cmp
                    break
                }
            }
            res
        }
    }
}