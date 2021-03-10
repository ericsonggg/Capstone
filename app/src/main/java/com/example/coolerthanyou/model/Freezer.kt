package com.example.coolerthanyou.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.coolerthanyou.model.Freezer.Companion.DEFAULT_HUMIDITY
import com.example.coolerthanyou.model.Freezer.Companion.DEFAULT_SAMPLING_RATE
import com.example.coolerthanyou.model.Freezer.Companion.DEFAULT_TEMPERATURE

/**
 * Database object representing a single freezer box.
 *
 * @property name   The user-set name of the box
 * @property bluetoothAddress   The Bluetooth address
 * @property set_temperature    The set optimal temperature. Default is [DEFAULT_TEMPERATURE]
 * @property set_humidity       The set optimal humidity. Default is [DEFAULT_HUMIDITY]
 * @property sampling_rate  The set sampling rate (in seconds). Default is [DEFAULT_SAMPLING_RATE]
 * @property set_power_on   Whether the box should actively cool or not. True if on, false if not. Default is true.
 * @property is_favorite    Whether the box is set as a favorite or not. Default is false
 * @property boxId  The unique id of this freezer
 */
@Entity
data class Freezer(
    @ColumnInfo val name: String,
    @ColumnInfo val bluetoothAddress: String,
    @ColumnInfo val set_temperature: Float = DEFAULT_TEMPERATURE,
    @ColumnInfo val set_humidity: Float = DEFAULT_HUMIDITY,
    @ColumnInfo val sampling_rate: Int = DEFAULT_SAMPLING_RATE,
    @ColumnInfo val set_power_on: Boolean = true,
    @ColumnInfo val is_favorite: Boolean = false,
    @PrimaryKey(autoGenerate = true) val boxId: Long = 0
) {
    companion object {
        const val DEFAULT_TEMPERATURE: Float = 12f
        const val DEFAULT_HUMIDITY: Float = 20f
        const val DEFAULT_SAMPLING_RATE = 60

        const val MAX_NAME_LENGTH: Int = 15
        const val MAX_ADDRESS_LENGTH: Int = 17
        const val MAX_TEMPERATURE: Float = 25f
        const val MIN_TEMPERATURE: Float = 0f
        const val MAX_HUMIDITY: Float = 40f
        const val MIN_HUMIDITY: Float = 0f
        const val MAX_SAMPLING_RATE: Int = 15
        const val MIN_SAMPLING_RATE: Int = 1

        /**
         * Check whether [name] is valid for a freezer
         *
         * @param name  The name to validate
         * @return  True if valid, false if not
         */
        fun validateName(name: String): Boolean {
            return (name.isNotBlank() && name.length in 1..MAX_NAME_LENGTH)
        }

        /**
         * Check whether [address] is valid for a freezer
         *
         * @param address  The address to validate
         * @return  True if valid, false if not
         */
        fun validateAddress(address: String): Boolean {
            return address.isNotBlank() && address.length == MAX_ADDRESS_LENGTH
        }

        /**
         * Check whether [temperature] is valid for a freezer
         *
         * @param temperature  The temperature to validate
         * @return  True if valid, false if not
         */
        fun validateTemperature(temperature: Float): Boolean {
            return (temperature in MIN_TEMPERATURE..MAX_TEMPERATURE)
        }

        /**
         * Check whether [humidity] is valid for a freezer
         *
         * @param humidity  The humidity to validate
         * @return  True if valid, false if not
         */
        fun validateHumidity(humidity: Float): Boolean {
            return (humidity in MIN_HUMIDITY..MAX_HUMIDITY)
        }

        /**
         * Check whether [rate] is valid for a freezer
         *
         * @param rate  The rate to validate
         * @return  True if valid, false if not
         */
        fun validateSamplingRate(rate: Int): Boolean {
            return (rate in MIN_SAMPLING_RATE..MAX_SAMPLING_RATE)
        }
    }

    init {
        if (!validateName(name)) {
            throw IllegalArgumentException("A freezer's name can be max $MAX_NAME_LENGTH characters. Freezer: $this")
        }
        if (!validateAddress(bluetoothAddress)) {
            throw IllegalArgumentException("A freezer's bluetooth address must be of the form ??-??-??-??-??-??. Freezer: $this")
        }
        if (!validateTemperature(set_temperature)) {
            throw IllegalArgumentException("A freezer's temperature must be from [$MIN_TEMPERATURE, $MAX_TEMPERATURE] degrees. Freezer: $this")
        }
        if (!validateHumidity(set_humidity)) {
            throw IllegalArgumentException("A freezer's humidity must be from [$MIN_HUMIDITY, $MAX_HUMIDITY] percent. Freezer: $this")
        }
        if (!validateSamplingRate(sampling_rate)) {
            throw IllegalArgumentException("A freezer's sampling rate must be from [$MIN_SAMPLING_RATE, $MAX_SAMPLING_RATE] seconds. Freezer: $this")
        }
    }

}