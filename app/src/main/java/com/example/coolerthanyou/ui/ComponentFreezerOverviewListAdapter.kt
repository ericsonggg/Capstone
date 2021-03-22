package com.example.coolerthanyou.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.coolerthanyou.R
import com.example.coolerthanyou.model.Alert
import com.example.coolerthanyou.model.Freezer
import com.example.coolerthanyou.model.FreezerRecord
import com.example.coolerthanyou.ui.home.HomeFreezerListAdapter

/**
 * List adapter for the generic component freezer overview list
 *
 * @property alertCallback   Callback method to call when the alert icon is clicked. Null if no callback needed.
 */
class ComponentFreezerOverviewListAdapter(private val alertCallback: ((boxId: Long) -> Unit)) :
    RecyclerView.Adapter<ComponentFreezerOverviewListAdapter.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_NONE = 0
        const val VIEW_TYPE_REGULAR = 1
    }

    /**
     * Common [RecyclerView.ViewHolder] for [HomeFreezerListAdapter]
     *
     * @property type   Type of the view holder
     * @param itemView  View of the layout
     */
    abstract inner class ViewHolder(itemView: View, internal val type: Int) : RecyclerView.ViewHolder(itemView)

    /**
     * Empty [ViewHolder] for when there are no freezers.
     *
     * @param itemView  View of the layout
     */
    inner class NoneViewHolder(itemView: View) : ViewHolder(itemView, VIEW_TYPE_NONE)

    /**
     * Typical [ViewHolder] for a regular freezer entry
     *
     * @param itemView  The[View] that this view holder represents
     */
    inner class RegularViewHolder(itemView: View) : ViewHolder(itemView, VIEW_TYPE_REGULAR) {
        private val name: TextView = itemView.findViewById(R.id.component_freezer_overview_name)
        private val temperature: TextView = itemView.findViewById(R.id.component_freezer_overview_temp)
        private val humidity: TextView = itemView.findViewById(R.id.component_freezer_overview_humidity)
        private val battery: TextView = itemView.findViewById(R.id.component_freezer_overview_battery)
        private val lastUpdate: TextView = itemView.findViewById(R.id.component_freezer_overview_last_update)
        private val alertIcon: ImageView = itemView.findViewById(R.id.component_freezer_overview_alert_icon)
        private val favoriteIcon: ImageView = itemView.findViewById(R.id.component_freezer_overview_favorite)

        /**
         * Bind the freezer's data to this view holder and display it.
         * Try to display the matching record data if it exists
         *
         * @param freezer   The freezer to bind
         * @param alert     The type of alert (0 if none)
         */
        internal fun bindData(freezer: Freezer, record: FreezerRecord?, alert: Int) {
            val context = name.context

            //hide favorites
            favoriteIcon.visibility = View.GONE

            name.text = freezer.name
            if (record == null) {
                context.getString(R.string.component_freezer_overview_no_record).apply {
                    temperature.text = this
                    humidity.text = this
                    battery.text = this
                    lastUpdate.text = this
                }
            } else {
                temperature.text = context.getString(R.string.component_freezer_overview_temp, record.temperature)
                humidity.text = context.getString(R.string.component_freezer_overview_humidity, record.humidity)
                battery.text = context.getString(R.string.component_freezer_overview_battery, record.battery)
                lastUpdate.text = context.getString(R.string.component_freezer_overview_last_update, record.time)
            }

            when (alert) {
                Alert.TYPE_NONE -> {
                    alertIcon.visibility = View.GONE
                }
                Alert.TYPE_URGENT -> {
                    alertIcon.visibility = View.VISIBLE
                    alertIcon.setColorFilter(ContextCompat.getColor(context, R.color.urgent))
                }
                Alert.TYPE_WARNING -> {
                    alertIcon.visibility = View.VISIBLE
                    alertIcon.setColorFilter(ContextCompat.getColor(context, R.color.warning))
                }
            }

            // add callbacks
            alertIcon.setOnClickListener(Listener(freezer.boxId))
        }
    }

    /**
     * OnClick Listener for each item (i.e. [ViewHolder])
     *
     * @property boxId    The boxId of the freezer this listener is for
     */
    internal inner class Listener(private val boxId: Long) : View.OnClickListener {
        override fun onClick(p0: View?) {
            alertCallback(boxId)
        }
    }

    private var freezers: MutableList<Freezer> = mutableListOf()
    private var records: MutableMap<Long, FreezerRecord> = mutableMapOf()
    private var urgents: MutableMap<Long, Int> = mutableMapOf() // Map<boxId, count>
    private var warnings: MutableMap<Long, Int> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentFreezerOverviewListAdapter.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NONE -> {
                LayoutInflater.from(parent.context).inflate(R.layout.component_freezer_overview_item_none, parent, false).let { view ->
                    NoneViewHolder(view)
                }
            }
            VIEW_TYPE_REGULAR -> {
                LayoutInflater.from(parent.context).inflate(R.layout.component_freezer_overview_item, parent, false).let { view ->
                    RegularViewHolder(view)
                }
            }
            else -> {
                val message = "Somehow got a invalid ViewHolder type: $viewType"
                throw IllegalStateException(message)
            }
        }
    }

    override fun onBindViewHolder(holder: ComponentFreezerOverviewListAdapter.ViewHolder, position: Int) {
        // don't need to bind data for VIEW_TYPE_NONE
        when (holder.type) {
            VIEW_TYPE_REGULAR -> {
                (holder as RegularViewHolder).apply {
                    val freezer = freezers[position]
                    bindData(freezer, records[freezer.boxId], determineAlertType(freezer.boxId))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (freezers.isEmpty()) {
            1 // for VIEW_TYPE_NONE
        } else {
            freezers.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (freezers.isEmpty()) {
            HomeFreezerListAdapter.VIEW_TYPE_NONE
        } else {
            HomeFreezerListAdapter.VIEW_TYPE_REGULAR
        }
    }

    /**
     * Update freezers to the recycler view
     * Note: duplicates will be added if they are present
     *
     * @param freezerList  Freezers to add
     */
    internal fun updateFreezers(freezerList: MutableList<Freezer>) {
        freezers = freezerList.toMutableList()
    }

    /**
     * Update records in the recycler view
     *
     * @param recordList    Records to add
     */
    internal fun updateRecords(recordList: MutableList<FreezerRecord>) {
        // convert to map by the boxId, and reduce it by keeping the most recent timestamp
        records = recordList.groupingBy { it.boxId }.reduce { _, mostRecentRecord, currentRecord ->
            if (mostRecentRecord.time.after(currentRecord.time)) {
                mostRecentRecord    //this is after current record
            } else {
                currentRecord
            }
        }.toMutableMap()
    }

    /**
     * Update all urgent alerts in the recycler view, replacing duplicates
     *
     * @param urgentsList   Urgent alerts to add
     */
    internal fun updateUrgents(urgentsList: MutableSet<Alert>) {
        urgentsList.groupingBy { it.boxId }.eachCount().apply {
            urgents = this.toMutableMap()
        }
    }

    /**
     * Update all warning alerts in the recycler view, replacing duplicates
     *
     * @param warningsList  Warning alerts to add
     */
    internal fun updateWarnings(warningsList: MutableSet<Alert>) {
        warningsList.groupingBy { it.boxId }.eachCount().apply {
            warnings = this.toMutableMap()
        }
    }

    /**
     * Helper method to determine the alert type of a freezer
     *
     * @param id    The boxId of the freezer
     * @return  An [Alert] type, with the most severe type if multiple alerts exist
     */
    private fun determineAlertType(id: Long): Int {
        //check urgents
        if (urgents.containsKey(id)) return Alert.TYPE_URGENT

        //check warnings
        if (warnings.containsKey(id)) return Alert.TYPE_WARNING

        // no alerts found
        return Alert.TYPE_NONE
    }
}