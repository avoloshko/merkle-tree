import kotlin.math.floor

class MerkleTree(vararg input: ByteArray,
                 val hash: (ByteArray) -> ByteArray = { data -> SHA256Digest.instance.digest(data) }) {

    private val elements = input.map { hash(it) }.sortedWith(byteArrayComparator)
    private val layers = layers(elements.toList())

    fun root(): ByteArray {
        return layers.last().first()
    }

    fun hexRoot(): ByteArray {
        return bytesToHex(root())
    }

    fun containsLeaf(el: ByteArray): Boolean = elements.binarySearch(el, byteArrayComparator) >= 0

    fun containsElement(el: ByteArray): Boolean = containsLeaf(hash(el))

    fun proofForLeaf(el: ByteArray): List<ByteArray> {
        var idx = elements.binarySearch(el, byteArrayComparator)
        if (idx < 0) {
            throw IllegalArgumentException("Element ${bytesToString(bytesToHex(el))} does not exist in Merkle tree")
        }

        val res = mutableListOf<ByteArray>()

        layers.forEach { next ->
            val pairElement = pairElement(idx, next)

            if (pairElement != null) {
                res.add(pairElement);
            }
            idx = floor(idx / 2.0).toInt()
        }

        return res
    }

    fun hexProofForLeaf(el: ByteArray): List<ByteArray> {
        val proof = proofForLeaf(el);

        return proof.map { bytesToHex(it) }
    }

    fun proofForElement(el: ByteArray): List<ByteArray> {
        return proofForLeaf(hash(el))
    }

    fun hexProofForElement(el: ByteArray): List<ByteArray> {
        return proofForLeaf(hash(el)).map { bytesToHex(it) }
    }

    private fun pairElement(idx: Int, layer: List<ByteArray>): ByteArray? {
        val pairIdx = if (idx % 2 == 0) idx + 1 else idx - 1;

        if (pairIdx < layer.size) {
            return layer.get(pairIdx)
        } else {
            return null;
        }
    }

    private fun layers(elements: List<ByteArray>): List<List<ByteArray>> {
        if (elements.isEmpty()) {
            return emptyList()
        }

        val layers = mutableListOf<List<ByteArray>>()
        layers.add(elements);

        // Get next layer until we reach the root
        while (layers[layers.size - 1].size > 1) {
            layers.add(nextLayer(layers[layers.size - 1]));
        }

        return layers;
    }

    private fun nextLayer(elements: List<ByteArray>): List<ByteArray> {
        val res = mutableListOf<ByteArray>()

        elements.forEachIndexed({ idx, next ->
            if (idx % 2 == 0) {
                res.add(combinedHash(next, elements.elementAtOrNull(idx + 1), hash))
            }
        })
        return res
    }

    companion object {
        private fun combinedHash(d1: ByteArray, d2: ByteArray?, hash: (ByteArray) -> ByteArray): ByteArray {
            if (d2 == null) {
                return d1
            }

            val list = listOf(d1, d2).sortedWith(byteArrayComparator)
            return hash(list.first().plus(list.last()))
        }

        fun verifyProof(proof: List<ByteArray>, root: ByteArray, leaf: ByteArray, hash: (ByteArray) -> ByteArray): Boolean {
            var computedHash = leaf
            for (proofElement in proof) {
                if (byteArrayComparator.compare(computedHash, proofElement) < 0) {
                    computedHash = combinedHash(computedHash, proofElement, hash)
                } else {
                    computedHash = combinedHash(proofElement, computedHash, hash)
                }
            }

            return byteArrayComparator.compare(computedHash, root) == 0
        }

        private val byteArrayComparator = Comparator<ByteArray> { a, b ->
            var res = 0
            for (i in 0 until a.size) {
                val cmp = a.get(i).toChar().compareTo(b.get(i).toChar())
                if (cmp != 0) {
                    res = cmp
                    break
                }
            }
            res
        }
    }
}