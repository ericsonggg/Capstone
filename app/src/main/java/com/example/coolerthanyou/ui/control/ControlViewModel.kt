package com.example.coolerthanyou.ui.control

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.coolerthanyou.BaseViewModel
import javax.inject.Inject

/**
 * ViewModel for [ControlFragment]
 */
class ControlViewModel @Inject constructor() : BaseViewModel() {

    private val _boxList: Array<String> by lazy { retrieveConnectedBoxes() }
    private var _currentBox: String = "Box 1"
    private val _currentTemperature = MutableLiveData<Int>().apply { value = 4 }
    private val _currentHumidity = MutableLiveData<Int>().apply { value = 6 }
    private val _targetTemperature = MutableLiveData<Int>().apply { value = 0 }
    private val _targetHumidity = MutableLiveData<Int>().apply { value = 0 }
    private val _temperatureRange: Pair<Int, Int> by lazy { retrieveTemperatureRange() }
    private val _humidityRange: Pair<Int, Int> by lazy { retrieveHumidityRange() }
    private val _temperatureHistory: Map<Int, Int> by lazy { retrieveTemperatureHistory() }
    private val _humidityHistory: Map<Int, Int> by lazy { retrieveHumidityHistory() }

    fun getBoxList(): Array<String> = _boxList
    fun getCurrentTemperature(): LiveData<Int> = _currentTemperature
    fun getCurrentHumidity(): LiveData<Int> = _currentHumidity
    fun getTargetTemp(): LiveData<Int> = _targetTemperature
    fun getTargetHumidity(): LiveData<Int> = _targetHumidity
    fun getTemperatureRange(): Pair<Int, Int> = _temperatureRange
    fun getHumidityRange(): Pair<Int, Int> = _humidityRange
    fun getTemperatureHistory(): Map<Int, Int> = _temperatureHistory
    fun getHumidityHistory(): Map<Int, Int> = _humidityHistory

    /**
     * TODO: replace with string resources and retrieve it via data source!
     */
    fun getTemperatureUnit(): String = "C"
    fun getHumidityUnit(): String = "%"

    /**
     * TODO: add actual functionality
     *
     * @param position  Position in [_boxList]
     */
    fun setCurrentBox(position: Int) {
        if (position >= 0 && position < _boxList.size) {
            _currentBox = _boxList[position]
        }
    }

    /**
     * Validate and set the new target temperature
     *
     * @param target    New target temperature
     */
    fun setTargetTemperature(target: Int) {
        _targetTemperature.value = target
    }

    /**
     * TODO: Send the Box controller the new temp
     */
    fun uploadTargetTemperature() {}

    /**
     * Validate and set the new target humidity
     *
     * @param target    New target humidity
     */
    fun setTargetHumidity(target: Int) {
        _targetHumidity.value = target
    }

    /**
     * TODO: Send the box controller the new humidity
     */
    fun uploadTargetHumidity() {}

    /**
     * TODO: replace with repository
     */
    private fun retrieveConnectedBoxes(): Array<String> {
        return arrayOf(
            "Box 1",
            "Box 2",
            "Box 3"
        )
    }

    /**
     * TODO: replace with proper repository
     */
    private fun retrieveTemperatureRange(): Pair<Int, Int> {
        return Pair(2, 20)
    }

    /**
     * TODO: replace with proper repository
     */
    private fun retrieveHumidityRange(): Pair<Int, Int> {
        return Pair(0, 30)
    }

    /**
     * TODO: replace with proper repository
     */
    private fun retrieveTemperatureHistory(): Map<Int, Int> {
        return mapOf(
            0 to 0,
            1 to 1,
            2 to 2,
            3 to 3,
            4 to 5,
            5 to 8,
            6 to 13
        )
    }

    /**
     * TODO: replace with proper repository
     */
    private fun retrieveHumidityHistory(): Map<Int, Int> {
        return mapOf(
            0 to 5,
            1 to 11,
            2 to 15,
            3 to 20,
            4 to 26,
            5 to 33,
            6 to 45
        )
    }
}