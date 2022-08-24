package com.example.androidthings.gattserver

import android.content.Context
import android.util.Log
import com.example.androidthings.gattserver.BleManager.notifyRegisteredDevices

private val TAG = NotifyManager::class.simpleName

object NotifyManager {

    private const val VENDOR_INFO_COUNT = 4

    fun initContext(context: Context) {
        DeviceInfoManager.initContext(context)
    }

    fun sendVendorInfo() {
        Log.d(
            TAG,
            "SN: ${DeviceInfoManager.getSN()} , Model : ${DeviceInfoManager.getModel()} " +
                    ", ssid : ${DeviceInfoManager.getSsid()} , passwd : ${DeviceInfoManager.getPassword()}"
        )
        // Every info length need under 15 character
        val modelName = ConvertData.stringToByteArray(DeviceInfoManager.getModel())
        val serialNumber = ConvertData.stringToByteArray(DeviceInfoManager.getSN())
        val wifiSsid = ConvertData.stringToByteArray(DeviceInfoManager.getSsid())
        val wifiPasswd = ConvertData.stringToByteArray(DeviceInfoManager.getPassword())
        // 1. Send total string length
        MultiPacketManager.sendTotalLength(VENDOR_INFO_COUNT)
        // 2. Send several parts
        val modelNamePacket = MultiPacketManager.sendModelNamePacket(modelName, 0)
        val serialNumberPacket = MultiPacketManager.sendSerialNumber(serialNumber, 1)
        val wifiSsidPacket = MultiPacketManager.sendWifiSsidPacket(wifiSsid, 2)
        val wifiPasswdPacket = MultiPacketManager.sendWifiPasswordPacket(wifiPasswd, 3)

        notifyRegisteredDevices(modelNamePacket)
        notifyRegisteredDevices(serialNumberPacket)
        notifyRegisteredDevices(wifiSsidPacket)
        notifyRegisteredDevices(wifiPasswdPacket)
    }
}