package com.example.coolerthanyou.ui

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coolerthanyou.R

/**
 * List adapter for [MainActivity]'s bluetooth discovery
 *
 * @property callback   Callback method to call when a device is clicked
 */
class MainScanListAdapter(private val callback: (device: BluetoothDevice) -> Unit) :
    RecyclerView.Adapter<MainScanListAdapter.ViewHolder>() {

    /**
     * Custom view holder class for [MainScanListAdapter]
     *
     * @param itemView  The[View] that this view holder represents
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.activity_main_scan_list_name)
        private val address: TextView = itemView.findViewById(R.id.activity_main_scan_list_address)

        /**
         * Bind the device's data to this view holder and display it
         *
         * @param device    The device to bind
         */
        internal fun bindData(device: BluetoothDevice) {
            if (device.name.isBlank()) {
                name.setText(R.string.main_scan_no_name)
            } else {
                name.text = device.name
            }
            address.text = device.address
        }
    }

    private val deviceList: MutableList<BluetoothDevice> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        LayoutInflater.from(parent.context).inflate(R.layout.activity_main_scan_item, parent, false).let { view ->
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            bindData(deviceList[position])
            val device = deviceList[position]
            itemView.setOnClickListener { callback(device) }
        }
    }

    override fun getItemCount(): Int = deviceList.size

    /**
     * Add new bluetooth device to list
     *
     * @param device    Device to add
     */
    fun addDevice(device: BluetoothDevice) {
        if (!deviceList.contains(device)) {
            deviceList.add(device)
        }
    }
}