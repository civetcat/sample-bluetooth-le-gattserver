package com.example.androidthings.gattserver

import android.util.Log

private val TAG = MultiPacketManager::class.simpleName

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
        val output = ByteArray(30)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = index.toByte()
        output[3] = 0x00
        feedPacket(sendPacket, output)
        return output
    }

    fun sendSerialNumber(sendPacket: ByteArray, index: Int): ByteArray {
        val output = ByteArray(30)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = index.toByte()
        output[3] = 0x01

        feedPacket(sendPacket, output)
        return output
    }

    private fun feedPacket(sendPacket: ByteArray, output: ByteArray) {
        Log.d(TAG,"sendPacket : ${ConvertData.transferForPrint(sendPacket)}")
        for (i in sendPacket.indices) {
            Log.d(TAG, "packet : ${sendPacket[i]}")
            output[i + 4] = sendPacket[i]
        }

        Log.d(TAG,"output : ${String(output).trim()}")
    }

    fun sendWifiSsidPacket(wifiSsid: ByteArray, index: Int): ByteArray {
        val output = ByteArray(30)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = index.toByte()
        output[3] = 0x02
        feedPacket(wifiSsid, output)
        return output
    }

    fun sendWifiPasswordPacket(sendPacket: ByteArray, index: Int): ByteArray {
        val output = ByteArray(30)
        output[0] = 0x01
        output[1] = 0x00
        output[2] = index.toByte()
        output[3] = 0x03
        feedPacket(sendPacket, output)
        return output
    }
}