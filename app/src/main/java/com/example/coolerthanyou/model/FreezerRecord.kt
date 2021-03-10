package com.example.coolerthanyou.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

/**
 * A single historical record for the state of a single Freezer
 *
 * @property boxId  ID of the freezer
 * @property time   Timestamp
 * @property temperature    Temperature in Celsius
 * @property humidity   Humidity in percentage
 * @property battery    Battery percentage
 */
@Entity(primaryKeys = ["boxId", "time"])
data class FreezerRecord(
    @ColumnInfo val boxId: Long,
    @ColumnInfo val time: Date,
    @ColumnInfo val temperature: Float,
    @ColumnInfo val humidity: Float,
    @ColumnInfo val battery: Int
) {

    companion object {
        const val TEMPERATURE_STABILITY = 3f
        const val HUMIDITY_STABILITY = 5f
        const val BATTERY_LOW = 15
    }


    /**
     * Validate the temperature and humidity.
     *
     * @param freezer   The freezer to validate against
     * @return  True if in +-[TEMPERATURE_STABILITY], false if not
     */
    fun validateTemperature(freezer: Freezer):Boolean {
        return temperature in (freezer.set_temperature - TEMPERATURE_STABILITY)..(freezer.set_temperature+ TEMPERATURE_STABILITY)
    }

    /**
     * Validate the humidity
     *
     * @param freezer   The freezer to validate against
     * @return  True if in +-[HUMIDITY_STABILITY], false if not
     */
    fun validateHumidity(freezer: Freezer) :Boolean {
        return humidity in (freezer.set_humidity - HUMIDITY_STABILITY)..(freezer.set_humidity+ HUMIDITY_STABILITY)
    }

    /**
     * Validate whether the battery is OK.
     *
     * @return  True if > [BATTERY_LOW], false if not
     */
    fun validateBattery():Boolean {
        return battery > BATTERY_LOW
    }
}