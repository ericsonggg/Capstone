package com.example.coolerthanyou.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.coolerthanyou.R
import com.example.coolerthanyou.model.Alert
import com.example.coolerthanyou.model.FreezerRecord
import java.util.*

/**
 * List adapter for [Record]s and [Alert]s in [HistoryFragment]
 */
class HistoryListAdapter : RecyclerView.Adapter<HistoryListAdapter.ViewHolder>() {

    /**
     * [ViewHolder] for [HistoryListAdapter]
     *
     * @param itemView  The view of the item layout
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.component_simple_list_item_text)

        /**
         * Bind the record or alert data to this view, depending on [isRecord]
         *
         * @param isRecord  True if this view holder binds from a record. False if from an alert
         * @param record    The record to bind data from
         * @param alert The alert to bind data from
         */
        internal fun bindData(isRecord: Boolean, record: FreezerRecord? = null, alert: Alert? = null) {
            val context = textView.context

            if (isRecord) {
                record!!
                textView.setBackgroundColor(ContextCompat.getColor(context, R.color.accent))
                textView.text = context.getString(R.string.fragment_history_list_record, record.time, record.temperature, record.humidity)
            } else {
                when (alert!!.type) {
                    Alert.TYPE_WARNING -> {
                        textView.setBackgroundColor(ContextCompat.getColor(context, R.color.warning))
                    }
                    Alert.TYPE_URGENT -> {
                        textView.setBackgroundColor(ContextCompat.getColor(context, R.color.urgent))
                    }
                }
                textView.text = context.getString(R.string.fragment_history_list_alert, alert.time, alert.message)
            }
        }
    }

    private var records: MutableList<FreezerRecord> = mutableListOf()
    private var alerts: MutableList<Alert> = mutableListOf()
    private var data: MutableList<Pair<FreezerRecord?, Alert?>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context).inflate(R.layout.component_simple_list_item, parent, false).let { view ->
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = data[position]

        holder.bindData(data.second == null, data.first, data.second)
    }

    override fun getItemCount(): Int = records.size + alerts.size

    /**
     * Update all the records
     *
     * @param newRecords    The list of new records
     */
    internal fun updateRecords(newRecords: List<FreezerRecord>) {
        records = newRecords.sortedByDescending { it.time }.toMutableList()
        sortData()
    }

    /**
     * Update all the alerts
     *
     * @param newAlerts The list of new alerts
     */
    internal fun updateAlerts(newAlerts: List<Alert>) {
        alerts = newAlerts.sortedByDescending { it.time }.toMutableList()
        sortData()
    }

    /**
     * Organizes records and alerts into a single time sorted array, in descending order.
     */
    private fun sortData() {
        data.clear()    //wipe all data before we start
        if (itemCount == 0) return

        // check for empty lists
        if (alerts.isEmpty()) {
            records.map { Pair(it, null) }.forEach { data.add(it) }
            return
        }
        if (records.isEmpty()) {
            alerts.map { Pair(null, it) }.forEach { data.add(it) }
            return
        }

        // both lists are non-empty
        var recordIndex = 0
        var alertsIndex = 0

        for (i in 0 until itemCount) {
            if (records[recordIndex].time.before(alerts[alertsIndex].time)) {
                // record is before alert, so insert the alert first
                data.add(Pair(null, alerts[alertsIndex]))
                alertsIndex++

                if (alertsIndex == alerts.size) {
                    //exhausted all alerts, put all records in
                    records.slice(recordIndex until records.size).forEach {
                        data.add(Pair(it, null))
                    }
                    return
                }
            } else {
                // record is not before alert, so insert the record
                data.add(Pair(records[recordIndex], null))
                recordIndex++

                if (recordIndex == records.size) {
                    //exhausted all reports, put all alerts in
                    alerts.slice(alertsIndex until alerts.size).forEach {
                        data.add(Pair(null, it))
                    }
                    return
                }
            }
        }
    }
}