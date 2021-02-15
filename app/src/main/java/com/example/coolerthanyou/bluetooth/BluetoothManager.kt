package com.example.coolerthanyou.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.Context
import com.example.coolerthanyou.log.ILogger
import javax.inject.Inject

/**
 * Class to actually control all Bluetooth tasks
 */
class BluetoothManager {

    private val verifier: BluetoothSupportChecker = BluetoothSupportChecker()

    @Inject
    protected lateinit var logger: ILogger
    private val logTag = "BluetoothManager"

    /**
     * Nested class for the express purpose of locking down [BluetoothAdapter].
     * Forces [BluetoothManager] to call [getAdapter] in order to get a valid [BluetoothAdapter]
     * This way, an appropriate error can be thrown before Bluetooth tasks are done on hardware that does not support Bluetooth
     */
    private class BluetoothSupportChecker {
        private lateinit var bluetoothAdapter: BluetoothAdapter
        private var isInitialized: Boolean = false  // becomes true the first time verifyAdapter() is called

        /**
         * Get a valid [BluetoothAdapter].
         * In the process, check whether the hardware supports Bluetooth
         *
         * @throws BluetoothUnsupportedException    if the hardware does not support Bluetooth
         *
         * @return  A valid [BluetoothAdapter] if the hardware supports Bluetooth
         */
        fun getAdapter(): BluetoothAdapter {
            // Only try to get the adapter the first time
            if (!isInitialized) {
                BluetoothAdapter.getDefaultAdapter()?.let {
                    bluetoothAdapter = it
                }
                isInitialized = true
            }

            // throw error if getDefaultAdapter() was null,
            if (!this::bluetoothAdapter.isInitialized) {
                throw BluetoothUnsupportedException("Device does not support Bluetooth")
            }

            // All was well, so return the valid adapter
            return bluetoothAdapter
        }
    }

    /**
     * Return a [BluetoothDevice] with [address]
     *
     * @param address   Address of the Bluetooth device to connect to
     * @return  A valid BluetoothDevice, otherwise null
     */
    internal fun getDevice(address: String): BluetoothDevice? {
        val bluetoothAdapter = verifier.getAdapter()
        return if (BluetoothAdapter.checkBluetoothAddress(address)) {
            logger.d(logTag, "Successfully found device with address: $address")
            bluetoothAdapter.getRemoteDevice(address)
        } else {
            logger.i(logTag, "Could not find device with address: $address")
            null
        }
    }

    /**
     * Try to make a GATT connection to the [device]
     *
     * @param device    Bluetooth device to connect to
     * @param context   The calling context
     * @param callback  The callback interface
     * @return  A Gatt client for further action
     */
    internal fun tryConnect(device: BluetoothDevice, context: Context, callback: BluetoothGattCallback): BluetoothGatt {
        logger.i(logTag, "Trying to connect to device with address: ${device.address}")
        return device.connectGatt(context, true, callback)
    }

    internal fun isBluetoothOn(): Boolean {
        return verifier.getAdapter().isEnabled
    }
}