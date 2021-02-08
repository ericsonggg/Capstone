package com.example.coolerthanyou.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.coolerthanyou.model.Converters
import com.example.coolerthanyou.model.Freezer

/**
 * Database object for all Room-based data sources (aka DAOs).
 */
@Database(entities = [Freezer::class], version = 1)
@TypeConverters(Converters::class)
abstract class MainRoomDatabase : RoomDatabase() {

    /**
     * Get the [IFreezerRoomDao]
     *
     * @return  A [IFreezerRoomDao]
     */
    abstract fun getFreezerDao(): IFreezerRoomDao
}