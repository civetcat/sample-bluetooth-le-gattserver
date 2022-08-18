package com.example.androidthings.gattserver

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import java.util.*

object ServiceProfile {
    /* User define GATT Service UUID */
    val USER_DATA_GATT_SERVICE: UUID = UUID.fromString("0000181c-0000-1000-8000-00805f9b34fb")

    /* User define Characteristic - Read and Notify */
    val USER_INDEX: UUID = UUID.fromString("00002a9a-0000-1000-8000-00805f9b34fb")

    /* Client Characteristic Config Descriptor */
    val CLIENT_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    /* User define Characteristic - write */
    private val UUID_CHAR_WRITE: UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb")

    fun createService(): BluetoothGattService {
        val service = BluetoothGattService(
            USER_DATA_GATT_SERVICE,
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        // Create Read Characteristic
        val readCharacter = BluetoothGattCharacteristic(
            USER_INDEX,
            BluetoothGattCharacteristic.PROPERTY_READ or
                    BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        val configDescriptor = BluetoothGattDescriptor(
            CLIENT_CONFIG,
            //Read/write descriptor
            BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE
        )
        readCharacter.addDescriptor(configDescriptor)

        // Create Write Characteristic
        val writeCharacter = BluetoothGattCharacteristic(
            UUID_CHAR_WRITE,
            BluetoothGattCharacteristic.PROPERTY_WRITE or
                    BluetoothGattCharacteristic.PROPERTY_READ or
                    BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )

        service.addCharacteristic(readCharacter)
        service.addCharacteristic(writeCharacter)

        return service
    }
}