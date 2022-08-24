package com.example.androidthings.gattserver

import android.content.Context
import com.mitac.api.libs.MitacAPI

object NetworkManager {
    fun openWifiHotspot(context: Context, ssid: String, password: String) {
        MitacAPI.getInstance().netService.startSoftAp(context, ssid, password)
    }

    fun closeWifiHotspot(context: Context) {
        MitacAPI.getInstance().netService.stopSoftAp(context)
    }

    fun isWifiHotspotOn(context: Context): Boolean {
        if (MitacAPI.getInstance().netService.isWifiApEnabled(context)) return true
        return false
    }
}