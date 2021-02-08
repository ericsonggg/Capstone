package com.example.coolerthanyou.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.coolerthanyou.BaseService
import com.example.coolerthanyou.R
import com.example.coolerthanyou.datasource.IFreezerDao
import javax.inject.Inject

class BluetoothService : BaseService() {

    companion object {
        const val THREAD_NAME = "BluetoothService"
        const val NOTIFICATION_SERVICE_CHANNEL = "BluetoothService"
        const val NOTIFICATION_SERVICE_ID = 69274

        const val HANDLER_MSG_CONNECT = 1

        const val HARDWARE_SERIAL_UUID = "0000dfb1-0000-1000-8000-00805f9b34fb"
        const val HARDWARE_COMMAND_UUID = "0000dfb2-0000-1000-8000-00805f9b34fb"
        const val HARDWARE_MODEL_NUMBER_UUID = "00002a24-0000-1000-8000-00805f9b34fb"
    }

    private val logTag: String = "BluetoothService"

    @Inject
    private lateinit var bluetoothManager: BluetoothManager
    private val connectedDevices: MutableSet<BluetoothGatt> = mutableSetOf()

    @Inject
    private lateinit var freezerDao: IFreezerDao

    private lateinit var looper: Looper
    private lateinit var handler: Handler
    private lateinit var serviceNotification: NotificationCompat.Builder
    private val binder: IBinder = Binder()

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {

        /**
         * Start discovering services for all newly connected devices.
         * Also broadcast relevant status changes
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
                            connectedDevices.add(gatt)
                            broadcastConnected(gatt)
                            gatt.discoverServices()
                        }
                        else -> {
                            logger.i(logTag, "Disconnected from device with address: ${gatt.device.address}")
                            connectedDevices.remove(gatt)
                            broadcastDisconnected(gatt)
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
                    var foundSerial = false
                    for (service in gatt.services) {
                        logger.v(logTag, "onServicesDiscovered service uuid: ${service.uuid}")
                        for (charac in service.characteristics) {
                            logger.v(logTag, "onServicesDiscovered characteristic uuid: ${charac.uuid}")
                            when (charac.uuid.toString()) {
                                HARDWARE_SERIAL_UUID -> {
                                    logger.d(logTag, "onServicesDiscovered enabling Serial Port notification channel")
                                    with(gatt) {
                                        setCharacteristicNotification(charac, true)
                                        readCharacteristic(charac)
                                    }
                                    foundSerial = true
                                }
                                HARDWARE_COMMAND_UUID, HARDWARE_MODEL_NUMBER_UUID -> {
                                    // ignore
                                }
                                else -> {
                                    // ignore
                                }
                            }
                        }
                    }

                    if (!foundSerial) {
                        logger.w(
                            logTag,
                            "no service had a Serial Port characteristic for ${gatt.device.address}, disconnecting"
                        )
                        gatt.disconnect()
                    }
                }
            }
        }

        /**
         * Read data from the serial characteristic
         */
        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            when (status) {
                BluetoothGatt.GATT_FAILURE -> {
                    logger.w(logTag, "Failed to read characteristic from ${gatt.device.address}")
                }
                else -> {
                    processCharacteristic(characteristic.value)
                }
            }
        }

        /**
         * Read changed data from the device, via [onCharacteristicRead]
         */
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            onCharacteristicRead(gatt, characteristic, BluetoothGatt.GATT_SUCCESS)
        }
    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            logger.d(logTag, "onReceive action ${intent.action}")
            val notif: NotificationManagerCompat = NotificationManagerCompat.from(this@BluetoothService)

            when (intent.action) {
                BluetoothBroadcast.ACTION_CONNECTED -> {
                    // Update message with at least 1 connected device
                    val message = getString(R.string.notif_bluetooth_x_devices, connectedDevices.size)
                    serviceNotification.setContentTitle(message)
                    notif.notify(NOTIFICATION_SERVICE_ID, serviceNotification.build())
                }
                BluetoothBroadcast.ACTION_WARNING -> {
                    //TODO
                }
                BluetoothBroadcast.ACTION_ALERT -> {
                    //TODO
                }
                BluetoothBroadcast.ACTION_DISCONNECTED, BluetoothBroadcast.ACTION_UPDATE -> {
                    // Update message
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
                    notif.notify(NOTIFICATION_SERVICE_ID, serviceNotification.build())
                }
            }
        }
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
                    logger.i(logTag, "Handler@handleMessage connect to ${address}")

                    bluetoothManager.getDevice(address)?.let { device ->
                        bluetoothManager.tryConnect(device, this@BluetoothService, gattCallback)
                    }
                }
            }
        }
    }

    /**
     * Start thread and service as a foreground service
     */
    override fun onCreate() {
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

        // register broadcast receiver
        registerReceiver(broadcastReceiver, BluetoothBroadcast.getIntentFilter())

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
        for (gatt in connectedDevices) {
            gatt.disconnect()
            gatt.close()
        }
        connectedDevices.clear()
    }

    private fun processCharacteristic(charac: ByteArray) {
        //TODO:
    }

    /**
     * Broadcast an [Intent] with the specified [action]
     *
     * @param action    The [Intent]'s action
     */
    private fun broadcast(action: String) {
        sendBroadcast(Intent(action))
    }

    /**
     * Broadcast an [Intent] for connecting with the device address that connected
     *
     * @param gatt  The device that connected
     */
    private fun broadcastConnected(gatt: BluetoothGatt) {
        Intent(BluetoothBroadcast.ACTION_CONNECTED).apply {
            putExtra(BluetoothBroadcast.KEY_ADDRESS, gatt.device.address)
            sendBroadcast(this)
        }
    }

    /**
     * Broadcast an [Intent] for disconnecting with the device address that disconnected
     *
     * @param gatt  The device that disconnected
     */
    private fun broadcastDisconnected(gatt: BluetoothGatt) {
        Intent(BluetoothBroadcast.ACTION_DISCONNECTED).apply {
            putExtra(BluetoothBroadcast.KEY_ADDRESS, gatt.device.address)
            sendBroadcast(this)
        }
    }
}