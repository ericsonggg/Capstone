package com.example.coolerthanyou.bluetooth

/**
 * Singleton object to hold broadcast constants for [BluetoothService]
 * Also generates an intent filter for registering broadcast receivers
 */
object BluetoothBroadcast {
    const val ACTION_SERVICE_BOUND = "com.bme.solon.ACTION_SERVICE_BOUND"
    const val ACTION_SERVICE_DISCONNECTED = "com.bme.solon.ACTION_SERVICE_DISCONNECTED"
    const val ACTION_CONNECTING = "com.bme.solon.ACTION_CONNECTING"
    const val ACTION_CONNECTED = "com.bme.solon.ACTION_CONNECTED"
    const val ACTION_CONNECTED_UPDATE = "com.bme.solon.ACTION_CONNECTED_UPDATE"
    const val ACTION_DISCONNECTED = "com.bme.solon.ACTION_DISCONNECTED"
    const val ACTION_DEVICES_CHANGED = "com.bme.solon.ACTION_DEVICES_CHANGED"
    const val ACTION_DEVICE_UPDATED = "com.bme.solon.ACTION_DEVICE_UPDATED"
    const val ACTION_NEW_INSTANCE = "com.bme.solon.ACTION_NEW_INSTANCE"
    const val ACTION_INSTANCE_UPDATE = "com.bme.solon.ACTION_INSTANCE_UPDATE"
    const val ACTION_ADDRESS = "com.bme.solon.ADDRESS"
    const val ACTION_SNOOZE = "com.bme.solon.SNOOZE"

    const val KEY_DEVICE_NAME = "KEY_DEVICE_NAME"
    const val KEY_DEVICE_ADDRESS = "KEY_DEVICE_ADDRESS"
    const val KEY_DEVICE_ID = "KEY_DEVICE_ID"
    const val KEY_INSTANCE_ID = "KEY_INSTANCE_ID"

}