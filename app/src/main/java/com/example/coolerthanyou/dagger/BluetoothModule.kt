package com.example.coolerthanyou.dagger

import com.example.coolerthanyou.bluetooth.BluetoothManager
import com.example.coolerthanyou.log.ILogger
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
     * @param logger    The logger
     * @return  A bt manager instance
     */
    @Provides
    @Singleton
    fun providesBluetoothManager(logger: ILogger): BluetoothManager {
        return BluetoothManager(logger)
    }
}