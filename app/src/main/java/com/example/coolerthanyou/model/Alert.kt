package com.example.coolerthanyou.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

/**
 * An alert that occurred for a freezer.
 *
 * @property boxId  The id of the freezer
 * @property time   The time that it occurred
 * @property type   The type of the alert
 * @property message    The message for the alert
 * @property solved Whether this alert was solved (i.e. dismissed) or not. True if solved, false if not.
 */
@Entity(primaryKeys = ["boxId", "time", "type", "dataType"])
data class Alert(
    @ColumnInfo val boxId: Long,
    @ColumnInfo val time: Date,
    @ColumnInfo val type: Int,
    @ColumnInfo val dataType: Int,
    @ColumnInfo val message: String,
    @ColumnInfo val solved: Boolean = false
) {
    companion object {
        const val TYPE_NONE = 0 // for other classes using Alert
        const val TYPE_URGENT = 1
        const val TYPE_WARNING = 2

        const val DATA_TYPE_TEMPERATURE = 50
        const val DATA_TYPE_HUMIDITY = 51
        const val DATA_TYPE_BATTERY = 52
    }
}