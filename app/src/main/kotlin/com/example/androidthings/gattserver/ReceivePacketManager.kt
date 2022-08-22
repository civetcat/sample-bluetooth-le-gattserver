package com.example.androidthings.gattserver

object ReceivePacketManager {
    fun parsingReceivePacketType(receiveMsg: String): BleCommand {
        when (receiveMsg) {
            // CDR Vendor info -> 0x01 0x00 0x00
            "010000" -> {
                return BleCommand.CDR_VENDOR_INFO
            }
            // Turn on Wifi hotspot -> 0x02 0x00 0x01
            "020001" -> {
                return BleCommand.TURN_ON_CDR_HOTSPOT
            }
            // Turn off Wifi hotspot -> 0x02 0x00 0x00
            "020000" -> {
                return BleCommand.TURN_OFF_CDR_HOTSPOT
            }
            // Installation complete -> 0x03 0x00 0x00
            "030000" -> {
                return BleCommand.INSTALLATION_COMPLETE
            }
        }
        return BleCommand.UNKNOWN
    }
}