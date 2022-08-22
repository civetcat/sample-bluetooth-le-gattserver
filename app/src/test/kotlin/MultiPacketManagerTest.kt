import com.example.androidthings.gattserver.MultiPacketManager
import org.junit.Test

internal class MultiPacketManagerTest {

    @Test fun calculateTotalLength() {
        println("123456  separate to ${MultiPacketManager.calculateTotalLength("123456")} part")
        val resultLength = MultiPacketManager.calculateTotalLength("12123345345666777878992323223333345667222223467989043456345245qw734532451245357357700033111133")
        println("12123345345666777878992323223333345667222223467989043456345245qw734532451245357357700033111133 separate to : $resultLength part")
    }

    @Test fun sendTotalLength() {
        val byteArray = MultiPacketManager.sendTotalLength("12345678901234567890")
        for (i in byteArray) {
            println(i)
        }
    }

    @Test fun sendMultiPacket() {

    }
}