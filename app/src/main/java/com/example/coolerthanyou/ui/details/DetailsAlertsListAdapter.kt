package com.example.coolerthanyou.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.coolerthanyou.R
import com.example.coolerthanyou.log.ILogger
import com.example.coolerthanyou.model.Alert
import javax.inject.Inject

/**
 * List adapter for [Alert]s in [DetailsFragment]
 */
class DetailsAlertsListAdapter : RecyclerView.Adapter<DetailsAlertsListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.component_simple_list_item_text)

        /**
         * Bind the alert data to this view
         *
         * @param alert The alert to bind data from
         */
        internal fun bindData(alert: Alert) {
            when (alert.type) {
                Alert.TYPE_WARNING -> {
                    textView.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.warning))
                }
                Alert.TYPE_URGENT -> {
                    textView.setBackgroundColor(ContextCompat.getColor(textView.context, R.color.urgent))
                }
            }

            textView.text = alert.message
        }
    }

    @Inject
    protected lateinit var logger: ILogger
    private val logTag: String = "DetailsAlertsListAdapter"

    private var alerts: MutableList<Alert> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(parent.context).inflate(R.layout.component_simple_list_item, parent, false).let { view ->
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            bindData(alerts[position])
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
}