package com.example.coolerthanyou.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coolerthanyou.model.Freezer

/**
 * Database object for all Room-based data sources (aka DAOs).
 */
@Database(entities = [Freezer::class], version = 1)
abstract class MainRoomDatabase : RoomDatabase() {

    /**
     * Get the [IFreezerRoomDataSource]
     *
     * @return  A [IFreezerRoomDataSource]
     */
    abstract fun getFreezerDao(): IFreezerRoomDataSource
}