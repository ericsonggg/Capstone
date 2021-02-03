package com.example.coolerthanyou.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * A single historical record for the state of a single Freezer
 *
 * @property boxId  ID of the freezer
 * @property time   Timestamp
 * @property temperature    Temperature in Celsius
 * @property humidity   Humidity in percentage
 */
@Entity
data class FreezerRecord(
    @PrimaryKey val boxId: String,
    @ColumnInfo val time: Date,
    @ColumnInfo val temperature: Double,
    @ColumnInfo val humidity: Double
)