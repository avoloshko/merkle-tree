import kotlin.test.Test
import kotlin.test.assertEquals

class HexTest() {
    @Test
    fun test1() {
        assertEquals("5468697320697320612064696374696f6e617279", "This is a dictionary".hex())
    }

    @Test
    fun test2() {
        assertEquals("5468652074657374206672616d65776f726b2069732061627374726163746564207468726f7567682074686520417373657274657220636c6173732e", "The test framework is abstracted through the Asserter class.".hex())
    }

    @Test
    fun test3() {
        assertEquals("20", " ".hex())
    }
}
