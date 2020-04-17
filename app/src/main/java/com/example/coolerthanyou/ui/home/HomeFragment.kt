package com.example.coolerthanyou.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
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

        var entries = populateData(getData())
        var lineDataSet = LineDataSet(entries,"testData")

        lineDataSet.setColor(5,4)

        var lineData = LineData(lineDataSet)
        temperatureChart.setData(lineData)
        humidityChart.setData(lineData)
        temperatureChart.invalidate()

        notificationTextView.text = "No Notifications to display"
        return root
    }

    private fun populateData(dataToUse: Array<ChartDataContainer>) : MutableList<Entry>{
        val entries: MutableList<Entry> = ArrayList()

        for (data in dataToUse){
            val newEntry = Entry(data.getValueX(),data.getValueY())
            entries.add(newEntry)
        }
        return entries
    }

    private fun getData(): Array<ChartDataContainer>{
        val dataArray = Array(3){ChartDataContainer(0f,0f)}
        dataArray[0].setValueY(10f)
        dataArray[1].setValueX(1f)
        dataArray[1].setValueY(12f)
        dataArray[2].setValueX(2f)
        dataArray[2].setValueY(5f)
        return dataArray
    }

    class ChartDataContainer(inputX: Float, inputY: Float) : IDataContainer{
        private var valueX : Float
        private var valueY : Float

        init{
            valueX = inputX
            valueY = inputY
        }

        override fun getValueX(): Float {
            return valueX;
        }

        override fun getValueY(): Float {
            return valueY;
        }

        fun setValueX(newValueX: Float){
            valueX = newValueX
        }

        fun setValueY(newValueY: Float){
            valueY = newValueY
        }

    }

}
