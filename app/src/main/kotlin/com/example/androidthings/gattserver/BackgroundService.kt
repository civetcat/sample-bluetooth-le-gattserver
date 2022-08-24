package com.example.androidthings.gattserver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast

private val TAG = BackgroundService::class.simpleName

class BackgroundService : Service() {

    override fun onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        Log.d(TAG, "onCreate")
        BleManager.init(this)
        BleManager.startBleServer()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy")
        val bluetoothAdapter = BleManager.bluetoothManager.adapter
        if (bluetoothAdapter.isEnabled) {
            BleManager.stopServer()
            BleManager.stopAdvertising()
        }
        super.onDestroy()
    }
}