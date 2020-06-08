package com.example.coolerthanyou.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coolerthanyou.model.Freezer
import com.example.coolerthanyou.repository.FreezerRepository
import com.example.coolerthanyou.ui.IDataContainer
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val freezerRepository: FreezerRepository) : ViewModel() {

    //TODO: remove after testing, see testing comment below
    init {
        testDatabase()
    }

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

    //TODO: Remove this after testing is done! When the PR is approved, I'll remove this block and re-commit.
    private fun testDatabase() {
        // GlobalScope.launch is a Kotlin-specific way of running code in a different thread.
        // GlobalScope literally means this thread will never prematurely die as long as the app is running
        // Obviously this is terrible for real code LOL
        GlobalScope.launch {
            // delete all
            Log.d("KEK", "---Starting database wipe---")
            val allBoxes = freezerRepository.getAllFreezers()
            Log.d("KEK", "Found " + allBoxes.size + " items, listing all items: " + allBoxes?.toString())
            for (box in allBoxes) {
                freezerRepository.deleteFreezer(box)
            }
            val remainingBoxes = freezerRepository.getAllFreezers()
            Log.d("KEK", "" + remainingBoxes.size + " items remaining")
            Log.d("KEK", "---Finished database wipe---")

            // test
            Log.d("KEK", "---Starting test---")
            val kek = Freezer("ABC", "name", 20.0, 30.0)
            freezerRepository.addFreezer(kek)
            Log.d("KEK", "A " + freezerRepository.getAllFreezers().toString())
            Log.d("KEK", "B " + freezerRepository.getFreezerById("ABC")?.toString())
            Log.d("KEK", "C " + freezerRepository.getFreezerById("name")?.toString())
            Log.d("KEK", "D " + freezerRepository.getFreezerByName("name")?.toString())
            Log.d("KEK", "E " + freezerRepository.getFreezerByName("ABC")?.toString())
            freezerRepository.deleteFreezer(kek)
            Log.d("KEK", "F " + freezerRepository.getAllFreezers().toString())
        }
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