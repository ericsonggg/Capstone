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
)