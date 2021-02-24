package com.example.coolerthanyou.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import javax.inject.Inject

class HomeViewModel @Inject constructor() : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Main Application Page"
    }

    fun getPageText(): LiveData<String> = _text

    private val _temperaturePlotData = MutableLiveData<LineData>().apply {
        val entries = populateData(getData())
        val lineDataSet = LineDataSet(entries, "testData")

        lineDataSet.setColor(5, 4)
        value = LineData(lineDataSet)
    }
    fun getTemperaturePlotData(): LiveData<LineData> = _temperaturePlotData

    private val _humidityPlotData = MutableLiveData<LineData>().apply {
        val entries = populateData(getData())
        val lineDataSet = LineDataSet(entries, "testData")

        lineDataSet.setColor(5, 4)
        value = LineData(lineDataSet)
    }

    fun getHumidityPlotData(): LiveData<LineData> = _humidityPlotData

    /**
     * Helper function to convert x y data into [Entry] for the graphs
     *
     * @param dataToUse Array of x,y data
     * @return  List of [Entry] representation of [dataToUse]
     */
    private fun populateData(dataToUse: Array<Pair<Float, Float>>): MutableList<Entry> {
        val entries: MutableList<Entry> = ArrayList()

        for (data in dataToUse) {
            val newEntry = Entry(data.first, data.second)
            entries.add(newEntry)
        }
        return entries
    }

    /**
     * TODO: replace with proper sample data entry
     */
    private fun getData(): Array<Pair<Float, Float>> {
        return arrayOf(
            Pair(0f, 0f),
            Pair(1f, 1f),
            Pair(2f, 4f),
            Pair(3f, 9f),
            Pair(4f, 16f)
        )
    }
}