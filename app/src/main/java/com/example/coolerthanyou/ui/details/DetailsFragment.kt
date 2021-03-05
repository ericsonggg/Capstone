package com.example.coolerthanyou.ui.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coolerthanyou.BaseFragment
import com.example.coolerthanyou.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

/**
 * Fragment that shows the details and controls for a particular freezer
 */
class DetailsFragment : BaseFragment() {

    companion object {
        private const val millisPerHour: Float = 3600000f
    }

    private val logTag: String = "DetailsFragment"
    private val _detailsViewModel: DetailsViewModel by viewModels { viewModelFactory }

    private lateinit var alertsList: RecyclerView
    private lateinit var overviewName: TextView
    private lateinit var overviewTemperature: TextView
    private lateinit var overviewHumidity: TextView
    private lateinit var overviewBattery: TextView
    private lateinit var overviewLastUpdate: TextView
    private lateinit var nameEdit: EditText
    private lateinit var temperatureEdit: EditText
    private lateinit var temperatureUp: ImageView
    private lateinit var temperatureDown: ImageView
    private lateinit var humidityEdit: EditText
    private lateinit var humidityUp: ImageView
    private lateinit var humidityDown: ImageView
    private lateinit var samplingRateEdit: EditText
    private lateinit var samplingRateUp: ImageView
    private lateinit var samplingRateDown: ImageView
    private lateinit var powerSwitch: Switch
    private lateinit var btAddress: TextView
    private lateinit var btConnectSwitch: Switch
    private lateinit var btSampleButton: Button
    private lateinit var removeButton: Button
    private lateinit var temperatureChart: LineChart
    private lateinit var humidityChart: LineChart

    private lateinit var powerOnLabel: String
    private lateinit var powerOffLabel: String
    private lateinit var btConnectYesLabel: String
    private lateinit var btConnectNoLabel: String
    private lateinit var temperatureChartLabel: String
    private lateinit var humidityChartLabel: String
    private lateinit var timeChartLabel: String

    override fun onAttach(context: Context) {
        super.onAttach(context)

        application.appComponent.mainComponent().create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        logger.d(logTag, "onCreateView")
        root = inflater.inflate(R.layout.fragment_details, container, false)

        // parse navigation args
        val args: DetailsFragmentArgs by navArgs()
        _detailsViewModel.getFreezerInfo(args.boxId)

        // get permanent string resources
        powerOnLabel = getString(R.string.on)
        powerOffLabel = getString(R.string.off)
        btConnectYesLabel = getString(R.string.yes)
        btConnectNoLabel = getString(R.string.no)
        temperatureChartLabel = getString(R.string.temperature)
        humidityChartLabel = getString(R.string.humidity)
        timeChartLabel = getString(R.string.time)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.d(logTag, "onViewCreated")

        alertsList = view.findViewById(R.id.fragment_details_alerts_list)
        overviewName = view.findViewById(R.id.component_freezer_overview_name)
        overviewTemperature = view.findViewById(R.id.component_freezer_overview_temp)
        overviewHumidity = view.findViewById(R.id.component_freezer_overview_humidity)
        overviewBattery = view.findViewById(R.id.component_freezer_overview_battery)
        overviewLastUpdate = view.findViewById(R.id.component_freezer_overview_last_update)
        nameEdit = view.findViewById(R.id.fragment_details_controls_name)
        temperatureEdit = view.findViewById(R.id.fragment_details_controls_temperature)
        temperatureUp = view.findViewById(R.id.fragment_details_controls_temperature_up)
        temperatureDown = view.findViewById(R.id.fragment_details_controls_temperature_down)
        humidityEdit = view.findViewById(R.id.fragment_details_controls_humidity)
        humidityUp = view.findViewById(R.id.fragment_details_controls_humidity_up)
        humidityDown = view.findViewById(R.id.fragment_details_controls_humidity_down)
        samplingRateEdit = view.findViewById(R.id.fragment_details_controls_sampling_rate)
        samplingRateUp = view.findViewById(R.id.fragment_details_controls_sampling_rate_up)
        samplingRateDown = view.findViewById(R.id.fragment_details_controls_sampling_rate_down)
        powerSwitch = view.findViewById(R.id.fragment_details_controls_power)
        btAddress = view.findViewById(R.id.fragment_details_controls_address)
        btConnectSwitch = view.findViewById(R.id.fragment_details_controls_connect)
        btSampleButton = view.findViewById(R.id.fragment_details_controls_manual_sample)
        removeButton = view.findViewById(R.id.fragment_details_controls_remove)
        temperatureChart = view.findViewById(R.id.fragment_details_history_temperature_chart)
        humidityChart = view.findViewById(R.id.fragment_details_history_humidity_chart)

        // don't need to show alerts icon
        view.findViewById<ImageView>(R.id.component_freezer_overview_alert_icon).apply {
            visibility = View.GONE
        }

        // Temperature "More"
        view.findViewById<TextView>(R.id.fragment_details_history_temperature_more_label).apply {
            setOnClickListener {
                navigateTemperatureHistory()
            }
        }
        view.findViewById<ImageView>(R.id.fragment_details_history_temperature_more_icon).apply {
            setOnClickListener {
                navigateTemperatureHistory()
            }
        }

        // Humidity "More"
        view.findViewById<TextView>(R.id.fragment_details_history_humidity_more_label).apply {
            setOnClickListener {
                navigateHumidityHistory()
            }
        }
        view.findViewById<ImageView>(R.id.fragment_details_history_humidity_more_icon).apply {
            setOnClickListener {
                navigateHumidityHistory()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        logger.d(logTag, "onStart")

        val alertsAdapter = DetailsAlertsListAdapter()
        alertsList.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = alertsAdapter
        }

        _detailsViewModel.getFreezer().observe(this, Observer { freezer ->
            overviewName.text = freezer.name

            nameEdit.setText(freezer.name)
            temperatureEdit.setText(freezer.set_temperature.toString())
            humidityEdit.setText(freezer.set_humidity.toString())
            samplingRateEdit.setText(freezer.sampling_rate.toString())
            powerSwitch.isChecked = freezer.set_power_on
            powerSwitch.text = if (freezer.set_power_on) powerOnLabel else powerOffLabel
            btAddress.text = freezer.bluetoothAddress
        })
        _detailsViewModel.getAlerts().observe(this, Observer { alerts ->
            alertsAdapter.updateAlerts(alerts)
            alertsAdapter.notifyDataSetChanged()
        })
        _detailsViewModel.getRecords().observe(this, Observer { records ->
            //populate charts
            val tempEntries: MutableList<Entry> = mutableListOf()
            val humidEntries: MutableList<Entry> = mutableListOf()
            var time: Float

            val sortedRecords = records.sortedBy { it.time }
            if (android.os.Build.VERSION.SDK_INT >= 26) {
                for (record in sortedRecords) {
                    time = record.time.toInstant().toEpochMilli() / millisPerHour
                    tempEntries.add(Entry(time, record.temperature))
                    humidEntries.add(Entry(time, record.humidity))
                }
            } else {
                for (record in sortedRecords) {
                    time = record.time.time / millisPerHour
                    tempEntries.add(Entry(time, record.temperature))
                    humidEntries.add(Entry(time, record.humidity))
                }
            }

            temperatureChart.data = LineData(LineDataSet(tempEntries, temperatureChartLabel))
            temperatureChart.invalidate()
            humidityChart.data = LineData(LineDataSet(humidEntries, humidityChartLabel))
            humidityChart.invalidate()
        })
        _detailsViewModel.getLatestRecord().observe(this, Observer { latestRecord ->
            if (latestRecord == null) {
                getString(R.string.component_freezer_overview_no_record).apply {
                    overviewTemperature.text = this
                    overviewHumidity.text = this
                    overviewBattery.text = this
                    overviewLastUpdate.text = this
                }
            } else {
                //populate overview
                overviewTemperature.text = getString(R.string.component_freezer_overview_temp, latestRecord.temperature)
                overviewHumidity.text = getString(R.string.component_freezer_overview_humidity, latestRecord.humidity)
                overviewBattery.text = getString(R.string.component_freezer_overview_battery, latestRecord.battery)
                overviewLastUpdate.text = getString(R.string.component_freezer_overview_last_update, latestRecord.time)
            }
        })
    }

    /**
     * Navigate to the temperature history
     */
    private fun navigateTemperatureHistory() {

    }

    /**
     * Navigate to the humidity history
     */
    private fun navigateHumidityHistory() {

    }
}
