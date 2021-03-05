package com.example.coolerthanyou.bluetooth

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.coolerthanyou.BaseApplication
import com.example.coolerthanyou.BaseService
import com.example.coolerthanyou.R
import com.example.coolerthanyou.datasource.IFreezerDao
import com.example.coolerthanyou.model.Freezer
import java.nio.ByteBuffer
import java.util.*
import javax.inject.Inject

/**
 * Service for long-running Bluetooth operations.
 * Uses [BluetoothManager]
 */
class BluetoothService : BaseService() {

    companion object {
        const val THREAD_NAME = "BluetoothService"
        const val NOTIFICATION_SERVICE_CHANNEL = "BluetoothService"
        const val NOTIFICATION_SERVICE_ID = 69274

        const val HANDLER_MSG_CONNECT = 1
        const val HANDLER_MSG_WRITE = 2
        const val HANDLER_DELAY_WRITE = 1000L

        private const val HARDWARE_SCAN_UUID = "0000dfb0-0000-1000-8000-00805f9b34fb"
        const val HARDWARE_SERIAL_UUID = "0000dfb1-0000-1000-8000-00805f9b34fb"
        const val HARDWARE_COMMAND_UUID = "0000dfb2-0000-1000-8000-00805f9b34fb"
        const val HARDWARE_MODEL_NUMBER_UUID = "00002a24-0000-1000-8000-00805f9b34fb"

        private const val KEY_STANDARD: Byte = 0x00.toByte()
        private const val KEY_SETTINGS: Byte = 0x01.toByte()
        private const val KEY_ERROR: Byte = 0x10.toByte()
        private const val KEY_UPDATE_NAME: Byte = 0x50.toByte()
        private const val KEY_UPDATE_REFRESH: Byte = 0x51.toByte()
        private const val KEY_UPDATE_SETTING: Byte = 0x52.toByte()
        private const val KEY_MANUAL_PULL: Byte = 0x60.toByte()
        private const val KEY_MANUAL_PULL_SETTINGS: Byte = 0x61.toByte()

        private const val MESSAGE_SIZE: Int = 26

        val scanFilter: ScanFilter = ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(HARDWARE_SCAN_UUID)).build()
    }

    /**
     * Private [Handler] just for [BluetoothService]
     * Runs [Message] and [Runnable] on separate thread
     *
     * @constructor create a Handler with the specific thread [Looper]
     *
     * @param looper    The thread looper
     */
    private inner class Handler(looper: Looper) : android.os.Handler(looper) {

        /**
         * [HANDLER_MSG_CONNECT] -> Try to start a connection with the freezer id from the message
         */
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                HANDLER_MSG_CONNECT -> {
                    val address = msg.obj as String
                    logger.i(logTag, "Handler@handleMessage connect to $address")

                    bluetoothManager.getDevice(address)?.let { device ->
                        bluetoothManager.tryConnect(device, this@BluetoothService, gattCallback)
                    }
                }
                HANDLER_MSG_WRITE -> {
                    if (writeQueue.isEmpty()) {
                        // no charac to send, we delay
                        Message.obtain(handler, HANDLER_MSG_WRITE).also { newMsg ->
                            sendMessageDelayed(newMsg, HANDLER_DELAY_WRITE)
                        }
                        return
                    }

                    writeQueue.peek()?.let { pair ->
                        val gatt = pair.first
                        val charac = pair.second
                        logger.d(logTag, "writing characteristic $charac to remote device ${gatt.device.address}")

                        gatt.writeCharacteristic(charac)
                    }
                }
            }
        }
    }

    /**
     * Custom Binder class for [BluetoothService]
     */
    internal inner class Binder : android.os.Binder() {

        /**
         * Get the BluetoothService for this [Binder]
         *
         * @return  The bound BluetoothService
         */
        fun getService(): BluetoothService = this@BluetoothService
    }

    private val logTag: String = "BluetoothService"

    @Inject
    protected lateinit var bluetoothManager: BluetoothManager
    private val connectedDevices: MutableMap<String, BluetoothGatt> = mutableMapOf()
    private var discoveryCallback: ScanCallback? = null
    private val writeQueue: Queue<Pair<BluetoothGatt, BluetoothGattCharacteristic>> = LinkedList()

    @Inject
    protected lateinit var freezerDao: IFreezerDao

    private lateinit var looper: Looper
    private lateinit var handler: Handler
    private lateinit var serviceNotification: NotificationCompat.Builder
    private val binder: IBinder = Binder()

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        private var characs: ByteBuffer = ByteBuffer.allocate(MESSAGE_SIZE)

        /**
         * Start discovering services for all newly connected devices.
         */
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (status) {
                BluetoothGatt.GATT_FAILURE -> {
                    logger.i(logTag, "Failed to connect to device with address: ${gatt.device.address}")
                    gatt.disconnect()
                }
                else -> {
                    logger.d(logTag, "onConnectionStateChange new state: $newState")
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            logger.i(logTag, "Connected to device with address: ${gatt.device.address}")
                            connectedDevices[gatt.device.address] = gatt
                            updateNotification()
                            gatt.discoverServices()
                        }
                        else -> {
                            logger.i(logTag, "Disconnected from device with address: ${gatt.device.address}")
                            connectedDevices.remove(gatt.device.address)
                            updateNotification()
                        }
                    }
                }
            }
        }

        /**
         * Store all appropriate characteristics on success.
         * If failed, retry service discovery.
         */
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            when (status) {
                BluetoothGatt.GATT_FAILURE -> {
                    logger.w(logTag, "Failed to discover services for ${gatt.device.address}, retrying")
                    gatt.discoverServices()
                }
                else -> {
                    findSerialCharacteristic(gatt)?.let { charac ->
                        with(gatt) {
                            setCharacteristicNotification(charac, true)
                            readCharacteristic(charac)
                        }
                        return
                    }

                    // no serial charac, so disconnect
                    logger.w(
                        logTag,
                        "no service had a Serial Port characteristic for ${gatt.device.address}, disconnecting"
                    )
                    gatt.disconnect()
                }
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            // ignore first characteristic read
        }

        /**
         * Read changed data from the device and apply it to buffer.
         */
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val data = characteristic.value
            var dataIndex = 0
            var availableSpace: Int = MESSAGE_SIZE - characs.position()
            var remainingData: Int = data.size - dataIndex

            // loop until data depleted
            while (remainingData > 0) {
                // fill as much of buffer as we can without overflow
                dataIndex = if ((availableSpace - remainingData) <= 0) {
                    characs.put(data, dataIndex, availableSpace)
                    dataIndex + availableSpace
                } else {
                    characs.put(data, dataIndex, remainingData)
                    dataIndex + remainingData
                }

                // only process if we've got MESSAGE_SIZE bytes
                if (characs.position() == MESSAGE_SIZE) {
                    processCharacteristic(gatt.device.address, characs.array())
                    characs.clear()
                }

                // recalculate
                availableSpace = MESSAGE_SIZE - characs.position()
                remainingData = data.size - dataIndex
            }
        }

        /**
         * Write data to a device, with retries
         */
        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            when (status) {
                BluetoothGatt.GATT_FAILURE -> {
                    logger.w(logTag, "Failed to write characteristic to ${gatt.device.address}")
                }
                else -> {
                    writeQueue.remove() //remove element that was just processed
                }
            }

            Message.obtain(handler, HANDLER_MSG_WRITE).apply {
                sendToTarget()
            }
        }
    }

    /**
     * Start thread and service as a foreground service
     */
    override fun onCreate() {
        (applicationContext as BaseApplication).appComponent.inject(this)
        super.onCreate()
        logger.d(logTag, "onCreate started")

        // Start thread
        HandlerThread(THREAD_NAME, Process.THREAD_PRIORITY_FOREGROUND).also { thread ->
            thread.start()
            looper = thread.looper
        }
        handler = Handler(looper)

        // Create notification and start service as foreground
        serviceNotification = NotificationCompat.Builder(this, NOTIFICATION_SERVICE_CHANNEL).apply {
            setSmallIcon(R.mipmap.ic_launcher) //TODO: update with app icon
            priority = NotificationCompat.PRIORITY_LOW
            setContentTitle(getString(if (bluetoothManager.isBluetoothOn()) R.string.notif_bluetooth_no_devices else R.string.notif_bluetooth_off))
        }
        startForeground(NOTIFICATION_SERVICE_ID, serviceNotification.build())

        // Start write queue poll
        Message.obtain(handler, HANDLER_MSG_WRITE).apply {
            sendToTarget()
        }

        logger.d(logTag, "onCreate finished")
    }

    /**
     * Try to connect to all registered devices on start
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logger.d(logTag, "onStartCommand started")

        // Send message to connect with all Freezers
        freezerDao.getAllBluetooth().forEach { address ->
            with(Message.obtain(handler, HANDLER_MSG_CONNECT)) {
                obj = address
                sendToTarget()
            }
        }

        logger.d(logTag, "onStartCommand finished")
        return START_STICKY
    }

    /**
     * Allow UI to bind to service via {@link BluetoothService.Binder}
     * @return      A valid binder for this service
     */
    override fun onBind(intent: Intent): IBinder {
        logger.d(logTag, "onBind")
        return binder
    }

    override fun onDestroy() {
        logger.d(logTag, "onDestroy")
        stopForeground(true)
        for ((_, gatt) in connectedDevices) {
            gatt.disconnect()
            gatt.close()
        }
        connectedDevices.clear()
    }

    /**
     * Returns whether the Bluetooth is on or off
     *
     * @return  True if on, false if not
     */
    internal fun isBluetoothOn(): Boolean = bluetoothManager.isBluetoothOn()

    /**
     * Connect to a Bluetooth device
     *
     * @param device    The device to connect to
     */
    internal fun connectToDevice(device: BluetoothDevice) {
        logger.d(logTag, "connectToDevice: posting task with device: $device")
        handler.post {
            bluetoothManager.tryConnect(device, this, gattCallback)
        }
    }

    /**
     * Start discovering Bluetooth LE devices, if a current scan is not underway
     *
     * @param callback  The callback for the scan results
     */
    internal fun startDiscovery(callback: ScanCallback) {
        logger.d(logTag, "startDiscovery: posting task")
        handler.post {
            if (discoveryCallback == null) {
                discoveryCallback = callback
                bluetoothManager.startDiscovery(callback)
            }
        }
    }

    /**
     * Stop discovering Bluetooth LE devices if a current scan is underway
     */
    internal fun stopDiscovery() {
        logger.d(logTag, "stopDiscovery: posting task")
        handler.post {
            discoveryCallback?.let {
                bluetoothManager.stopDiscovery(discoveryCallback!!)
                discoveryCallback = null
            }
        }
    }

    /**
     * Update the name of a freezer via BLE
     *
     * @param freezer   The freezer with the updated name
     */
    internal fun updateName(freezer: Freezer) {
        byteArrayOf().apply {
            this + KEY_UPDATE_NAME
            this + freezer.name.toByteArray()
            writeCharacteristic(this, freezer.bluetoothAddress)
        }
    }

    /**
     * Update refresh rate of a freezer via BLE
     *
     * @param freezer   Freezer to update
     * @param rate  The new rate in seconds
     */
    internal fun updateRefreshRate(freezer: Freezer, rate: Int) {
        byteArrayOf().apply {
            this + KEY_UPDATE_REFRESH
            this + rate.toByte()
            writeCharacteristic(this, freezer.bluetoothAddress)
        }
    }

    /**
     * Update settings of a freezer via BLE
     *
     * @param freezer   Freezer with updated settings
     */
    internal fun updateSettings(freezer: Freezer) {
        byteArrayOf().apply {
            this + KEY_UPDATE_SETTING
            this + if (freezer.set_power_on) 0.toByte() else 1.toByte()
            this + ByteBuffer.allocate(4).putFloat(freezer.set_temperature).array()
            this + ByteBuffer.allocate(4).putFloat(freezer.set_humidity).array()
            writeCharacteristic(this, freezer.bluetoothAddress)
        }
    }

    /**
     * Request a data pull immediately
     *
     * @param freezer   The freezer to pull data from
     */
    internal fun pullData(freezer: Freezer) {
        byteArrayOf().apply {
            this + KEY_MANUAL_PULL
            writeCharacteristic(this, freezer.bluetoothAddress)
        }
    }

    /**
     * Request a pull for settings immediately
     *
     * @param address   The address of the device to pull from
     */
    internal fun pullSettings(address: String) {
        byteArrayOf().apply {
            this + KEY_MANUAL_PULL_SETTINGS
            writeCharacteristic(this, address)
        }
    }

    /**
     * Update the notification
     */
    internal fun updateNotification() {
        val message = if (connectedDevices.isEmpty()) {
            if (bluetoothManager.isBluetoothOn()) {
                getString(R.string.notif_bluetooth_no_devices)
            } else {
                getString(R.string.notif_bluetooth_off)
            }
        } else {
            getString(R.string.notif_bluetooth_x_devices, connectedDevices.size)
        }
        serviceNotification.setContentTitle(message)
        NotificationManagerCompat.from(this@BluetoothService)
            .notify(NOTIFICATION_SERVICE_ID, serviceNotification.build())
    }

    /**
     * Helper method to get the serial characteristic from a GATT
     *
     * @param gatt  The GATT to search
     * @return  The serial characteristic if found. Otherwise null
     */
    private fun findSerialCharacteristic(gatt: BluetoothGatt): BluetoothGattCharacteristic? {
        for (service in gatt.services) {
            for (charac in service.characteristics) {
                when (charac.uuid.toString()) {
                    HARDWARE_SERIAL_UUID -> {
                        return charac
                    }
                }
            }
        }
        return null
    }

    /**
     * Converts the read characteristic into data
     *
     * @param address   The address of the GATT device
     * @param charac    The characteristic that should be processed
     */
    private fun processCharacteristic(address: String, charac: ByteArray) {
        if (charac.size < 2) {
            logger.w(logTag, "invalid characteristic with $charac")
        }

        when (val key = charac[0]) {
            KEY_STANDARD -> {
                if (charac.size < 10) {
                    logger.w(logTag, "invalid standard message, with charac $charac")
                    return
                }

                // Insert new record
                handler.post {
                    val temperature = charac.sliceArray(1..4).apply {
                        reverse()
                    }.let {
                        ByteBuffer.wrap(it).float
                    }
                    val humidity = charac.sliceArray(5..8).apply {
                        reverse()
                    }.let {
                        ByteBuffer.wrap(it).float
                    }
                    val battery: Int = charac[9].toInt()
                    freezerDao.insertFreezerRecord(address, temperature, humidity, battery)
                }
            }
            KEY_SETTINGS -> {
                if (charac.size < 26) {
                    logger.w(logTag, "invalid settings message, with charac $charac")
                    return
                }

                // Insert new freezer
                handler.post {
                    val isPowerOn = charac[1] == 0.toByte()
                    val temperature = ByteBuffer.wrap(charac.sliceArray(2..5)).float
                    val humidity = ByteBuffer.wrap(charac.sliceArray(6..9)).float
                    val samplingRate = charac[10].toInt()
                    val name = String(charac.sliceArray(10..25))
                    try {
                        freezerDao.insertAllFreezers(Freezer(name, address, temperature, humidity, samplingRate, isPowerOn, false))
                    } catch (e: IllegalArgumentException) {
                        logger.e(logTag, "Failed to create a freezer", e)
                    }
                }
            }
            KEY_ERROR -> {
                if (charac.size < 9) {
                    logger.w(logTag, "invalid error message, with charac $charac")
                    return
                }

                handler.post {
                    val error = String(charac.sliceArray(1..8))
                    freezerDao.getFreezerByAddress(address).also { freezer ->
                        logger.e(logTag, "received error from Freezer ${freezer.name} with error: $error")
                    }
                }
            }
            else -> {
                logger.w(logTag, "got an invalid characteristic key: $key, for charac: $charac")
            }
        }
    }

    /**
     * Helper method to write a characteristic to a specific device
     *
     * @param data  The data to write
     * @param address   The address of the device to write to
     */
    private fun writeCharacteristic(data: ByteArray, address: String) {
        connectedDevices[address]?.let { gatt ->
            findSerialCharacteristic(gatt)?.let { charac ->
                charac.value = data
                writeQueue.add(Pair(gatt, charac))
            }
        }
    }
}