package com.example.coolerthanyou.dagger

import com.example.coolerthanyou.bluetooth.BluetoothManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * [Module] for all Bluetooth related classes
 */
@Module
class BluetoothModule {

    /**
     * Provide a singleton bt manager
     *
     * @return  A bt manager instance
     */
    @Provides
    @Singleton
    fun providesBluetoothManager(): BluetoothManager {
        return BluetoothManager()
    }
}