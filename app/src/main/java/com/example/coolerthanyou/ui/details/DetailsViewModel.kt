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

/**
 * ViewModel for [DetailsFragment] and related classes.
 * [getFreezerInfo] should be called first to initialize data
 */
class DetailsViewModel @Inject constructor(protected val freezerDao: IFreezerDao) : ViewModel() {

    companion object {
        const val MAX_RECORDS: Int = 50
    }

    private val freezer: MutableLiveData<Freezer> = MutableLiveData()
    private val alerts: MutableLiveData<List<Alert>> = MutableLiveData()
    private val records: MutableLiveData<List<FreezerRecord>> = MutableLiveData()
    private val latestRecord: MutableLiveData<FreezerRecord?> = MutableLiveData()

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
}