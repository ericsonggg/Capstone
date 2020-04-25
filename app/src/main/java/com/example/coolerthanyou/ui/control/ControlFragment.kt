package com.example.coolerthanyou.ui.control

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.coolerthanyou.BaseFragment
import com.example.coolerthanyou.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

/**
 * Fragment that allows the user to control a specific box's settings
 */
class ControlFragment : BaseFragment() {

    private lateinit var _viewModel: ControlViewModel
    private lateinit var _currentBoxSpinner: Spinner
    private lateinit var _temperatureSlider: SeekBar
    private lateinit var _humiditySlider: SeekBar

    private val _currentBoxSpinnerListener: AdapterView.OnItemSelectedListener by lazy {
        object : AdapterView.OnItemSelectedListener {
            private var currentPosition = -1

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != currentPosition) {
                    _viewModel.setCurrentBox(position)
                    currentPosition = position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Never reached
            }
        }
    }

    private val _temperatureSliderChangeListener: SeekBar.OnSeekBarChangeListener by lazy {
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                _viewModel.setTargetTemperature(seekBar!!.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                _viewModel.uploadTargetTemperature()
            }
        }
    }

    private val _humiditySliderChangeListener: SeekBar.OnSeekBarChangeListener by lazy {
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                _viewModel.setTargetHumidity(seekBar!!.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                _viewModel.uploadTargetHumidity()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)


        application.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _viewModel = ViewModelProviders.of(this).get(ControlViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_control, container, false)

        // Init spinner
        _currentBoxSpinner = findViewById(R.id.fragment_control_box_select_spinner) as Spinner
        ArrayAdapter(
            activity as Context,
            android.R.layout.simple_spinner_item,
            _viewModel.getBoxList()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            _currentBoxSpinner.adapter = adapter
        }
        _currentBoxSpinner.onItemSelectedListener = _currentBoxSpinnerListener

        // Init current values
        val currentTemp: TextView = findViewById(R.id.fragment_control_current_temperature_value) as TextView
        _viewModel.getCurrentTemperature().observe(viewLifecycleOwner, Observer { temperature ->
            currentTemp.text = temperature.toString()
        })

        val currentHumidity: TextView = findViewById(R.id.fragment_control_current_humidity_value) as TextView
        _viewModel.getCurrentHumidity().observe(viewLifecycleOwner, Observer { humidity ->
            currentHumidity.text = humidity.toString()
        })

        // Init settings
        _temperatureSlider = findViewById(R.id.fragment_control_setting_temperature_slider) as SeekBar
        _viewModel.getTemperatureRange().also {
            _temperatureSlider.min = it.first //TODO: create API < 26 alternative
            _temperatureSlider.max = it.second
        }
        val targetTemp: TextView = findViewById(R.id.fragment_control_setting_temperature_value) as TextView
        _viewModel.getTargetTemp().observe(viewLifecycleOwner, Observer { temp ->
            targetTemp.text = temp.toString()
            if (_temperatureSlider.progress != temp) {
                _temperatureSlider.setOnSeekBarChangeListener(null)
                _temperatureSlider.progress = temp
                _temperatureSlider.invalidate()
                _temperatureSlider.setOnSeekBarChangeListener(_temperatureSliderChangeListener)
            }
        })
        _temperatureSlider.setOnSeekBarChangeListener(_temperatureSliderChangeListener)

        _humiditySlider = findViewById(R.id.fragment_control_setting_humidity_slider) as SeekBar
        _viewModel.getHumidityRange().also {
            _humiditySlider.min = it.first
            _humiditySlider.max = it.second
        }
        val targetHumidity: TextView = findViewById(R.id.fragment_control_setting_humidity_value) as TextView
        _viewModel.getTargetHumidity().observe(viewLifecycleOwner, Observer { humidity ->
            targetHumidity.text = humidity.toString()
            if (_humiditySlider.progress != humidity) {
                _humiditySlider.setOnSeekBarChangeListener(null)
                _humiditySlider.progress = humidity
                _humiditySlider.invalidate()
                _humiditySlider.setOnSeekBarChangeListener(_humiditySliderChangeListener)
            }
        })
        _humiditySlider.setOnSeekBarChangeListener(_humiditySliderChangeListener)

        // Init chart
        val historyChart: LineChart = findViewById(R.id.fragment_control_history_chart) as LineChart
        val temperatureHistory = _viewModel.getTemperatureHistory().map { Entry(it.key.toFloat(), it.value.toFloat()) }
        val temperatureLineDataSet = LineDataSet(temperatureHistory, getString(R.string.fragment_control_history_temperature_label))
        temperatureLineDataSet.setColor(Color.RED, 255)
        val humidityHistory = _viewModel.getHumidityHistory().map { Entry(it.key.toFloat(), it.value.toFloat()) }
        val humidityLineDataSet = LineDataSet(humidityHistory, getString(R.string.fragment_control_history_humidity_label))
        humidityLineDataSet.setColor(Color.BLUE, 255)

        historyChart.data = LineData(temperatureLineDataSet, humidityLineDataSet)
        historyChart.invalidate()

        // Init units
        val currentTempUnit: TextView = findViewById(R.id.fragment_control_current_temperature_unit) as TextView
        val settingTempUnit: TextView = findViewById(R.id.fragment_control_setting_temperature_unit) as TextView
        val currentHumidityUnit: TextView = findViewById(R.id.fragment_control_current_humidity_unit) as TextView
        val settingHumidityUnit: TextView = findViewById(R.id.fragment_control_setting_humidity_unit) as TextView
        _viewModel.getTemperatureUnit().also { temp ->
            currentTempUnit.text = temp
            settingTempUnit.text = temp
        }
        _viewModel.getHumidityUnit().also { humidity ->
            currentHumidityUnit.text = humidity
            settingHumidityUnit.text = humidity
        }

        return root
    }
}