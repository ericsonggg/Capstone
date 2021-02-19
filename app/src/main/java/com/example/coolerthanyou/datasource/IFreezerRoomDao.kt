package com.example.coolerthanyou.datasource

import androidx.room.*
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

    @Query("SELECT * FROM freezer WHERE name=:name")
    override fun getFreezerByName(name: String): Freezer

    @Query("SELECT * FROM freezer WHERE bluetoothAddress=:address")
    override fun getFreezerByAddress(address: String): Freezer

    @Query("SELECT bluetoothAddress FROM freezer")
    override fun getAllBluetooth(): List<String>

    @Insert
    override fun insertAllFreezers(vararg freezers: Freezer)

    @Update
    override fun updateAllFreezers(vararg freezers: Freezer)

    @Delete
    override fun deleteFreezer(freezer: Freezer)

    @Insert
    override fun insertFreezerRecord(vararg freezerRecord: FreezerRecord)

    override fun insertFreezerRecord(address: String, temp: Float, humid: Float, battery: Int) {
        getIDofFreezer(address).also { id ->
            insertFreezerRecord(FreezerRecord(id, Calendar.getInstance().time, temp, humid, battery))
        }
    }

    @Query("SELECT boxId FROM freezer WHERE bluetoothAddress=:address")
    fun getIDofFreezer(address: String): Long
}