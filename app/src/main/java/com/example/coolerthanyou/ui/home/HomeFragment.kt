package com.example.coolerthanyou.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.coolerthanyou.BaseFragment
import com.example.coolerthanyou.R
import com.github.mikephil.charting.charts.LineChart

class HomeFragment : BaseFragment(){

    private lateinit var homeViewModel: HomeViewModel

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

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_home, container, false)
        val notificationTextView : TextView = findViewById(R.id.fragment_home_text_notification_body) as TextView
        val temperatureChart : LineChart = findViewById(R.id.fragment_home_temperature_chart) as LineChart
        val humidityChart : LineChart = findViewById(R.id.fragment_home_humidity_chart) as LineChart

        homeViewModel.getTemperaturePlotData().observe(viewLifecycleOwner, Observer {
            temperatureChart.setData(it)
        })
        homeViewModel.getHumidityPlotData().observe(viewLifecycleOwner, Observer {
            humidityChart.setData(it)
        })
        temperatureChart.invalidate()

        notificationTextView.text = getText(R.string.fragment_home_no_notifications);
        return root
    }
}
