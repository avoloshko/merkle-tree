import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MerkleTreeTest {
    val merkle = MerkleTree("elem1".bytes(), "elem2".bytes(), "elem3".bytes(), "elem4".bytes(), "elem5".bytes(), "elem2".bytes())

    @Test
    fun testRoot() {
        assertEquals("6982312644b56754fb401dea40745f70ab4690339374830aa6b49ccadc3dd0a4", bytesToString(merkle.hexRoot()))
    }

    @Test
    fun testContainsElem() {
        assertTrue(merkle.containsElement("elem1".bytes()))
        assertTrue(merkle.containsElement("elem2".bytes()))
        assertTrue(merkle.containsElement("elem3".bytes()))
        assertTrue(merkle.containsElement("elem4".bytes()))
        assertTrue(merkle.containsElement("elem5".bytes()))
        assertFalse(merkle.containsElement("elem6".bytes()))
    }

    @Test
    fun testProofSize() {
        val proof1 = merkle.proofForElement("elem1".bytes())
        assertEquals(1, proof1.size)

        val proof2 = merkle.proofForElement("elem2".bytes())
        assertEquals(3, proof2.size)

        val proof3 = merkle.proofForElement("elem3".bytes())
        assertEquals(3, proof3.size)
    }

    @Test
    fun testVerifyProof() {
        val proof1 = merkle.proofForElement("elem1".bytes())
        assertTrue(MerkleTree.verifyProof(proof1, merkle.root(), merkle.hash("elem1".bytes()), merkle.hash))
        assertFalse(MerkleTree.verifyProof(proof1, merkle.root(), merkle.hash("elem2".bytes()), merkle.hash))

        val proof2 = merkle.proofForElement("elem2".bytes())
        assertTrue(MerkleTree.verifyProof(proof2, merkle.root(), merkle.hash("elem2".bytes()), merkle.hash))
        assertFalse(MerkleTree.verifyProof(proof2, merkle.root(), merkle.hash("elem3".bytes()), merkle.hash))

        val proof3 = merkle.proofForElement("elem3".bytes())
        assertTrue(MerkleTree.verifyProof(proof3, merkle.root(), merkle.hash("elem3".bytes()), merkle.hash))
        assertFalse(MerkleTree.verifyProof(proof3, merkle.root(), merkle.hash("elem4".bytes()), merkle.hash))
    }

    @Test
    fun testVerifyProofWithWrongElem() {
        try {
            merkle.proofForElement("elem6".bytes())
        } catch (ex: IllegalArgumentException) {
            assertTrue(true)
            return
        }
        assertTrue(false)
    }
}