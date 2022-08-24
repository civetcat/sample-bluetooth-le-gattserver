package com.example.androidthings.gattserver

import android.app.Service
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.*
import android.os.Process.THREAD_PRIORITY_BACKGROUND
import android.util.Log
import android.widget.Toast

private val TAG = BackgroundService::class.simpleName

class BackgroundService : Service() {
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bleManager : BleManager

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

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        // If we get killed, after returning from here, restart
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // We don't provide binding, so return null
        return null
    }

    override fun onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show()
        val bluetoothAdapter = BleManager.bluetoothManager.adapter
        /*
        if (bluetoothAdapter.isEnabled) {
            BleManager.stopServer()
            BleManager.stopAdvertising()
        }
         */
    }
}