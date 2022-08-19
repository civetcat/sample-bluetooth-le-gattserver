package com.example.androidthings.gattserver

object MultiPacketManager {
    fun calculateTotalLength(string: String): Int {
        val result = string.length / 15
        if (string.length % 15 == 0) {
            return result
        }
        return result + 1
    }

    fun sendTotalLength(int: Int): ByteArray {
        val output = ByteArray(20)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = 0x00 // first packet index always be 0
        output[3] = 0x06 // test
        output[4] = 0x32
        output[4] = 0x31

        return output
    }

    fun sendMultiPacket(value: String, index: Int): ByteArray {
        val output = ByteArray(20)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = index.toByte()
        output[3] = 0x06 // test
        output[4] = 0x32
        output[4] = 0x31

        return output
    }
}