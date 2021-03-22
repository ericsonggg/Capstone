package com.example.coolerthanyou.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coolerthanyou.datasource.IFreezerDao
import com.example.coolerthanyou.model.Alert
import com.example.coolerthanyou.model.Freezer
import com.example.coolerthanyou.model.FreezerRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * ViewModel for [DetailsFragment], HistoryFragment and related classes.
 * [getFreezerInfo] should be called first to initialize data
 */
class DetailsViewModel @Inject constructor(protected val freezerDao: IFreezerDao) : ViewModel() {

    companion object {
        const val MAX_RECORDS: Int = 50

        enum class UpdateFlag {
            NO_UPDATE, UPDATE_NAME, UPDATE_REFRESH_RATE, UPDATE_SETTINGS
        }
    }

    private val freezer: MutableLiveData<Freezer> = MutableLiveData()
    private val alerts: MutableLiveData<List<Alert>> = MutableLiveData()
    private val records: MutableLiveData<List<FreezerRecord>> = MutableLiveData()
    private val latestRecord: MutableLiveData<FreezerRecord?> = MutableLiveData()

    private val editName: MutableLiveData<String> = MutableLiveData()
    private val editTemperature: MutableLiveData<Float> = MutableLiveData()
    private val editHumidity: MutableLiveData<Float> = MutableLiveData()
    private val editSamplingRate: MutableLiveData<Int> = MutableLiveData()

    private var updateFlag: UpdateFlag = UpdateFlag.NO_UPDATE

    /**
     * Get the information for the freezer with [boxId].
     * This method should be called FIRST to initialize view model data
     *
     * @param boxId The Freezer's boxId
     */
    internal fun getFreezerInfo(boxId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val retrievedFreezer = freezerDao.getFreezerById(boxId)

            viewModelScope.launch(Dispatchers.Main) {
                freezer.value = retrievedFreezer
                editName.value = retrievedFreezer?.name
                editTemperature.value = retrievedFreezer?.set_temperature
                editHumidity.value = retrievedFreezer?.set_humidity
                editSamplingRate.value = retrievedFreezer?.sampling_rate
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val retrievedAlerts = freezerDao.getAllAlertsForFreezer(boxId)

            viewModelScope.launch(Dispatchers.Main) {
                alerts.value = retrievedAlerts
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val retrievedRecords = freezerDao.getRecordsForFreezer(boxId, MAX_RECORDS)

            viewModelScope.launch(Dispatchers.Main) {
                records.value = retrievedRecords
                if (retrievedRecords.isNotEmpty()) {
                    latestRecord.value = retrievedRecords[0]
                } else {
                    latestRecord.value = null
                }
            }
        }
    }

    /**
     * Get the freezer
     *
     * @return  Freezer live data
     */
    internal fun getFreezer(): LiveData<Freezer> = freezer

    /**
     * Get all alerts
     *
     * @return  Alerts live data
     */
    internal fun getAlerts(): LiveData<List<Alert>> = alerts

    /**
     * Get all records
     *
     * @return  Records live data
     */
    internal fun getRecords(): LiveData<List<FreezerRecord>> = records

    /**
     * Get the most recent record
     *
     * @return  Most recent freezer record
     */
    internal fun getLatestRecord(): LiveData<FreezerRecord?> = latestRecord

    /**
     * Get the name for editing
     *
     * @return  Editable name
     */
    internal fun getEditName(): LiveData<String> = editName

    /**
     * Get the temperature for editing
     *
     * @return  Editable temperature
     */
    internal fun getEditTemperature(): LiveData<Float> = editTemperature

    /**
     * Get the humidity for editing
     *
     * @return  Editable humidity
     */
    internal fun getEditHumidity(): LiveData<Float> = editHumidity

    /**
     * Get the sampling rate for editing
     *
     * @return  Editable sampling rate
     */
    internal fun getEditSamplingRate(): LiveData<Int> = editSamplingRate

    /**
     * Get the update flag
     *
     * @return  Update flag
     */
    internal fun getUpdateFlag(): UpdateFlag = updateFlag

    /**
     * Dismiss an alert
     *
     * @param alert The alert to dismiss
     */
    internal fun dismissAlert(alert: Alert) {
        viewModelScope.launch(Dispatchers.IO) {
            val newAlert = alert.copy(solved = true)
            freezerDao.updateAllAlerts(newAlert)

            // no need to update live data
        }
    }

    /**
     * Toggle whether the freezer is a favorite or not.
     */
    internal fun toggleFavorite() {
        freezer.value?.let { oldFreezer ->
            viewModelScope.launch(Dispatchers.IO) {
                val newFreezer = oldFreezer.copy(is_favorite = !oldFreezer.is_favorite)
                freezerDao.updateAllFreezers(newFreezer)

                viewModelScope.launch(Dispatchers.Main) {
                    freezer.value = newFreezer
                }
            }
        }
    }

    /**
     * Set the new name of the freezer
     *
     * @param newName   The new name of the freezer
     * @return  True if new name is valid, false if not
     */
    internal fun setName(newName: String): Boolean {
        return if (Freezer.validateName(newName)) {
            editName.value = newName
            freezer.value?.let { oldFreezer ->
                viewModelScope.launch(Dispatchers.IO) {
                    val newFreezer = oldFreezer.copy(name = newName)
                    freezerDao.updateAllFreezers(newFreezer)

                    viewModelScope.launch(Dispatchers.Main) {
                        updateFlag = UpdateFlag.UPDATE_NAME
                        freezer.value = newFreezer
                        updateFlag = UpdateFlag.NO_UPDATE
                    }
                }
            }
            true
        } else {
            freezer.value?.let {
                editName.value = it.name
            }
            false
        }
    }

    /**
     * Set the new temperature of the freezer
     *
     * @param newTemp   The new temperature of the freezer
     * @return  True if new temperature is valid, false if not
     */
    internal fun setTemperature(newTemp: Float): Boolean {
        val roundedTemp = (newTemp * 100).roundToInt() / 100f
        return if (Freezer.validateTemperature(roundedTemp)) {
            editTemperature.value = roundedTemp
            freezer.value?.let { oldFreezer ->
                viewModelScope.launch(Dispatchers.IO) {
                    val newFreezer = oldFreezer.copy(set_temperature = roundedTemp)
                    freezerDao.updateAllFreezers(newFreezer)

                    viewModelScope.launch(Dispatchers.Main) {
                        updateFlag = UpdateFlag.UPDATE_SETTINGS
                        freezer.value = newFreezer
                        updateFlag = UpdateFlag.NO_UPDATE
                    }
                }
            }
            true
        } else {
            freezer.value?.let {
                editTemperature.value = it.set_temperature
            }
            false
        }
    }

    /**
     * Change the temperature by [change] amount
     *
     * @param change    The amount to change the temperature by
     * @return  True if the new temperature is valid, false if not
     */
    internal fun changeTemperature(change: Float): Boolean {
        editTemperature.value?.let { temperature ->
            return setTemperature(temperature + change)
        }
        return false
    }

    /**
     * Set the new humidity of the freezer
     *
     * @param newHumidity   The new humidity of the freezer
     * @return  True if new humidity is valid, false if not
     */
    internal fun setHumidity(newHumidity: Float): Boolean {
        return if (Freezer.validateHumidity(newHumidity)) {
            val roundedHum = (newHumidity * 100).roundToInt() / 100f
            editHumidity.value = roundedHum
            freezer.value?.let { oldFreezer ->
                viewModelScope.launch(Dispatchers.IO) {
                    val newFreezer = oldFreezer.copy(set_humidity = roundedHum)
                    freezerDao.updateAllFreezers(newFreezer)

                    viewModelScope.launch(Dispatchers.Main) {
                        updateFlag = UpdateFlag.UPDATE_SETTINGS
                        freezer.value = newFreezer
                        updateFlag = UpdateFlag.NO_UPDATE
                    }
                }
            }
            true
        } else {
            freezer.value?.let {
                editHumidity.value = it.set_humidity
            }
            false
        }
    }

    /**
     * Change the humidity by [change] amount
     *
     * @param change    The amount to change the humidity by
     * @return  True if the new humidity is valid, false if not
     */
    internal fun changeHumidity(change: Float): Boolean {
        editHumidity.value?.let { humidity ->
            return setHumidity(humidity + change)
        }
        return false
    }

    /**
     * Set the new sampling rate of the freezer
     *
     * @param newRate   The new sampling rate of the freezer
     * @return  True if new rate is valid, false if not
     */
    internal fun setSamplingRate(newRate: Int): Boolean {
        return if (Freezer.validateSamplingRate(newRate)) {
            editSamplingRate.value = newRate
            freezer.value?.let { oldFreezer ->
                viewModelScope.launch(Dispatchers.IO) {
                    val newFreezer = oldFreezer.copy(sampling_rate = newRate)
                    freezerDao.updateAllFreezers(newFreezer)

                    viewModelScope.launch(Dispatchers.Main) {
                        updateFlag = UpdateFlag.UPDATE_REFRESH_RATE
                        freezer.value = newFreezer
                        updateFlag = UpdateFlag.NO_UPDATE
                    }
                }
            }
            true
        } else {
            freezer.value?.let {
                editSamplingRate.value = it.sampling_rate
            }
            false
        }
    }

    /**
     * Change the sampling rate by [change] amount
     *
     * @param change    The amount to change the rate by
     * @return  True if the new rate is valid, false if not
     */
    internal fun changeSamplingRate(change: Int): Boolean {
        editSamplingRate.value?.let { rate ->
            return setSamplingRate(rate + change)
        }
        return false
    }

    /**
     * Set the power for this freezer
     *
     * @param power True if on, false if off
     */
    internal fun setPower(power: Boolean) {
        freezer.value?.let { oldFreezer ->
            viewModelScope.launch(Dispatchers.IO) {
                val newFreezer = oldFreezer.copy(set_power_on = power)
                freezerDao.updateAllFreezers(newFreezer)

                viewModelScope.launch(Dispatchers.Main) {
                    updateFlag = UpdateFlag.UPDATE_SETTINGS
                    freezer.value = newFreezer
                    updateFlag = UpdateFlag.NO_UPDATE
                }
            }
        }
    }

    /**
     * Remove this freezer and all related records/alerts from the database
     *
     * @param callback  Callback method to call on completion
     */
    internal fun removeFreezer(callback: () -> Unit) {
        val freezerToRemove = freezer.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            freezerDao.deleteFreezer(freezerToRemove)
            freezerDao.deleteFreezerRelatedData(freezerToRemove)

            viewModelScope.launch(Dispatchers.Main) {
                callback()
            }
        }
    }
}