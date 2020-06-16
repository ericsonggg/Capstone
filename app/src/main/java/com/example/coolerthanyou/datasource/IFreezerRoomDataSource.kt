package com.example.coolerthanyou.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.coolerthanyou.model.Freezer

/**
 * Room-specific implementation of [IFreezerDataSource].
 * AKA data access object (DAO).
 */
@Dao
interface IFreezerRoomDataSource : IFreezerDataSource {

    @Query("SELECT * FROM freezer")
    override fun getAllFreezers(): List<Freezer>

    @Query("SELECT * FROM freezer WHERE boxId=:id")
    override fun getFreezerById(id: String): Freezer

    @Query("SELECT * FROM freezer WHERE name=:name")
    override fun getFreezerByName(name: String): Freezer

    @Insert
    override fun addFreezer(freezer: Freezer)

    @Delete
    override fun deleteFreezer(freezer: Freezer)
}