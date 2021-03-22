package com.example.coolerthanyou.mock

import android.util.Log
import com.example.coolerthanyou.datasource.IFreezerDao
import com.example.coolerthanyou.model.FreezerRecord
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.system.measureTimeMillis

/**
 * Class to run performance tests
 */
class StartupPerformanceTester @Inject constructor(private val mocker: Mocker) {

    companion object {
        const val TEST_REPEAT = 100

        const val MAX_DEVICES = 16
        const val SAMPLE_RATE_MIN = 13
        const val SAMPLE_RATE_MAX = 15
    }

    fun uploadMaxDataPerWeek(freezerDao: IFreezerDao) {
        GlobalScope.launch(Dispatchers.Default) {
            val allData: MutableMap<Int, List<Long>> = mutableMapOf()

            for (sampleRate in SAMPLE_RATE_MIN..SAMPLE_RATE_MAX step 2) {
                val dataForSampleRate: MutableList<Long> = mutableListOf()
                var executionTime: Long
                val numSamplesPerDay = (60 / sampleRate) * 24 * 7

                repeat(TEST_REPEAT) {
                    async(Dispatchers.IO) {
                        // pre-load new freezers
                        repeat(MAX_DEVICES) {
                            freezerDao.insertAllFreezers(mocker.mockFreezer())
                        }
                        val freezers = freezerDao.getAllFreezers().sortedByDescending { it.boxId }.take(MAX_DEVICES)

                        // pre-generate data
                        val records: MutableSet<Set<FreezerRecord>> = mutableSetOf()
                        freezers.forEach { freezer ->
                            val recordTime = Calendar.getInstance()
                            val deviceRecords: MutableSet<FreezerRecord> = mutableSetOf()
                            repeat(numSamplesPerDay) {
                                recordTime.add(Calendar.MINUTE, sampleRate)
                                deviceRecords.add(mocker.mockRecord(freezer.boxId, recordTime.time))
                            }
                            records.add(deviceRecords)
                        }

                        // actual test
                        executionTime = measureTimeMillis {
                            val coroutines: MutableSet<Deferred<Unit>> = mutableSetOf()
                            records.forEach { recordData ->
                                recordData.forEach { record ->
                                    coroutines.add(async {
                                        synchronized(freezerDao) { //synchronize here to prevent race conditions
                                            freezerDao.insertAndValidateAllFreezerRecords(record)
                                        }
                                    })
                                }
                            }

                            coroutines.forEach { it.await() } //wait for all to be done
                        }
                        Log.w("PerformanceTest", "uploadMaxDataPerDay: rate $sampleRate had $executionTime millis")
                        dataForSampleRate.add(executionTime)

                        // remove new freezers
                        for (freezer in freezers) {
                            freezerDao.deleteFreezerRelatedData(freezer)
                            freezerDao.deleteFreezer(freezer)
                        }
                    }.await()
                }

                allData[sampleRate] = dataForSampleRate
            }

            // print data
            val csv = StringBuilder()
            for (data in allData) {
                csv.append(data.key)
                csv.append("\n")
                data.value.forEach { csv.append("$it,") }
                csv.append("\n")
            }
            Log.w("PerformanceTest", "uploadMaxDataPerDay: csv: $csv")
        }
    }
}