package com.example.coolerthanyou.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database object representing a single freezer box.
 *
 * @property name   The user-set name of the box
 * @property set_temperature    The set optimal temperature
 * @property set_humidity       The set optimal humidity
 * @property set_power_on   Whether the box should actively cool or not. True if on, false if not.
 * @property bluetoothAddress   The Bluetooth address
 * @property is_favorite    Whether the box is set as a favorite or not.
 */
@Entity
data class Freezer(
    @ColumnInfo val name: String,
    @ColumnInfo val set_temperature: Float,
    @ColumnInfo val set_humidity: Float,
    @ColumnInfo val set_power_on: Boolean,
    @ColumnInfo val bluetoothAddress: String,
    @ColumnInfo val is_favorite: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var boxId: Long = 0
}