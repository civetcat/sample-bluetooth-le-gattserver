package com.example.androidthings.gattserver

object SinglePacketManager {

    // open wifi hotspot?
    fun sendWifiHotSpotPacket(sendPacket: ByteArray, index: Int, itemId: Int): ByteArray {
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

}