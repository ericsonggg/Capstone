package com.example.coolerthanyou.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.coolerthanyou.R
import com.example.coolerthanyou.ui.IDataContainer
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val notificationTextView: TextView = root.findViewById(R.id.text_notification_body)

        val temperatureChart = root.findViewById(R.id.temperature_chart) as LineChart
        val humidityChart = root.findViewById(R.id.humidity_chart) as LineChart


        homeViewModel.temperaturePlotData.observe(viewLifecycleOwner, Observer {
            temperatureChart.setData(it)
        })
        homeViewModel.temperaturePlotData.observe(viewLifecycleOwner, Observer {
            humidityChart.setData(it)
        })
        temperatureChart.invalidate()

        notificationTextView.text = getText(R.string.fragment_home_no_notifications);
        return root
    }
}
