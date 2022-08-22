package com.example.androidthings.gattserver

object SinglePacketManager {
    fun sendWifiHotSpotPacket(success: Boolean): ByteArray {
        val output = ByteArray(3)
        output[0] = 0x02
        output[1] = 0x00
        if (!success) {
            output[2] = 0x00 // 0x01 Success, 0x00 Fail
        } else {
            output[2] = 0x01 // 0x01 Success, 0x00 Fail
        }
        return output
    }

    fun sendInstallComplete(success: Boolean): ByteArray {
        val output = ByteArray(3)
        output[0] = 0x03
        output[1] = 0x00
        if (!success) {
            output[2] = 0x00 // 0x01 Success, 0x00 Fail
        } else {
            output[2] = 0x01 // 0x01 Success, 0x00 Fail
        }
        return output
    }
}