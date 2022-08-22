import org.junit.Test

internal class ConvertDataTest {
    @Test
    fun byteArrayToHex() {
        val bytes = byteArrayOf(10, 2, 15, 11)

        for (b in bytes) {
            val st = String.format("%02X", b)
            println(st)
        }
    }
}