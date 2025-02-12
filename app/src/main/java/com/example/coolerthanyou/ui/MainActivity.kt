package com.example.coolerthanyou.ui

import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coolerthanyou.BaseActivity
import com.example.coolerthanyou.BaseApplication
import com.example.coolerthanyou.R
import com.example.coolerthanyou.bluetooth.BluetoothService
import com.example.coolerthanyou.model.Freezer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

/**
 * Main Activity for all core functions.
 * Each "function" should reside as a fragment within this activity, swapping the Fragment and ViewModel as necessary.
 */
class MainActivity : BaseActivity() {

    private val logTag: String = "MainActivity"
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    private lateinit var fab: FloatingActionButton

    private var isServiceBound: Boolean = false
    private var bluetoothService: BluetoothService? = null
    private val bluetoothServiceConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            logger.i(logTag, "Bluetooth service connected")
            bluetoothService = (binder as BluetoothService.Binder).getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            logger.i(logTag, "Bluetooth service disconnected")
            isServiceBound = false
            bluetoothService = null
        }
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val scanDialog: AlertDialog by lazy {
        AlertDialog.Builder(this)
            .setTitle(R.string.main_scan_title)
            .setView(R.layout.activity_main_scan_recycler)
            .setCancelable(true)
            .setOnDismissListener {
                if (isServiceBound) {
                    bluetoothService!!.stopDiscovery()
                }
            }
            .create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Inject dependencies before init
        (applicationContext as BaseApplication).appComponent.mainComponent().create().inject(this)
        super.onCreate(savedInstanceState)

        logger.d(logTag, "onCreate started")

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.activity_main_toolbar)
        setSupportActionBar(toolbar)

        fab = findViewById(R.id.activity_main_fab)

        val drawerLayout: DrawerLayout = findViewById(R.id.activity_main_drawer_layout)
        val navView: NavigationView = findViewById(R.id.activity_main_nav)
        val navController = findNavController(R.id.activity_main_fragment_host)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        logger.d(logTag, "onCreate completed")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.activity_main_fragment_host)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        logger.d(logTag, "onStart")

        bindService(Intent(this, BluetoothService::class.java), bluetoothServiceConn, Context.BIND_AUTO_CREATE)

        fab.setOnClickListener {
            discoverDevices()
        }
    }

    /**
     * Update the action bar
     *
     * @param title New title of the action bar
     */
    internal fun updateActionBar(title: String) {
        supportActionBar?.title = title
    }

    /**
     * Show the fab
     */
    internal fun showFab() {
        fab.visibility = View.VISIBLE
    }

    /**
     * Hide the fab
     */
    internal fun hideFab() {
        fab.visibility = View.GONE
    }

    /**
     * Check whether bluetooth is on or not
     *
     * @return
     */
    internal fun isBluetoothOn(): Boolean {
        return if (isServiceBound) {
            bluetoothService!!.isBluetoothOn()
        } else {
            false
        }
    }

    /**
     * Check whether the freezer is connected to bluetooth
     *
     * @param freezer   The freezer to check
     * @return  True if connected, false if not connected, bluetooth is off, or service is unbound
     */
    internal fun checkIfConnected(freezer: Freezer): Boolean {
        return if (isServiceBound) {
            bluetoothService!!.checkIfConnected(freezer)
        } else {
            false
        }
    }

    /**
     * Try to connect to the freezer
     *
     * @param freezer   The freezer to connect to
     */
    internal fun tryConnect(freezer: Freezer) {
        if (isServiceBound) {
            bluetoothService!!.connectToDevice(freezer.bluetoothAddress)
        }
    }

    /**
     * Disconnect from the freezer
     *
     * @param freezer   The freezer to disconnect from
     */
    internal fun disconnect(freezer: Freezer) {
        if (isServiceBound) {
            bluetoothService!!.disconnectDevice(freezer.bluetoothAddress)
        }
    }

    /**
     * Update the name of the freezer via Bluetooth
     *
     * @param freezer   The freezer with the new name
     */
    internal fun updateName(freezer: Freezer) {
        if (isServiceBound) {
            bluetoothService!!.updateName(freezer)
        }
    }

    /**
     * Update the refresh rate of the freezer via Bluetooth
     *
     * @param freezer   The freezer with the new refresh rate
     */
    internal fun updateRefreshRate(freezer: Freezer) {
        if (isServiceBound) {
            bluetoothService!!.updateRefreshRate(freezer)
        }
    }

    /**
     * Update the settings of the freezer via Bluetooth
     *
     * @param freezer   The freezer with updated settings
     */
    internal fun updateSettings(freezer: Freezer) {
        if (isServiceBound) {
            bluetoothService!!.updateSettings(freezer)
        }
    }

    /**
     * Manually sample the data for [freezer].
     * Does nothing if the service is unbound
     *
     * @param freezer   The freezer to sample
     */
    internal fun manualSample(freezer: Freezer) {
        if (isServiceBound) {
            bluetoothService!!.pullData(freezer)
        }
    }

    /**
     * Triggers the BLE discovery process
     */
    private fun discoverDevices() {
        if (!isServiceBound) {
            logger.e(logTag, "discoverDevices went wrong, no service bound")
            Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
            return
        }
        if (!bluetoothService!!.isBluetoothOn()) {
            logger.d(logTag, "discoverDevices Bluetooth is off, not discovering")
            Toast.makeText(this, getString(R.string.bluetooth_is_off), Toast.LENGTH_SHORT).show()
            return
        }

        scanDialog.show()

        //Initialize RecyclerView in dialog
        val scanAdapter = MainScanListAdapter(::scanClickCallback)
        scanDialog.findViewById<RecyclerView>(R.id.activity_main_scan_view).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(scanDialog.context)
            adapter = scanAdapter
        }

        val scanCallback: ScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                this@MainActivity.runOnUiThread {
                    if (BluetoothService.scanFilter.matches(result)) {
                        scanAdapter.apply {
                            addDevice(result.device)
                            notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        //Start Bluetooth LE scan
        bluetoothService!!.startDiscovery(scanCallback)
    }

    /**
     * Callback method for when an item is clicked in the scan adapter.
     * Tries to connect to the device
     *
     * @param device    The device to connect to
     */
    private fun scanClickCallback(device: BluetoothDevice) {
        bluetoothService?.connectToDevice(device)
    }
}
