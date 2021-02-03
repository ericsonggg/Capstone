package com.example.coolerthanyou.bluetooth

import android.content.BroadcastReceiver
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import androidx.core.app.NotificationCompat
import com.example.coolerthanyou.BaseService
import com.example.coolerthanyou.R
import javax.inject.Inject

class BluetoothService : BaseService {

    private val logTag: String = "BluetoothService"

    @Inject
    private lateinit var bluetoothManager: BluetoothManager

    private lateinit var looper: Looper
    private lateinit var handler: Handler
    private lateinit var serviceNotification: NotificationCompat.Builder

    companion object {
        const val THREAD_NAME = "BluetoothService"
        const val NOTIFICATION_SERVICE_CHANNEL = "BluetoothService"
        const val NOTIFICATION_SERVICE_ID = 69274

        val broadcastReceiver = object : BroadcastReceiver() {
            //TODO
        }
    }

    override fun onCreate() {
        super.onCreate()
        logger.d(logTag, "onCreate started")

        // Start thread
        HandlerThread(THREAD_NAME, Process.THREAD_PRIORITY_FOREGROUND).also {
            it.start()
            looper = it.looper

        }
        handler = Handler(looper)

        // Create notification and start service as foreground
        serviceNotification = NotificationCompat.Builder(this, NOTIFICATION_SERVICE_CHANNEL).apply {
            setSmallIcon(R.mipmap.ic_launcher) //TODO: update with app icon
            priority = NotificationCompat.PRIORITY_LOW
            setContentTitle(getString(if (bluetoothManager.isBluetoothOn()) R.string.bluetooth_disconnected else R.string.bluetooth_disconnected))
        }
        startForeground(NOTIFICATION_SERVICE_ID, serviceNotification.build())

        // register broadcast receiver
        registerReceiver(broadcastReceiver, )

        logger.d(logTag, "onCreate finished")
    }
}