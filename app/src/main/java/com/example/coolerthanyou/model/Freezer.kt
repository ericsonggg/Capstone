package com.example.coolerthanyou.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database object representing a single freezer box.
 *
 * @property boxId  The unique ID of the box
 * @property name   The user-set name of the box
 * @property setting_temperature    The set optimal temperature
 * @property setting_humidity       The set optimal humidity
 */
@Entity
data class Freezer(
    @PrimaryKey val boxId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "setting_temperature") val setting_temperature: Double,
    @ColumnInfo(name = "setting_humidity") val setting_humidity: Double
)