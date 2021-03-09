package com.example.coolerthanyou.ui.home

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

/**
 * List adapter for [HomeFragment]'s simple freezer list
 *
 * @property callback   Callback method to call when a device is clicked
 */
class HomeFreezerListAdapter(private val callback: (boxId: Long) -> Unit) :
    RecyclerView.Adapter<HomeFreezerListAdapter.ViewHolder>() {

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
     * @param itemView  View of the layout
     */
    inner class RegularViewHolder(itemView: View) : ViewHolder(itemView, VIEW_TYPE_REGULAR) {
        private val name: TextView = itemView.findViewById(R.id.fragment_home_freezer_item_name)
        private val alertIcon: ImageView = itemView.findViewById(R.id.fragment_home_freezer_item_alert_icon)

        /**
         * Bind the freezer's data to this view holder and display it
         *
         * @param freezer   The freezer to bind
         * @param alertType     The type of alert (0 if none)
         */
        internal fun bindData(freezer: Freezer, alertType: Int) {
            val context = name.context

            name.text = freezer.name
            when (alertType) {
                Alert.TYPE_NONE -> {
                    alertIcon.visibility = View.INVISIBLE
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
        }
    }

    /**
     * OnClick Listener for each item (i.e. [ViewHolder])
     *
     * @property boxId     The boxId of the freezer
     */
    internal inner class Listener(private val boxId: Long) : View.OnClickListener {
        override fun onClick(p0: View?) {
            callback(boxId)
        }
    }

    private var freezers: MutableList<Freezer> = mutableListOf()
    private var urgents: MutableMap<Long, Int> = mutableMapOf() // Map<boxId, count>
    private var warnings: MutableMap<Long, Int> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NONE -> {
                LayoutInflater.from(parent.context).inflate(R.layout.fragment_home_freezer_item_none, parent, false).let { view ->
                    NoneViewHolder(view)
                }
            }
            VIEW_TYPE_REGULAR -> {
                LayoutInflater.from(parent.context).inflate(R.layout.fragment_home_freezer_item, parent, false).let { view ->
                    RegularViewHolder(view)
                }
            }
            else -> {
                throw IllegalStateException("Somehow got a invalid ViewHolder type: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Don't need to bind data for VIEW_TYPE_NONE
        when (holder.type) {
            VIEW_TYPE_REGULAR -> {
                (holder as RegularViewHolder).apply {
                    val freezer = freezers[position]
                    bindData(freezer, determineAlertType(freezer.boxId))
                    itemView.setOnClickListener(Listener(freezer.boxId))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (freezers.isEmpty()) {
            1 // VIEW_TYPE_NONE
        } else {
            freezers.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (freezers.isEmpty()) {
            VIEW_TYPE_NONE
        } else {
            VIEW_TYPE_REGULAR
        }
    }

    /**
     * Update freezers to the recycler view
     * Note: duplicates will be added if they are present
     *
     * @param freezerList  Freezers to add
     */
    internal fun updateFreezers(freezerList: MutableList<Freezer>) {
        freezers = freezerList
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