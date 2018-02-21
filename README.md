# merkle-tree
Merkle Tree and Merkle proofs implementation in Kotlin

MerkleTree is implemented in Kotlin with `kotlin-platform-common` plugin which allows to compile the code to JavaScript.

Implemented methods:
```kotlin
MerkleTree(input: List<ByteArray>, hash: (ByteArray) -> ByteArray)
  root(): ByteArray
  hexRoot(): ByteArray
  containsLeaf(el: ByteArray): Boolean
  containsElement(el: ByteArray): Boolean
  proofForLeaf(el: ByteArray): List<ByteArray>
  hexProofForLeaf(el: ByteArray): List<ByteArray>
  proofForElement(el: ByteArray): List<ByteArray>
  hexProofForElement(el: ByteArray): List<ByteArray>MerkleTree

MerkleTree.verifyProof(proof: List<ByteArray>, root: ByteArray, leaf: ByteArray, hash: (ByteArray) -> ByteArray): Boolean
```

SHA-256 implementation is provided.
```kotlin
val merkleTree = MerkleTree(elements, { data -> SHA256Digest.instance.digest(data) })
```
