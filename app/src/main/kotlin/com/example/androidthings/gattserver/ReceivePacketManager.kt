package com.example.androidthings.gattserver

import android.util.Log

private val TAG = ReceivePacketManager::class.simpleName

object ReceivePacketManager {
    fun parsingReceivePacketType(receiveMsg: String): BleCommand {
        when (receiveMsg) {
            // CDR Vendor info -> 0x01 0x00 0x00
            "010000" -> {
                Log.d(TAG, "CDR_VENDOR_INFO")
                return BleCommand.CDR_VENDOR_INFO
            }
            // Turn on Wifi hotspot -> 0x02 0x00 0x01
            "020001" -> {
                Log.d(TAG, "TURN_ON_CDR_HOTSPOT")
                return BleCommand.TURN_ON_CDR_HOTSPOT
            }
            // Turn off Wifi hotspot -> 0x02 0x00 0x00
            "020000" -> {
                Log.d(TAG, "TURN_OFF_CDR_HOTSPOT")
                return BleCommand.TURN_OFF_CDR_HOTSPOT
            }
            // Installation complete -> 0x03 0x00 0x00
            "030000" -> {
                Log.d(TAG, "INSTALLATION_COMPLETE")
                return BleCommand.INSTALLATION_COMPLETE
            }
        }
        return BleCommand.UNKNOWN
    }
}