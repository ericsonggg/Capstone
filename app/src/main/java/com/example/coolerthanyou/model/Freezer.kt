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
        const val MAX_SAMPLING_RATE: Int = 900
    }

    init {
        if (name.length > MAX_NAME_LENGTH) {
            throw IllegalArgumentException("A freezer's name can be max 15 characters. Freezer: $this")
        }
        if (bluetoothAddress.length != MAX_ADDRESS_LENGTH) {
            throw IllegalArgumentException("A freezer's bluetooth address must be of the form ??-??-??-??-??-??. Freezer: $this")
        }
        if (sampling_rate < 0 || sampling_rate > MAX_SAMPLING_RATE) {
            throw IllegalArgumentException("A freezer's sampling rate must be from [0, $MAX_SAMPLING_RATE] seconds. Freezer: $this")
        }
    }
}