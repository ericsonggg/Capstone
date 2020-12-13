package com.example.coolerthanyou.dagger

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.coolerthanyou.datasource.IFreezerRoomDataSource
import com.example.coolerthanyou.datasource.MainRoomDatabase
import com.example.coolerthanyou.repository.IFreezerRepository
import com.example.coolerthanyou.repository.IFreezerRoomRepository
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
     * Provide the Room implementation of [IFreezerRoomDataSource]
     *
     * @param mainRoomDatabase  The main Room database, from [providesMainRoomDatabase]
     * @return  A FreezerDao to be used in repositories
     */
    @Provides
    @Singleton
    fun providesFreezerDao(mainRoomDatabase: MainRoomDatabase): IFreezerRoomDataSource {
        return mainRoomDatabase.getFreezerDao()
    }

    /**
     * Provide the Room implementation of [IFreezerRoomRepository]
     *
     * @param freezerDao    The Room DAO for Freezer, from [providesFreezerDao]
     * @return  A FreezerRepository to be used in view models
     */
    @Provides
    fun providesFreezerRepository(freezerDao: IFreezerRoomDataSource): IFreezerRepository {
        return IFreezerRoomRepository(freezerDao)
    }
}