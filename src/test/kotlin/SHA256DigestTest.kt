import kotlin.test.Test
import kotlin.test.assertEquals

class SHA256DigestTest() {
    @Test
    fun test1() {
        assertEquals("59141a5181f305b329bfeaaf88a9af423db1792a3686c023c75284212054b69c", "This is a dictionary".sha256())
    }

    @Test
    fun test2() {
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", "".sha256())
    }

    @Test
    fun test3() {
        assertEquals("36a9e7f1c95b82ffb99743e0c5c4ce95d83c9a430aac59f84ef3cbfab6145068", " ".sha256())
    }

    @Test
    fun test4() {
        assertEquals("67a0a2aeb63143775084b0e0cad12dd6db56b08e1e036f9f38d581cca1f1a441", "The test framework is abstracted through the Asserter class.".sha256())
    }
}