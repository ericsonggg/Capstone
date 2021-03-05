package com.example.coolerthanyou.ui.splash

import androidx.lifecycle.viewModelScope
import com.example.coolerthanyou.AppConfiguration
import com.example.coolerthanyou.BaseViewModel
import com.example.coolerthanyou.datasource.IFreezerDao
import com.example.coolerthanyou.mock.Mocker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model for [SplashViewModel]
 */
class SplashViewModel @Inject constructor(
    private val appConfiguration: AppConfiguration,
    private val mocker: Mocker,
    private val freezerDao: IFreezerDao
) : BaseViewModel() {

    companion object {
        private const val TASK_STARTUP_MOCKS = "TASK_STARTUP_MOCKS"
    }

    private val tasks: MutableSet<String> = mutableSetOf()

    /**
     * Add an ongoing task to track
     *
     * @param id    ID of the ongoing task
     */
    internal fun addTask(id: String) {
        tasks.add(id)
    }

    /**
     * Remove a finished task
     *
     * @param id    ID of the finished task
     */
    internal fun removeTask(id: String) {
        tasks.remove(id)
    }

    /**
     * Check whether the activity can be finished
     *
     * @return  True if ready, false otherwise
     */
    internal fun isFinished(): Boolean = tasks.isEmpty()

    /**
     * Run startup data mocks
     */
    internal fun runStartupMocks() {
        if (appConfiguration.runStartupMocks) {
            addTask(TASK_STARTUP_MOCKS)

            viewModelScope.launch(Dispatchers.IO) {
                if (appConfiguration.runStartupClearDatabase) {
                    freezerDao.getAllFreezers().forEach {
                        freezerDao.deleteFreezer(it)
                    }
                    freezerDao.getAllFreezerRecords().forEach {
                        freezerDao.deleteFreezerRecord(it)
                    }
                    freezerDao.getAllAlerts().forEach {
                        freezerDao.deleteAlert(it)
                    }
                }

                if (appConfiguration.runStartupAddFreezers) {
                    freezerDao.insertAllFreezers(*mocker.mockFreezers(8).toTypedArray())
                }
                val ids = freezerDao.getAllFreezers().map { it.boxId }
                if (appConfiguration.runStartupAddRecords) {
                    freezerDao.insertAllFreezerRecords(*mocker.mockRecords(ids).toTypedArray())
                }
                if (appConfiguration.runStartupAddAlerts) {
                    freezerDao.insertAllAlerts(*mocker.mockAlerts(ids).toTypedArray())
                }

                removeTask(TASK_STARTUP_MOCKS)
            }
        }
    }
}