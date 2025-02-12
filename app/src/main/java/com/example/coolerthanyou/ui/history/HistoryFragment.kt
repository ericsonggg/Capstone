package com.example.coolerthanyou.ui.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coolerthanyou.BaseFragment
import com.example.coolerthanyou.R
import com.example.coolerthanyou.ui.DateValueFormatter
import com.example.coolerthanyou.ui.MainActivity
import com.example.coolerthanyou.ui.details.DetailsViewModel
import com.example.coolerthanyou.ui.details.HistoryListAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

/**
 * Fragment that shows the records history of a particular freezer
 * Uses [DetailsViewModel]
 */
class HistoryFragment : BaseFragment() {

    private val logTag = "HistoryFragment"
    private val _detailsViewModel: DetailsViewModel by viewModels({ activity as MainActivity }) { viewModelFactory }

    private lateinit var header: TextView
    private lateinit var chart: LineChart
    private lateinit var eventList: RecyclerView

    private lateinit var temperatureChartLabel: String
    private lateinit var humidityChartLabel: String

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
        root = inflater.inflate(R.layout.fragment_history, container, false)

        // get string constant resources
        temperatureChartLabel = getString(R.string.temperature)
        humidityChartLabel = getString(R.string.humidity)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.d(logTag, "onViewCreated")

        header = view.findViewById(R.id.fragment_history_chart_header)
        chart = view.findViewById(R.id.fragment_history_chart)
        eventList = view.findViewById(R.id.fragment_history_event_list)

        chart.apply {
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

        //hide the fab since it interferes with the events list
        (activity as MainActivity).hideFab()
    }

    override fun onStart() {
        super.onStart()
        logger.d(logTag, "onStart")

        val historyAdapter = HistoryListAdapter()
        eventList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }

        _detailsViewModel.getFreezer().observe(this, Observer { freezer ->
            (activity as MainActivity).updateActionBar(getString(R.string.fragment_history_appbar_title, freezer.name))
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
                addDataSet(LineDataSet(humidEntries, humidityChartLabel).apply {
                    color = ContextCompat.getColor(requireContext(), R.color.chart_humidity)
                })
                chart.data = this
            }
            chart.invalidate()

            // update event list
            historyAdapter.updateRecords(records)
            historyAdapter.notifyDataSetChanged()
        })
        _detailsViewModel.getAlerts().observe(this, Observer { alerts ->
            historyAdapter.updateAlerts(alerts)
            historyAdapter.notifyDataSetChanged()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logger.d(logTag, "onDestroyView")

        //reshow the fab
        (activity as MainActivity).showFab()
    }
}
