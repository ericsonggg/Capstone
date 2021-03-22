package com.example.coolerthanyou.datasource

import androidx.room.*
import com.example.coolerthanyou.model.Alert
import com.example.coolerthanyou.model.Freezer
import com.example.coolerthanyou.model.FreezerRecord
import java.util.*

/**
 * Room-specific implementation of [IFreezerDao].
 */
@Dao
interface IFreezerRoomDao : IFreezerDao {

    @Query("SELECT * FROM freezer")
    override fun getAllFreezers(): List<Freezer>

    @Query("SELECT * FROM freezer WHERE boxId=:boxId")
    override fun getFreezerById(boxId: Long): Freezer?

    @Query("SELECT bluetoothAddress FROM freezer")
    override fun getAllBluetooth(): List<String>

    @Query("SELECT * FROM freezerRecord")
    override fun getAllFreezerRecords(): List<FreezerRecord>

    @Query("SELECT * FROM freezerRecord GROUP BY boxId ORDER BY time DESC")
    override fun getAllUniqueRecords(): List<FreezerRecord>

    @Query("SELECT * FROM freezerRecord WHERE boxId=:boxId ORDER BY time DESC LIMIT :numRecords")
    override fun getRecordsForFreezer(boxId: Long, numRecords: Int): List<FreezerRecord>

    @Query("SELECT * FROM alert")
    override fun getAllAlerts(): List<Alert>

    @Query("SELECT * FROM alert WHERE boxId=:boxId")
    override fun getAllAlertsForFreezer(boxId: Long): List<Alert>

    @Insert
    override fun insertAllFreezers(vararg freezers: Freezer)

    override fun insertAndValidateAllFreezerRecords(vararg freezerRecords: FreezerRecord) {
        freezerRecords.forEach { record ->
            getFreezerById(record.boxId)?.let { freezer ->
                if (record.validateTemperature(freezer)) {
                    val alert = getAllAlertsForFreezer(record.boxId).filter { !it.solved }.find { it.dataType == Alert.DATA_TYPE_TEMPERATURE }
                    if (alert == null) {
                        insertAllAlerts(Alert(record.boxId, record.time, Alert.TYPE_URGENT, Alert.DATA_TYPE_TEMPERATURE, "KEK"))
                    }
                }
                if (record.validateHumidity(freezer)) {
                    val alert = getAllAlertsForFreezer(record.boxId).filter { !it.solved }.find { it.dataType == Alert.DATA_TYPE_HUMIDITY }
                    if (alert == null) {
                        insertAllAlerts(Alert(record.boxId, record.time, Alert.TYPE_URGENT, Alert.DATA_TYPE_HUMIDITY, "KEK"))
                    }
                }
                if (record.validateBattery()) {
                    val alert = getAllAlertsForFreezer(record.boxId).filter { !it.solved }.find { it.dataType == Alert.DATA_TYPE_BATTERY }
                    if (alert == null) {
                        insertAllAlerts(Alert(record.boxId, record.time, Alert.TYPE_URGENT, Alert.DATA_TYPE_BATTERY, "KEK"))
                    }
                }
            }
            insertFreezerRecord(record)
        }
    }

    override fun insertAndValidateFreezerRecord(address: String, temp: Float, humid: Float, battery: Int) {
        getIDofFreezer(address).also { id ->
            insertAndValidateAllFreezerRecords(FreezerRecord(id, Calendar.getInstance().time, temp, humid, battery))
        }
    }

    @Insert
    override fun insertAllAlerts(vararg alerts: Alert)

    @Update
    override fun updateAllFreezers(vararg freezers: Freezer)

    override fun updateFreezerAndInsertIfNotExists(freezer: Freezer) {
        if (getFreezerById(freezer.boxId) == null) {
            insertAllFreezers(freezer)
        } else {
            updateAllFreezers(freezer)
        }
    }

    @Update
    override fun updateAllAlerts(vararg alerts: Alert)

    @Delete
    override fun deleteFreezer(freezer: Freezer)

    @Delete
    override fun deleteFreezerRecord(freezerRecord: FreezerRecord)

    override fun deleteFreezerRelatedData(freezer: Freezer) {
        deleteFreezerRecordsOfFreezer(freezer.boxId)
        deleteAlertsOfFreezer(freezer.boxId)
    }

    @Delete
    override fun deleteAlert(alert: Alert)

    @Query("SELECT boxId FROM freezer WHERE bluetoothAddress=:address")
    fun getIDofFreezer(address: String): Long

    @Insert
    fun insertFreezerRecord(record: FreezerRecord)

    @Query("DELETE FROM freezerRecord WHERE boxId=:boxId")
    fun deleteFreezerRecordsOfFreezer(boxId: Long)

    @Query("DELETE FROM alert WHERE boxId=:boxId")
    fun deleteAlertsOfFreezer(boxId: Long)
}