package com.example.coolerthanyou.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter

/**
 * Singleton object to hold broadcast constants for [BluetoothService]
 * Also generates an intent filter for registering broadcast receivers
 */
object BluetoothBroadcast {
    const val ACTION_SERVICE_BOUND = "com.bme.pyrexia.ACTION_SERVICE_BOUND"
    const val ACTION_SERVICE_UNBOUND = "com.bme.pyrexia.ACTION_SERVICE_UNBOUND"
    const val ACTION_CONNECTED = "com.bme.pyrexia.ACTION_CONNECTED"
    const val ACTION_DISCONNECTED = "com.bme.pyrexia.ACTION_DISCONNECTED"
    const val ACTION_WARNING = "com.bme.pyrexia.ADDRESS"
    const val ACTION_ALERT = "com.bme.pyrexia.SNOOZE"
    const val ACTION_UPDATE = "com.bme.pyrexia.UPDATE"

    const val KEY_ADDRESS = "KEY_ADDRESS"

    /**
     * Get an intent filter with all applicable actions for [BluetoothService]
     *
     * @return  A full intent filter
     */
    fun getIntentFilter(): IntentFilter = IntentFilter().apply {
        addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        addAction(ACTION_SERVICE_BOUND)
        addAction(ACTION_SERVICE_UNBOUND)
        addAction(ACTION_CONNECTED)
        addAction(ACTION_DISCONNECTED)
        addAction(ACTION_WARNING)
        addAction(ACTION_ALERT)
        addAction(ACTION_UPDATE)
    }
}