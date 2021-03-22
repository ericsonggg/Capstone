package com.example.coolerthanyou.model

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Converts common data types into Room compatible types.
 */
class Converters {

    companion object {
        const val ROOM_DATE_FORMAT = "yyyy-MM-dd HH-mm-ss-SSS"
    }

    private val dateFormatter: SimpleDateFormat

    init {
        dateFormatter = SimpleDateFormat(ROOM_DATE_FORMAT)
    }

    /**
     * Converts strings into the equivalent date.
     * String must be in the format:
     * YYYY MM DD HH mm SS
     *
     * @param value String representation of date
     * @return  Date representation
     */
    @TypeConverter
    fun dateFromString(value: String?): Date? {
        return value?.let {
            dateFormatter.parse(it)
        }
    }

    /**
     * Converts dates into the equivalent string.
     * String is in format:
     * YYYY MM DD HH mm SS
     *
     * @param date  Valid date
     * @return  String representation
     */
    @TypeConverter
    fun stringFromDate(date: Date): String {
        return dateFormatter.format(date)
    }
}