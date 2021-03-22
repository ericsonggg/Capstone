package com.example.coolerthanyou.ui.splash

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.coolerthanyou.BaseActivity
import com.example.coolerthanyou.BaseApplication
import com.example.coolerthanyou.R
import com.example.coolerthanyou.bluetooth.BluetoothManager
import com.example.coolerthanyou.bluetooth.BluetoothService
import com.example.coolerthanyou.bluetooth.BluetoothUnsupportedException
import com.example.coolerthanyou.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * First activity to load when app is started.
 */
class SplashActivity : BaseActivity() {

    companion object {
        private const val PERMISSION_FINE_LOCATION = 1

        private const val TASK_BIND_BLUETOOTH_SERVICE = "TASK_BIND_BLUETOOTH_SERVICE"
        private const val TASK_ENABLE_BLUETOOTH = "TASK_ENABLE_BLUETOOTH"
        private const val TASK_LOCATION_PERMISSION = "TASK_LOCATION_PERMISSION"
    }

    private val logTag: String = "SplashActivity"
    private val viewModel: SplashViewModel by viewModels { viewModelFactory }

    private var isServiceBound: Boolean = false
    private var bluetoothService: BluetoothService? = null
    private val bluetoothServiceConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            logger.i(logTag, "Bluetooth service connected")
            isServiceBound = true
            bluetoothService = (binder as BluetoothService.Binder).getService()

            // try to turn on bluetooth if supported by phone
            try {
                val bluetoothService = (binder).getService()
                if (!bluetoothService.isBluetoothOn()) {
                    // try to turn off bluetooth if off
                    viewModel.addTask(TASK_ENABLE_BLUETOOTH)
                    startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BluetoothManager.REQUEST_ENABLE_BT)
                }
            } catch (e: BluetoothUnsupportedException) {
                // Notify user that they cannot use app without bluetooth
                AlertDialog.Builder(this@SplashActivity)
                    .setMessage(R.string.splash_bluetooth_unsupported_message)
                    .setPositiveButton(R.string.okay) { _, _ ->
                        android.os.Process.killProcess(android.os.Process.myPid()) //kill app
                    }
                    .create().apply {
                        show()
                        setDefaults()
                    }

                logger.e(logTag, "Phone does not support Bluetooth, quitting")
            }

            viewModel.removeTask(TASK_BIND_BLUETOOTH_SERVICE)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            logger.i(logTag, "Bluetooth service disconnected")
            isServiceBound = false
            bluetoothService = null
        }
    }

    private lateinit var textMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inject dependencies before init
        (applicationContext as BaseApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        logger.d(logTag, "onCreate started")

        setContentView(R.layout.activity_splash)
        textMessage = findViewById(R.id.splash_message)

        // Show version number
        val textVersion: TextView = findViewById(R.id.splash_version)
        try {
            packageManager.getPackageInfo(packageName, 0)?.let { pinfo ->
                textVersion.text = getString(R.string.splash_version, pinfo.versionName)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            logger.w(logTag, "Couldn't get version name", e)
        }

        //Create notification channel for BluetoothService
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                BluetoothService.NOTIFICATION_SERVICE_CHANNEL,
                getString(R.string.notif_channel_bluetooth_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.notif_channel_bluetooth_description)
                setSound(null, null) //disable sound

                //register channel
                getSystemService(NotificationManager::class.java)?.also { manager ->
                    manager.createNotificationChannel(this)
                }
            }
        }

        logger.d(logTag, "onCreate finished")
    }

    override fun onStart() {
        super.onStart()
        logger.d(logTag, "onStart")

        // bind to BluetoothService
        bindService(Intent(this, BluetoothService::class.java), bluetoothServiceConn, Context.BIND_AUTO_CREATE)
        viewModel.addTask(TASK_BIND_BLUETOOTH_SERVICE)

        //run tasks
        mockData()
        getPermissions()
        changeActivity()
        runPerformanceTests()
    }

    override fun onStop() {
        super.onStop()
        logger.d(logTag, "onStop")

        if (isServiceBound) {
            logger.d(logTag, "unbinding bt service")
            unbindService(bluetoothServiceConn)
            isServiceBound = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            BluetoothManager.REQUEST_ENABLE_BT -> {
                if (resultCode != RESULT_OK) {
                    // request denied
                    Toast.makeText(this, R.string.splash_bluetooth_disabled_toast, Toast.LENGTH_SHORT).show()
                } else {
                    if (isServiceBound) {
                        bluetoothService!!.updateNotification()
                    }
                }
                viewModel.removeTask(TASK_ENABLE_BLUETOOTH)
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_FINE_LOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    logger.i(logTag, "user granted fine location permissions")
                } else {
                    Toast.makeText(this, R.string.splash_permission_disabled_toast, Toast.LENGTH_SHORT).show()
                }
                viewModel.removeTask(TASK_LOCATION_PERMISSION)
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    /**
     * Run startup data mocks
     */
    private fun mockData() {
        viewModel.runStartupMocks()
    }

    /**
     * Run startup tests
     */
    private fun runPerformanceTests() {
        viewModel.runStartupTests()
    }

    /**
     * Ask user for location permissions if not granted
     */
    private fun getPermissions() {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            logger.i(logTag, "asking user for location services")

            //Info dialog
            AlertDialog.Builder(this)
                .setTitle(R.string.splash_permission_info_title)
                .setMessage(R.string.splash_permission_location_message)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_FINE_LOCATION)
                }
                .create().apply {
                    show()
                    setDefaults()
                }

            viewModel.addTask(TASK_LOCATION_PERMISSION)
        }
    }

    /**
     * Background blocking sequence to check whether we can finish the activity
     */
    private fun changeActivity() {
        lifecycleScope.launch(Dispatchers.Default) {
            while (!viewModel.isFinished()) {
                Thread.sleep(200)
            }
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }
    }
}