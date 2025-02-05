import com.example.androidthings.gattserver.ConvertData
import com.example.androidthings.gattserver.MultiPacketManager
import org.junit.Test

internal class MultiPacketManagerTest {

    @Test fun calculateTotalLength() {
        println("123456  separate to ${MultiPacketManager.calculateTotalLength("123456")} part")
        val resultLength = MultiPacketManager.calculateTotalLength("12123345345666777878992323223333345667222223467989043456345245qw734532451245357357700033111133")
        println("12123345345666777878992323223333345667222223467989043456345245qw734532451245357357700033111133 separate to : $resultLength part")
    }

    @Test fun sendTotalLength() {
        val byteArray = MultiPacketManager.sendTotalLength(4)
        for (i in byteArray) {
            println(i)
        }
    }

    @Test fun countMultiPacket() {
        val sendPacket = ConvertData.stringToByteArray("K245")
        val output = ByteArray(20)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = 0x11
        output[3] = 0x00
        println(sendPacket.size)
        for (i in sendPacket.indices) {
            output[i+4] = sendPacket[i]
        }
        println("size : ${output.count()}")

    }
}