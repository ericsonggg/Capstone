package com.example.coolerthanyou.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.coolerthanyou.R
import com.example.coolerthanyou.model.Alert

/**
 * List adapter for [Alert]s in [DetailsFragment]
 *
 * @property callback  The callback method for when an alert is tapped
 */
class DetailsAlertsListAdapter(private val callback: (alert: Alert) -> Unit) : RecyclerView.Adapter<DetailsAlertsListAdapter.ViewHolder>() {

    /**
     * [ViewHolder] for [DetailsAlertsListAdapter]
     *
     * @param itemView  The view of the item layout
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.component_simple_list_item_text)

        /**
         * Bind the alert data to this view
         *
         * @param alert The alert to bind data from
         */
        internal fun bindData(alert: Alert) {
            val context = textView.context

            when (alert.type) {
                Alert.TYPE_WARNING -> {
                    textView.setBackgroundColor(ContextCompat.getColor(context, R.color.warning))
                }
                Alert.TYPE_URGENT -> {
                    textView.setBackgroundColor(ContextCompat.getColor(context, R.color.urgent))
                }
            }
            textView.text = alert.message
        }
    }

    private var alerts: MutableList<Alert> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context).inflate(R.layout.component_simple_list_item, parent, false).let { view ->
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            bindData(alerts[position])
            val alert = alerts[position]
            itemView.setOnClickListener {
                callback(alert)
            }
        }
    }

    override fun getItemCount(): Int = alerts.size

    /**
     * Update all the alerts
     *
     * @param newAlerts The list of new alerts
     */
    internal fun updateAlerts(newAlerts: List<Alert>) {
        alerts = newAlerts.toMutableList()
    }

    /**
     * Remove this alert if exists
     *
     * @param alert The alert to remove
     * @return  The index of the removed item. -1 if not found.
     */
    internal fun removeAlert(alert: Alert): Int {
        var index = -1
        for (i in 0 until alerts.size) {
            if (alerts[i].boxId == alert.boxId && alerts[i].time == alert.time) {
                index = i
                break
            }
        }

        if (index != -1) {
            alerts.removeAt(index)
        }
        return index
    }
}