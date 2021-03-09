package com.example.coolerthanyou.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coolerthanyou.datasource.IFreezerDao
import com.example.coolerthanyou.log.ILogger
import com.example.coolerthanyou.model.Alert
import com.example.coolerthanyou.model.Freezer
import com.example.coolerthanyou.model.FreezerRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model for [HomeFragment] and related classes
 *
 * @property freezerDao The DAO for Freezer and related structs
 */
class HomeViewModel @Inject constructor(protected val freezerDao: IFreezerDao) : ViewModel() {

    @Inject
    protected lateinit var logger: ILogger
    private val logTag: String = "HomeViewModel"

    private val freezers: MutableLiveData<MutableList<Freezer>> = MutableLiveData()
    private val uniqueRecords: MutableLiveData<MutableList<FreezerRecord>> = MutableLiveData()
    private val urgents: MutableLiveData<MutableSet<Alert>> = MutableLiveData()
    private val warnings: MutableLiveData<MutableSet<Alert>> = MutableLiveData()

    /**
     * Update all data held by this view model from the data base
     */
    internal fun updateData() {
        viewModelScope.launch(Dispatchers.IO) {
            val freezerList = freezerDao.getAllFreezers()

            viewModelScope.launch(Dispatchers.Main) {
                freezers.value = freezerList.toMutableList()
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val uniqueRecordsList = freezerDao.getAllUniqueRecords()

            viewModelScope.launch(Dispatchers.Main) {
                uniqueRecords.value = uniqueRecordsList.toMutableList()
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val urgentsList: MutableSet<Alert> = mutableSetOf()
            val warningsList: MutableSet<Alert> = mutableSetOf()

            freezerDao.getAllAlerts().forEach { alert ->
                when (alert.type) {
                    Alert.TYPE_URGENT -> {
                        urgentsList.add(alert)
                    }
                    Alert.TYPE_WARNING -> {
                        warningsList.add(alert)
                    }
                    else -> {
                        logger.w(logTag, "Retrieved invalid alert: $alert")
                    }
                }
            }

            viewModelScope.launch(Dispatchers.Main) {
                urgents.value = urgentsList
                warnings.value = warningsList
            }
        }
    }

    /**
     * Get all freezers
     *
     * @return  Live data for all freezers
     */
    internal fun getFreezers(): LiveData<MutableList<Freezer>> = freezers

    /**
     * Get all most recent unique records per freezer
     *
     * @return  Live data for all recent unique records
     */
    internal fun getUniqueRecords(): LiveData<MutableList<FreezerRecord>> = uniqueRecords

    /**
     * Get all urgent alerts
     *
     * @return  Live data for all urgents
     */
    internal fun getUrgents(): LiveData<MutableSet<Alert>> = urgents

    /**
     * Get all warning alerts
     *
     * @return  Live data for all warnings
     */
    internal fun getWarnings(): LiveData<MutableSet<Alert>> = warnings
}