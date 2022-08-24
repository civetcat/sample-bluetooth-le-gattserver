package com.example.androidthings.gattserver

import android.content.Context
import com.mitac.api.libs.MitacAPI

object DeviceInfoManager {
    private lateinit var sn: String
    private val model = "K245"
    private lateinit var ssid: String
    private val passwd = "Gemini2020"

    fun initContext(context: Context) {
        sn = MitacAPI.getInstance().systemService.getSerialNumber(context)
        ssid = "CDR-#(${sn})"
    }

    fun getSsid(): String {
        return ssid
    }

    fun getPassword(): String {
        return passwd
    }

    fun getSN(): String {
        return sn
    }

    fun getModel(): String {
        return model
    }
}