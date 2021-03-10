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

    @Insert
    override fun insertAllFreezerRecords(vararg freezerRecords: FreezerRecord)

    override fun insertFreezerRecord(address: String, temp: Float, humid: Float, battery: Int) {
        getIDofFreezer(address).also { id ->
            insertAllFreezerRecords(FreezerRecord(id, Calendar.getInstance().time, temp, humid, battery))
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

    @Query("DELETE FROM freezerRecord WHERE boxId=:boxId")
    fun deleteFreezerRecordsOfFreezer(boxId: Long)

    @Query("DELETE FROM alert WHERE boxId=:boxId")
    fun deleteAlertsOfFreezer(boxId: Long)
}