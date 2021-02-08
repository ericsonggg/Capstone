package com.example.coolerthanyou.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database object representing a single freezer box.
 *
 * @property boxId  The unique ID of the box
 * @property name   The user-set name of the box
 * @property set_temperature    The set optimal temperature
 * @property set_humidity       The set optimal humidity
 * @property bluetoothAddress   The Bluetooth address
 */
@Entity
data class Freezer(
    @PrimaryKey val boxId: Long,
    @ColumnInfo val name: String,
    @ColumnInfo val set_temperature: Double,
    @ColumnInfo val set_humidity: Double,
    @ColumnInfo val bluetoothAddress: String
)