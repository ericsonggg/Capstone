package com.example.coolerthanyou.ui

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * Chart axis formatter with dynamic dates based on chart resolution
 */
class DateValueFormatter : ValueFormatter() {
    companion object {
        const val MILLIS_PER_DAY = 86400000
        const val MILLIS_PER_HOUR = 3600000
        const val MILLIS_PER_MIN = 60000

        private const val FORMAT_DAY = "MMM d"
        private const val FORMAT_HOUR = "MMM d, h"
        private const val FORMAT_MIN = "HH:mm"
        private const val FORMAT_SEC = "mm:ss"
    }

    private val dateFormatter: SimpleDateFormat = SimpleDateFormat(FORMAT_HOUR)

    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        //calculate resolution and change format based on it
        if (axis.mEntryCount >= 2) {
            val diff = abs(axis.mEntries.last() - axis.mEntries[0]) / axis.mEntryCount
            when {
                diff <= MILLIS_PER_MIN -> {
                    dateFormatter.applyPattern(FORMAT_SEC)
                }
                diff <= MILLIS_PER_HOUR -> {
                    dateFormatter.applyPattern(FORMAT_MIN)
                }
                diff <= MILLIS_PER_DAY -> {
                    dateFormatter.applyPattern(FORMAT_HOUR)
                }
                else -> {
                    dateFormatter.applyPattern(FORMAT_DAY)
                }
            }
        }

        // format time (float) to string
        Calendar.getInstance().let {
            it.timeInMillis = value.toLong()
            return dateFormatter.format(it.time)
        }
    }
}