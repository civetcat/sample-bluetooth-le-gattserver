package com.example.androidthings.gattserver

object MultiPacketManager {
    fun calculateTotalLength(string: String): Byte {
        val result = string.length / 15
        if (string.length % 15 == 0) {
            return result.toByte()
        }
        return (result + 1).toByte()
    }

    fun sendTotalLength(value: Int): ByteArray {
        val output = ByteArray(4)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = 0x00 // first packet index always be 0
        output[3] = value.toByte()
        return output
    }

    fun sendModelNamePacket(sendPacket: ByteArray, index: Int): ByteArray {
        val output = ByteArray(20)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = index.toByte()
        output[3] = 0x00
        feedPacket(sendPacket, output)
        return output
    }

    fun sendSerialNumber(sendPacket: ByteArray, index: Int): ByteArray {
        val output = ByteArray(20)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = index.toByte()
        output[3] = 0x01

        feedPacket(sendPacket, output)
        return output
    }

    private fun feedPacket(sendPacket: ByteArray, output: ByteArray) {
        for (i in sendPacket.indices) {
            output[i + 4] = sendPacket[i]
        }
    }

    fun sendWifiSsidPacket(sendPacket: ByteArray, index: Int): ByteArray {
        val output = ByteArray(20)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = index.toByte()
        output[3] = 0x02
        feedPacket(sendPacket, output)
        return output
    }

    fun sendWifiPasswordPacket(sendPacket: ByteArray, index: Int): ByteArray {
        val output = ByteArray(20)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = index.toByte()
        output[3] = 0x03
        feedPacket(sendPacket, output)
        return output
    }
}