package com.example.androidthings.gattserver

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.util.Log
import java.util.*

private val TAG = BleManager::class.simpleName

@SuppressLint("StaticFieldLeak")
object BleManager {
    /* Bluetooth API */
    lateinit var bluetoothManager: BluetoothManager
    private var bluetoothGattServer: BluetoothGattServer? = null

    /* Collection of notification subscribers */
    private val registeredDevices = mutableSetOf<BluetoothDevice>()
    private lateinit var context: Context

    fun init(_context: Context) {
        context = _context
        NotifyManager.initContext(context)
    }

    fun startBleServer() {
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        // We can't continue without proper Bluetooth support
        if (!checkBluetoothSupport(bluetoothAdapter)) {
            //finish()
        }

        // Register for system Bluetooth events
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(bluetoothReceiver, filter)
        if (!bluetoothAdapter.isEnabled) {
            Log.d(TAG, "Bluetooth is currently disabled...enabling")
            bluetoothAdapter.enable()
        } else {
            Log.d(TAG, "Bluetooth enabled...starting services")
            startAdvertising()
            startServer()
        }
    }

    /**
     * Listens for Bluetooth adapter events to enable/disable
     * advertising and server functionality.
     */
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)) {
                BluetoothAdapter.STATE_ON -> {
                    startAdvertising()
                    startServer()
                }
                BluetoothAdapter.STATE_OFF -> {
                    stopServer()
                    stopAdvertising()
                }
            }
        }
    }

    /**
     * Verify the level of Bluetooth support provided by the hardware.
     * @param bluetoothAdapter System [BluetoothAdapter].
     * @return true if Bluetooth is properly supported, false otherwise.
     */
    private fun checkBluetoothSupport(bluetoothAdapter: BluetoothAdapter?): Boolean {

        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported")
            return false
        }

        if (!context.packageManager!!.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.w(TAG, "Bluetooth LE is not supported")
            return false
        }

        return true
    }

    /**
     * Begin advertising over Bluetooth that this device is connectable
     * and supports the Current Time Service.
     */
    private fun startAdvertising() {
        val bluetoothLeAdvertiser: BluetoothLeAdvertiser? =
            bluetoothManager.adapter.bluetoothLeAdvertiser

        bluetoothLeAdvertiser?.let {
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build()

            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(ParcelUuid(ServiceProfile.USER_DATA_GATT_SERVICE))
                .build()

            it.startAdvertising(settings, data, advertiseCallback)
        } ?: Log.w(TAG, "Failed to create advertiser")
    }

    /**
     * Stop Bluetooth advertisements.
     */
    fun stopAdvertising() {
        val bluetoothLeAdvertiser: BluetoothLeAdvertiser? =
            bluetoothManager.adapter.bluetoothLeAdvertiser
        bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback) ?: Log.w(
            TAG,
            "Failed to create advertiser"
        )
    }

    /**
     * Initialize the GATT server instance with the services/characteristics
     * from the Time Profile.
     */
    private fun startServer() {
        bluetoothGattServer =
            bluetoothManager.openGattServer(context, gattServerCallback)
        bluetoothGattServer?.addService(ServiceProfile.createService())
        //bluetoothGattServer?.addService(TimeProfile.createTimeService())
            ?: Log.w(TAG, "Unable to create GATT server")
    }

    /**
     * Shut down the GATT server.
     */
    fun stopServer() {
        bluetoothGattServer?.close()
        context.unregisterReceiver(bluetoothReceiver)
    }

    /**
     * Callback to receive information about the advertisement process.
     */
    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.i(TAG, "LE Advertise Started.")
        }

        override fun onStartFailure(errorCode: Int) {
            Log.w(TAG, "LE Advertise Failed: $errorCode")
        }
    }

    /**
     * Callback to handle incoming requests to the GATT server.
     * All read/write requests for characteristics and descriptors are handled here.
     */
    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onMtuChanged(device: BluetoothDevice?, mtu: Int) {
            super.onMtuChanged(device, mtu)
            Log.i(TAG, "New MTU value is : $mtu")
        }

        override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
            super.onNotificationSent(device, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Sent notification")
            }
        }

        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: $device")
                NotifyManager.sendVendorInfo()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: $device")
                //Remove device from any active subscriptions
                registeredDevices.remove(device)
            }
        }

        override fun onCharacteristicReadRequest(
            device: BluetoothDevice, requestId: Int, offset: Int,
            characteristic: BluetoothGattCharacteristic
        ) {
            /*
            when (ServiceProfile.USER_DEF_CHAR) {
                characteristic.uuid -> {
                    Log.i(TAG, "Characteristic : User Index read")
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        ConvertData.stringToByteArray("test1")
                    )
                }
                else -> {
                    // Invalid characteristic
                    Log.w(TAG, "Invalid Characteristic Read: " + characteristic.uuid)
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null
                    )
                }
            }

             */
        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            Log.i(
                TAG, "Request id = $requestId " +
                        "device = $device" +
                        "offset = $offset" +
                        "characteristic = $characteristic"
            )
            var receiveValue = ""
            if (value != null) {
                Log.d(TAG, ConvertData.transferForPrint(value))
            }
            if (value != null) receiveValue = ConvertData.bytesToHex(value)
            Log.d(
                TAG, "onCharacteristicWriteRequest：requestId = $requestId, " +
                        "preparedWrite=$preparedWrite, " +
                        "responseNeeded=$responseNeeded, " +
                        "offset=$offset, " +
                        "value=$receiveValue"
            )

            // Parsing receive value
            val receiveType: BleCommand =
                ReceivePacketManager.parsingReceivePacketType(receiveValue)
            Log.d(TAG, "Receive command : ${receiveType.name}")

            bluetoothGattServer?.sendResponse(
                device,
                requestId,
                BluetoothGatt.GATT_SUCCESS,
                offset,
                null
            )
            when (receiveType) {
                BleCommand.CDR_VENDOR_INFO -> {
                    NotifyManager.sendVendorInfo()
                    Log.d(TAG, "Vendor info")
                }
                BleCommand.TURN_ON_CDR_HOTSPOT -> {
                    val isOpenSuccess = context.let {
                        SystemManager.openWifiHotspot(
                            it,
                            DeviceInfoManager.getSsid(),
                            DeviceInfoManager.getPassword()
                        )
                    }
                    isOpenSuccess.let {
                        SinglePacketManager.sendWifiHotSpotPacket(
                            it
                        )
                    }.let { notifyRegisteredDevices(it) }
                    Log.d(TAG, " Turn on hotspot")
                }
                BleCommand.TURN_OFF_CDR_HOTSPOT -> {
                    val isCloseSuccess = context
                        .let { SystemManager.closeWifiHotspot(it) }
                    isCloseSuccess.let {
                        SinglePacketManager.sendWifiHotSpotPacket(
                            it
                        )
                    }.let { notifyRegisteredDevices(it) }
                    Log.d(TAG, " Turn off hotspot")
                }
                BleCommand.INSTALLATION_COMPLETE -> {
                    notifyRegisteredDevices(SinglePacketManager.sendInstallComplete(true))
                    Log.d(TAG, " Install complete")
                }
                else -> {}
            }

            //onResponseToClient(value, device, requestId, characteristic)
        }

        override fun onDescriptorReadRequest(
            device: BluetoothDevice, requestId: Int, offset: Int,
            descriptor: BluetoothGattDescriptor
        ) {
            if (ServiceProfile.CLIENT_CONFIG == descriptor.uuid) {
                Log.d(TAG, "Config descriptor read")
                val returnValue = if (registeredDevices.contains(device)) {
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                } else {
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                }
                bluetoothGattServer?.sendResponse(
                    device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    0,
                    returnValue
                )
            } else {
                Log.w(TAG, "Unknown descriptor read request")
                bluetoothGattServer?.sendResponse(
                    device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0, null
                )
            }
        }

        override fun onDescriptorWriteRequest(
            device: BluetoothDevice, requestId: Int,
            descriptor: BluetoothGattDescriptor,
            preparedWrite: Boolean, responseNeeded: Boolean,
            offset: Int, value: ByteArray
        ) {
            if (ServiceProfile.CLIENT_CONFIG == descriptor.uuid) {
                if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
                    Log.d(TAG, "Subscribe device to notifications: $device")
                    registeredDevices.add(device)
                } else if (Arrays.equals(
                        BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE,
                        value
                    )
                ) {
                    Log.d(TAG, "Unsubscribe device from notifications: $device")
                    registeredDevices.remove(device)
                }

                if (responseNeeded) {
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0, null
                    )
                }

            } else {
                Log.w(TAG, "Unknown descriptor write request")
                if (responseNeeded) {
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0, null
                    )
                }
            }
        }
    }

    /**
     * Send a time service notification to any devices that are subscribed
     * to the characteristic.
     */
    fun notifyRegisteredDevices(sendByteArray: ByteArray) {
        if (registeredDevices.isEmpty()) {
            Log.i(TAG, "No subscribers registered")
            return
        }

        Log.i(TAG, "Sending update to ${registeredDevices.size} subscribers")
        for (device in registeredDevices) {
            val timeCharacteristic = bluetoothGattServer
                ?.getService(ServiceProfile.USER_DATA_GATT_SERVICE)
                ?.getCharacteristic(ServiceProfile.UUID_CHAR_WRITE)
            timeCharacteristic?.value = sendByteArray
            bluetoothGattServer?.notifyCharacteristicChanged(device, timeCharacteristic, false)
        }
    }
}