package com.example.androidthings.gattserver

val packetLength = 15

object MultiPacketManager {
    fun calculateTotalLength(string: String): Byte {
        val result = string.length / 15
        if (string.length % 15 == 0) {
            return result.toByte()
        }
        return (result + 1).toByte()
    }

    fun sendTotalLength(value: String): ByteArray {
        val output = ByteArray(4)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = 0x00 // first packet index always be 0
        output[3] = calculateTotalLength(value)
        return output
    }

    fun sendMultiPacket(sendPacket: ByteArray, index: Int, itemId: Int): ByteArray {
        val output = ByteArray(20)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = index.toByte()
        output[3] = itemId.toByte() // item id , temp set 01 for vendor info
        output[4] = 0x32
        output[4] = 0x31

        for (i in 4 until output.size) {
            output[i] = sendPacket[i]
        }

        return output
    }

    fun splitToSmallPacket(value: ByteArray, index: Int): ByteArray {
        val output = ByteArray(packetLength)
        for(i in packetLength * index until packetLength * (index + 1)) {
            output[i] = value[i]
            println(value[i])
        }
        return output
    }
}