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
 * @property sampling_rate  The set sampling rate (in minutes). Default is [DEFAULT_SAMPLING_RATE]
 * @property set_power_on   Whether the box should actively cool or not. True if on, false if not. Default is true.
 * @property is_favorite    Whether the box is set as a favorite or not. Default is false
 */
@Entity
data class Freezer(
    @ColumnInfo val name: String,
    @ColumnInfo val bluetoothAddress: String,
    @ColumnInfo val set_temperature: Float = DEFAULT_TEMPERATURE,
    @ColumnInfo val set_humidity: Float = DEFAULT_HUMIDITY,
    @ColumnInfo val sampling_rate: Int = DEFAULT_SAMPLING_RATE,
    @ColumnInfo val set_power_on: Boolean = true,
    @ColumnInfo val is_favorite: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var boxId: Long = 0

    companion object {
        const val DEFAULT_TEMPERATURE: Float = 12f
        const val DEFAULT_HUMIDITY: Float = 20f
        const val DEFAULT_SAMPLING_RATE = 60
    }
}