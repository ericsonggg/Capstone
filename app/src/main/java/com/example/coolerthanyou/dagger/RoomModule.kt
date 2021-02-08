package com.example.coolerthanyou.dagger

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.coolerthanyou.datasource.IFreezerDao
import com.example.coolerthanyou.datasource.MainRoomDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * [Module] for all Room related classes
 */
@Module
class RoomModule {

    /**
     * Provide a singleton database for Room
     *
     * @param appContext    The context for the [Application]
     * @return  A singleton database for Room
     */
    @Provides
    @Singleton
    fun providesMainRoomDatabase(appContext: Context): MainRoomDatabase {
        return Room.databaseBuilder(appContext, MainRoomDatabase::class.java, "main-room-database").build()
    }

    /**
     * Provide the Room implementation of [IFreezerDao]
     *
     * @param mainRoomDatabase  The main Room database, from [providesMainRoomDatabase]
     * @return  A [IFreezerDao] to be used
     */
    @Provides
    @Singleton
    fun providesFreezerDao(mainRoomDatabase: MainRoomDatabase): IFreezerDao {
        return mainRoomDatabase.getFreezerDao()
    }
}