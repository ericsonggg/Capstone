package com.example.coolerthanyou.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coolerthanyou.repository.IFreezerRepository
import com.example.coolerthanyou.ui.IDataContainer
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val IFreezerRepository: IFreezerRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Main Application Page"
    }
    fun getPageText(): LiveData<String> = _text

    private val _temperaturePlotData = MutableLiveData<LineData>().apply{
        var entries = populateData(getData())
        var lineDataSet = LineDataSet(entries,"testData")

        lineDataSet.setColor(5,4)
        value = LineData(lineDataSet)
    }
    fun getTemperaturePlotData(): LiveData<LineData> = _temperaturePlotData

    private val _humidityPlotData = MutableLiveData<LineData>().apply{
        var entries = populateData(getData())
        var lineDataSet = LineDataSet(entries,"testData")

        lineDataSet.setColor(5,4)
        value = LineData(lineDataSet)
    }
    fun getHumidityPlotData(): LiveData<LineData> = _humidityPlotData

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

    class ChartDataContainer(inputX: Float, inputY: Float) : IDataContainer {
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