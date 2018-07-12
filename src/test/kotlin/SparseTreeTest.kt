import kotlin.test.Test

class SparseTreeTest {
    @Test
    fun testInit0() {
        SparseMerkleTree(hashMapOf(0 to "0".bytes(), 5 to "1".bytes(), 7 to "2".bytes()),
                depth = 4);
    }

    @Test(expected = TreeSizeExceededException::class)
    fun testInit1() {
        SparseMerkleTree(hashMapOf(-1 to "0".bytes(), 1 to "1".bytes(), 2 to "2".bytes()),
                depth = 4);
    }

    @Test(expected = TreeSizeExceededException::class)
    fun testInit2() {
        SparseMerkleTree(hashMapOf(8 to "0".bytes(), 1 to "1".bytes(), 2 to "2".bytes()),
                depth = 4);
    }

    @Test
    fun testInit3() {
        SparseMerkleTree(hashMapOf(
                1 to "1".bytes(),
                2 to "2".bytes(),
                4 to "4".bytes(),
                5 to "5".bytes()),
                depth = 4);
    }

    @Test(expected = TreeSizeExceededException::class)
    fun testInit4() {
        SparseMerkleTree(hashMapOf(0 to "0".bytes(),
                1 to "1".bytes(),
                2 to "2".bytes(),
                3 to "3".bytes(),
                4 to "4".bytes(),
                5 to "5".bytes(),
                6 to "6".bytes(),
                7 to "7".bytes(),
                8 to "8".bytes()),
                depth = 4);
    }

    @Test
    fun testProof() {
       val merkleTree = SparseMerkleTree(mapOf(
                5 to "5".bytes(),
                4 to "4".bytes(),
                2 to "2".bytes(),
                1 to "1".bytes()),
                depth = 4)

        assert(merkleTree.verifyProof(merkleTree.createMerkleProof(0).toList(),
                merkleTree.root, ByteArray(32), 0))
        assert(merkleTree.verifyProof(merkleTree.createMerkleProof(1).toList(),
                merkleTree.root, "1".bytes(), 1))
        assert(merkleTree.verifyProof(merkleTree.createMerkleProof(2).toList(),
                merkleTree.root, "2".bytes(), 2))
        assert(merkleTree.verifyProof(merkleTree.createMerkleProof(3).toList(),
                merkleTree.root, ByteArray(32), 3))
        assert(merkleTree.verifyProof(merkleTree.createMerkleProof(4).toList(),
                merkleTree.root, "4".bytes(), 4))
        assert(merkleTree.verifyProof(merkleTree.createMerkleProof(5).toList(),
                merkleTree.root, "5".bytes(), 5))
        assert(merkleTree.verifyProof(merkleTree.createMerkleProof(6).toList(),
                merkleTree.root, ByteArray(32), 6))
        assert(merkleTree.verifyProof(merkleTree.createMerkleProof(7).toList(),
                merkleTree.root, ByteArray(32), 7))
    }
}