package com.example.coolerthanyou.datasource

import androidx.room.*
import com.example.coolerthanyou.model.Freezer

/**
 * Room-specific implementation of [IFreezerDao].
 */
@Dao
interface IFreezerRoomDao : IFreezerDao {

    @Query("SELECT * FROM freezer")
    override fun getAll(): List<Freezer>

    @Query("SELECT * FROM freezer WHERE boxId=:id")
    override fun getById(id: String): Freezer

    @Query("SELECT * FROM freezer WHERE name=:name")
    override fun getByName(name: String): Freezer

    @Query("SELECT bluetoothAddress FROM freezer")
    override fun getAllBluetooth(): List<String>

    @Insert
    override fun insertAll(vararg freezers: Freezer)

    @Update
    override fun updateAll(vararg freezers: Freezer)

    @Delete
    override fun delete(freezer: Freezer)
}