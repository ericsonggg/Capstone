package com.example.coolerthanyou.ui.details

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coolerthanyou.BaseFragment
import com.example.coolerthanyou.R
import com.example.coolerthanyou.model.Alert
import com.example.coolerthanyou.model.Freezer
import com.example.coolerthanyou.ui.DateValueFormatter
import com.example.coolerthanyou.ui.MainActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fragment that shows the details and controls for a particular freezer
 */
class DetailsFragment : BaseFragment() {

    private val logTag: String = "DetailsFragment"
    private val _detailsViewModel: DetailsViewModel by viewModels({ activity as MainActivity }) { viewModelFactory }

    private val alertsAdapter = DetailsAlertsListAdapter(::solveAlert)
    private lateinit var alertsList: RecyclerView
    private lateinit var overviewName: TextView
    private lateinit var overviewTemperature: TextView
    private lateinit var overviewHumidity: TextView
    private lateinit var overviewBattery: TextView
    private lateinit var overviewLastUpdate: TextView
    private lateinit var overviewFavorite: ImageView
    private lateinit var nameEdit: EditText
    private lateinit var temperatureEdit: EditText
    private lateinit var humidityEdit: EditText
    private lateinit var samplingRateEdit: EditText
    private lateinit var powerSwitch: Switch
    private lateinit var btAddress: TextView
    private lateinit var btConnectSwitch: Switch
    private lateinit var btSampleButton: Button
    private lateinit var removeButton: Button
    private lateinit var temperatureChart: LineChart
    private lateinit var humidityChart: LineChart

    private lateinit var invalidName: String
    private lateinit var invalidTemperature: String
    private lateinit var invalidHumidity: String
    private lateinit var invalidSamplingRate: String
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
        invalidName = getString(R.string.fragment_details_controls_name_invalid_toast, Freezer.MAX_NAME_LENGTH)
        invalidTemperature =
            getString(
                R.string.fragment_details_controls_temperature_invalid_toast,
                Freezer.MIN_TEMPERATURE.toInt(),
                Freezer.MAX_TEMPERATURE.toInt()
            )
        invalidHumidity =
            getString(R.string.fragment_details_controls_humidity_invalid_toast, Freezer.MIN_HUMIDITY.toInt(), Freezer.MAX_HUMIDITY.toInt())
        invalidSamplingRate =
            getString(R.string.fragment_details_controls_sampling_rate_invalid_toast, Freezer.MIN_SAMPLING_RATE, Freezer.MAX_SAMPLING_RATE)
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
        overviewFavorite = view.findViewById(R.id.component_freezer_overview_favorite)
        nameEdit = view.findViewById(R.id.fragment_details_controls_name)
        temperatureEdit = view.findViewById(R.id.fragment_details_controls_temperature)
        humidityEdit = view.findViewById(R.id.fragment_details_controls_humidity)
        samplingRateEdit = view.findViewById(R.id.fragment_details_controls_sampling_rate)
        powerSwitch = view.findViewById(R.id.fragment_details_controls_power)
        btAddress = view.findViewById(R.id.fragment_details_controls_address)
        btConnectSwitch = view.findViewById(R.id.fragment_details_controls_connect)
        btSampleButton = view.findViewById(R.id.fragment_details_controls_manual_sample)
        removeButton = view.findViewById(R.id.fragment_details_controls_remove)
        temperatureChart = view.findViewById(R.id.fragment_details_history_temperature_chart)
        humidityChart = view.findViewById(R.id.fragment_details_history_humidity_chart)

        //charts
        temperatureChart.apply {
            isKeepPositionOnRotation = true
            description = Description().apply {
                isEnabled = false
            }
            axisRight.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = DateValueFormatter.MILLIS_PER_MIN.toFloat()
                valueFormatter = DateValueFormatter()
            }
        }
        humidityChart.apply {
            isKeepPositionOnRotation = true
            description = Description().apply {
                isEnabled = false
            }
            axisRight.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = DateValueFormatter.MILLIS_PER_MIN.toFloat()
                valueFormatter = DateValueFormatter()
            }
        }
        overviewFavorite.setOnClickListener {
            _detailsViewModel.toggleFavorite()
        }

        // don't need to show alerts icon
        view.findViewById<ImageView>(R.id.component_freezer_overview_alert_icon).apply {
            visibility = View.GONE
        }

        view.findViewById<ImageView>(R.id.fragment_details_controls_temperature_up).setOnClickListener {
            if (!_detailsViewModel.changeTemperature(1f)) {
                showToast(invalidTemperature)
            }
        }
        view.findViewById<ImageView>(R.id.fragment_details_controls_temperature_down).setOnClickListener {
            if (!_detailsViewModel.changeTemperature(-1f)) {
                showToast(invalidTemperature)
            }
        }
        view.findViewById<ImageView>(R.id.fragment_details_controls_humidity_up).setOnClickListener {
            if (!_detailsViewModel.changeHumidity(5f)) {
                showToast(invalidHumidity)
            }
        }
        view.findViewById<ImageView>(R.id.fragment_details_controls_humidity_down).setOnClickListener {
            if (!_detailsViewModel.changeHumidity(-5f)) {
                showToast(invalidHumidity)
            }
        }
        view.findViewById<ImageView>(R.id.fragment_details_controls_sampling_rate_up).setOnClickListener {
            if (!_detailsViewModel.changeSamplingRate(1)) {
                showToast(invalidSamplingRate)
            }
        }
        view.findViewById<ImageView>(R.id.fragment_details_controls_sampling_rate_down).setOnClickListener {
            if (!_detailsViewModel.changeSamplingRate(-1)) {
                showToast(invalidSamplingRate)
            }
        }

        // Temperature "More"
        view.findViewById<TextView>(R.id.fragment_details_history_temperature_more_label).apply {
            setOnClickListener {
                navigateToHistory()
            }
        }
        view.findViewById<ImageView>(R.id.fragment_details_history_temperature_more_icon).apply {
            setOnClickListener {
                navigateToHistory()
            }
        }

        // Humidity "More"
        view.findViewById<TextView>(R.id.fragment_details_history_humidity_more_label).apply {
            setOnClickListener {
                navigateToHistory()
            }
        }
        view.findViewById<ImageView>(R.id.fragment_details_history_humidity_more_icon).apply {
            setOnClickListener {
                navigateToHistory()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        logger.d(logTag, "onStart")

        alertsList.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = alertsAdapter
        }

        // do action when focus is lost on edit text
        nameEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!_detailsViewModel.setName(nameEdit.text.toString())) {
                    showToast(invalidName)
                }
            }
        }
        temperatureEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!_detailsViewModel.setTemperature(temperatureEdit.text.toString().toFloat())) {
                    showToast(invalidTemperature)
                }
            }
        }
        humidityEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!_detailsViewModel.setHumidity(humidityEdit.text.toString().toFloat())) {
                    showToast(invalidHumidity)
                }
            }
        }
        samplingRateEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!_detailsViewModel.setSamplingRate(samplingRateEdit.text.toString().toInt())) {
                    showToast(invalidSamplingRate)
                }
            }
        }

        powerSwitch.setOnCheckedChangeListener { switch, isChecked ->
            _detailsViewModel.setPower(isChecked)
            if (isChecked) {
                switch.text = powerOnLabel
            } else {
                switch.text = powerOffLabel
            }
        }
        btConnectSwitch.setOnCheckedChangeListener { switch, isChecked ->
            if (isChecked) {
                tryToConnectBluetooth()
                switch.text = btConnectYesLabel
            } else {
                disconnectBluetooth()
                switch.text = btConnectNoLabel
            }
        }
        btSampleButton.setOnClickListener {
            tryToManualSample()
        }
        removeButton.setOnClickListener {
            showRemoveDialog()
        }

        _detailsViewModel.getFreezer().observe(this, Observer { freezer ->
            val mainActivity = (activity as MainActivity)
            mainActivity.updateActionBar(getString(R.string.fragment_details_appbar_title, freezer.name))
            overviewName.text = freezer.name
            powerSwitch.isChecked = freezer.set_power_on
            powerSwitch.text = if (freezer.set_power_on) powerOnLabel else powerOffLabel
            btAddress.text = freezer.bluetoothAddress

            if (freezer.is_favorite) {
                overviewFavorite.setImageResource(R.drawable.ic_heart)
            } else {
                overviewFavorite.setImageResource(R.drawable.ic_heart_outline)
            }

            //send bluetooth update if necessary
            when (_detailsViewModel.getUpdateFlag()) {
                DetailsViewModel.Companion.UpdateFlag.UPDATE_NAME -> {
                    mainActivity.updateName(freezer)
                }
                DetailsViewModel.Companion.UpdateFlag.UPDATE_REFRESH_RATE -> {
                    mainActivity.updateRefreshRate(freezer)
                }
                DetailsViewModel.Companion.UpdateFlag.UPDATE_SETTINGS -> {
                    mainActivity.updateSettings(freezer)
                }
                DetailsViewModel.Companion.UpdateFlag.NO_UPDATE -> {
                    // do nothing
                }
            }
        })
        _detailsViewModel.getAlerts().observe(this, Observer { alerts ->
            alertsAdapter.updateAlerts(alerts.filter { !it.solved })
            alertsAdapter.notifyDataSetChanged()
        })
        _detailsViewModel.getRecords().observe(this, Observer { records ->
            //populate charts
            val tempEntries: MutableList<Entry> = mutableListOf()
            val humidEntries: MutableList<Entry> = mutableListOf()
            var time: Float

            records.sortedBy { it.time }.apply {
                for (record in this) {
                    time = record.time.time.toFloat()
                    tempEntries.add(Entry(time, record.temperature))
                    humidEntries.add(Entry(time, record.humidity))
                }
            }

            LineData().apply {
                addDataSet(LineDataSet(tempEntries, temperatureChartLabel).apply {
                    color = ContextCompat.getColor(requireContext(), R.color.chart_temperature)
                })
                temperatureChart.data = this
            }
            temperatureChart.invalidate()

            LineData().apply {
                addDataSet(LineDataSet(humidEntries, humidityChartLabel).apply {
                    color = ContextCompat.getColor(requireContext(), R.color.chart_humidity)
                })
                humidityChart.data = this
            }
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
        _detailsViewModel.getEditName().observe(this, Observer { name ->
            nameEdit.editableText.apply {
                clear()
                append(name)
                nameEdit.text = this
            }
        })
        _detailsViewModel.getEditTemperature().observe(this, Observer { temperature ->
            temperatureEdit.editableText.apply {
                clear()
                append(temperature.toString())
                temperatureEdit.text = this
            }
        })
        _detailsViewModel.getEditHumidity().observe(this, Observer { humidity ->
            humidityEdit.editableText.apply {
                clear()
                append(humidity.toString())
                humidityEdit.text = this
            }
        })
        _detailsViewModel.getEditSamplingRate().observe(this, Observer { samplingRate ->
            samplingRateEdit.editableText.apply {
                clear()
                append(samplingRate.toString())
                samplingRateEdit.text = this
            }
        })
    }

    /**
     * Navigate to the history fragment
     */
    private fun navigateToHistory() {
        DetailsFragmentDirections.actionNavDetailsToNavHistory().apply {
            findNavController().navigate(this)
        }
    }

    /**
     * Prompt the user whether they want to dismiss an alert
     *
     * @param alert The alert to solve
     */
    private fun solveAlert(alert: Alert) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.fragment_details_alert_dialog_title)
            .setMessage(alert.message)
            .setCancelable(true)
            .setPositiveButton(R.string.yes) { dialogInterface, _ ->
                _detailsViewModel.dismissAlert(alert)
                alertsAdapter.removeAlert(alert).let {
                    if (it != -1) {
                        alertsAdapter.notifyItemRemoved(it)
                    }
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton(R.string.no) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .apply {
                show()
                setDefaults()
            }
    }

    /**
     * Try to connect via bluetooth
     */
    private fun tryToConnectBluetooth() {
        val freezer = _detailsViewModel.getFreezer().value

        if (freezer == null) {
            // terminate early if the view model doesn't have the data
            Toast.makeText(requireContext(), R.string.try_again, Toast.LENGTH_SHORT).show()
        } else {
            // do work in background
            lifecycleScope.launch(Dispatchers.Default) {
                (activity as MainActivity).apply {
                    if (!isBluetoothOn()) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(requireContext(), R.string.fragment_details_bluetooth_must_be_on, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        tryConnect(freezer)
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                R.string.fragment_details_controls_connect_connecting_toast,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Disconnect from bluetooth
     */
    private fun disconnectBluetooth() {
        val freezer = _detailsViewModel.getFreezer().value

        if (freezer == null) {
            // terminate early if the view model doesn't have the data
            Toast.makeText(requireContext(), R.string.try_again, Toast.LENGTH_SHORT).show()
        } else {
            // do work in background
            lifecycleScope.launch(Dispatchers.Default) {
                (activity as MainActivity).apply {
                    disconnect(freezer)
                }
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(requireContext(), R.string.fragment_details_controls_connect_disconnecting_toast, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    /**
     * Try to manually sample the freezer
     * Show toasts for success/failure
     */
    private fun tryToManualSample() {
        val freezer = _detailsViewModel.getFreezer().value

        if (freezer == null) {
            // terminate early if the view model doesn't have the data
            Toast.makeText(requireContext(), R.string.try_again, Toast.LENGTH_SHORT).show()
        } else {
            // do work in background
            lifecycleScope.launch(Dispatchers.Default) {
                (activity as MainActivity).apply {
                    if (!isBluetoothOn()) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Toast.makeText(requireContext(), R.string.fragment_details_bluetooth_must_be_on, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (checkIfConnected(freezer)) {
                            manualSample(freezer)
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(requireContext(), R.string.fragment_details_controls_manual_sample_toast, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.fragment_details_controls_manual_sample_disconnected_toast,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Show the remove dialog after button press
     */
    private fun showRemoveDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.fragment_details_controls_remove_dialog_title)
            .setMessage(R.string.fragment_details_controls_remove_dialog_message)
            .setCancelable(false)
            .setPositiveButton(R.string.yes) { _, _ ->
                _detailsViewModel.removeFreezer {
                    // exit this fragment and return to home
                    DetailsFragmentDirections.actionNavDetailsToNavHome().apply {
                        findNavController().navigate(this)
                    }
                }
                //don't dismiss dialog until background task is done
            }
            .setNegativeButton(R.string.no) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .apply {
                show()
                setDefaults()
            }
    }

    /**
     * Helper method to show short toasts
     *
     * @param msg   The message to show
     */
    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}
