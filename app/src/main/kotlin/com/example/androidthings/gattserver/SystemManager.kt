package com.example.androidthings.gattserver

import android.content.Context
import com.mitac.api.libs.MitacAPI

object SystemManager {
    fun openWifiHotspot(context: Context, ssid : String, password : String) : Boolean {
        MitacAPI.getInstance().netService.startSoftAp(context, ssid, password)
        if (!MitacAPI.getInstance().netService.isWifiApEnabled(context)) return false
        return true
    }

    fun closeWifiHotspot(context: Context) : Boolean {
        MitacAPI.getInstance().netService.stopSoftAp(context)
        if (!MitacAPI.getInstance().netService.isWifiApEnabled(context)) return true
        return false
    }
}