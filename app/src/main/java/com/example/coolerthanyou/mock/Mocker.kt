package com.example.coolerthanyou.mock

import com.example.coolerthanyou.model.Alert
import com.example.coolerthanyou.model.Freezer
import com.example.coolerthanyou.model.FreezerRecord
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Returns data for data mocking purposes
 */
class Mocker {

    companion object {
        private val namePrimary: List<String> = listOf(
            "Malaria", "Vaccine", "Drugs", "Allergy", "Generic", "Liquid"
        )
        private val nameSecondary: List<String> = listOf(
            "Alpha", "Beta", "Delta", "Bckp", "Main", "Post", "Home"
        )
        private const val nameSuffixEnd: Int = 9

        private const val alphanumeric: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

        private const val temperatureLower: Float = 2f
        private const val temperatureHigher: Float = 25f

        private const val humidityLower: Float = 0f
        private const val humidityHigher: Float = 40f

        private const val samplingRateLower: Int = 5
        private const val samplingRateHigher: Int = 900 //15 mins

        private const val timeMinimum: Int = 10
        private const val timeMaximum: Int = 1440
    }

    enum class DataPattern {
        RANDOM, COSINE, LINEAR
    }

    private val random = Random.Default

    /**
     * Generate a random freezer with the provided (or random) details
     *
     * @param name  The name, or null for random
     * @param address   The address, or null for random
     * @param temperature   The temperature, or null for random
     * @param humidity  The humidity, or null for random
     * @param samplingRate  The sampling rate, or null for random
     * @param powerOn   Whether the power is on, or null for random
     * @param isFavorite    Whether the freezer is a favorite, or null for random
     * @return  The freezer instance
     */
    fun mockFreezer(
        name: String? = null,
        address: String? = null,
        temperature: Float? = null,
        humidity: Float? = null,
        samplingRate: Int? = null,
        powerOn: Boolean? = null,
        isFavorite: Boolean? = null
    ): Freezer {
        return Freezer(
            if (name.isNullOrBlank()) generateFreezerName() else name,
            if (address.isNullOrBlank()) generateBluetoothAddress() else address,
            temperature ?: generateTemperature(),
            humidity ?: generateHumidity(),
            samplingRate ?: generateSamplingRate(),
            powerOn ?: random.nextBoolean(),
            isFavorite ?: random.nextBoolean()
        )
    }

    /**
     * Generate [num] random freezers
     *
     * @param num   The number of random freezers to generate
     * @return  A list of random freezers
     */
    fun mockFreezers(num: Int): List<Freezer> {
        val freezers: MutableList<Freezer> = mutableListOf()
        for (i in 1..num) {
            freezers.add(mockFreezer())
        }
        return freezers
    }

    /**
     * Generate a random record for the freezer with [boxId] at [time]
     *
     * @param boxId The freezer's boxId that will have records
     * @param time  The time of the record
     * @return  A random record
     */
    fun mockRecord(boxId: Long, time: Date = Calendar.getInstance().time): FreezerRecord {
        return FreezerRecord(
            boxId,
            time,
            generateTemperature(),
            generateHumidity(),
            generateBattery()
        )
    }

    /**
     * Generate random records for every freezer with an id
     *
     * @param ids   The freezer's boxId that will have records
     * @param maxRecords    The maximum number of records that could be generated
     * @return  A list of random records
     */
    fun mockRecords(ids: List<Long>, maxRecords: Int = 10): List<FreezerRecord> {
        val records: MutableList<FreezerRecord> = mutableListOf()
        val calendar: Calendar = Calendar.getInstance().apply {
            if (get(Calendar.DAY_OF_MONTH) == 1) {
                if (get(Calendar.MONTH) == 1) {
                    set(Calendar.YEAR, get(Calendar.YEAR) - 1) // reset to previous year
                } else {
                    set(Calendar.MONTH, get(Calendar.MONTH) - 1) // reset to previous month
                }
            } else {
                set(Calendar.DAY_OF_MONTH, 1) // reset to the first
            }
        }

        ids.forEach { id ->
            val numRecords = random.nextInt(1, maxRecords + 1) //1 to 10 records
            for (i in 1..numRecords) {
                calendar.add(Calendar.MINUTE, random.nextInt(timeMinimum, timeMaximum))
                records.add(mockRecord(id, calendar.time))
            }
        }
        return records
    }

    /**
     * Generate a random [Alert] for the freezer with [boxId]
     *
     * @param boxId The freezer's boxId that will have the alert
     * @param time  The time of the alert. Default is the current time.
     * @return  A random alert
     */
    fun mockAlert(boxId: Long, time: Date = Calendar.getInstance().time): Alert {
        val type = when (random.nextInt(1, 3)) {
            1 -> Alert.TYPE_URGENT
            2 -> Alert.TYPE_WARNING
            else -> Alert.TYPE_NONE // should not happen
        }
        return Alert(
            boxId,
            time,
            type,
            if (type == Alert.TYPE_URGENT) "This is an urgent alert for freezer #$boxId" else "This is a warning for freezer #$boxId"
        )
    }

    /**
     * Generate random alerts for SOME of the ids in the list, with staggered times.
     *
     * @param ids   The list of freezer boxIds that may or may not get an alert.
     * @param maxAlerts The maximum alerts that could possibly be generated
     * @return A list of random alerts.
     */
    fun mockAlerts(ids: List<Long>, maxAlerts: Int = 3): List<Alert> {
        val alerts: MutableList<Alert> = mutableListOf()
        val calendar: Calendar = Calendar.getInstance().apply {
            if (get(Calendar.DAY_OF_MONTH) == 1) {
                if (get(Calendar.MONTH) == 1) {
                    set(Calendar.YEAR, get(Calendar.YEAR) - 1) // reset to previous year
                } else {
                    set(Calendar.MONTH, get(Calendar.MONTH) - 1) // reset to previous month
                }
            } else {
                set(Calendar.DAY_OF_MONTH, 1) // reset to the first
            }
        }

        ids.forEach { id ->
            if (random.nextBoolean()) {
                val numAlerts = random.nextInt(1, maxAlerts + 1) //1 to 3 alerts
                for (i in 1..numAlerts) {
                    //randomly increment calendar
                    calendar.add(Calendar.MINUTE, random.nextInt(timeMinimum, timeMaximum))
                    alerts.add(mockAlert(id, calendar.time))
                }
            }
        }
        return alerts
    }

    /**
     * Generate a random freezer name with 15 chars or less
     *
     * @return  A random freezer name
     */
    private fun generateFreezerName(): String {
        return namePrimary[random.nextInt(namePrimary.size)] + " " +
                nameSecondary[random.nextInt(nameSecondary.size)] + " " +
                (random.nextInt(nameSuffixEnd) + 1)
    }

    /**
     * Generate a random 6 byte bluetooth address with '-' separators
     *
     * @return  A random bluetooth address
     */
    private fun generateBluetoothAddress(): String {
        var address = alphanumeric[random.nextInt(alphanumeric.length)] + "" +
                alphanumeric[random.nextInt(alphanumeric.length)]
        for (i in 1..5) {
            address += "-" + alphanumeric[random.nextInt(alphanumeric.length)] + alphanumeric[random.nextInt(alphanumeric.length)]
        }
        return address
    }

    /**
     * Generate a random temperature from [temperatureLower] to [temperatureHigher]
     *
     * @return  A random temperature
     */
    private fun generateTemperature(): Float {
        val temp = random.nextFloat() * (temperatureHigher - temperatureLower) + temperatureLower
        return (temp * 100).roundToInt() / 100f //round to 2 decimal places
    }

    /**
     * Generate a random humidity from [humidityLower] to [humidityHigher]
     *
     * @return  A random humidity
     */
    private fun generateHumidity(): Float {
        val humid = random.nextFloat() * (humidityHigher - humidityLower) + humidityLower
        return (humid * 100).roundToInt() / 100f //round to 2 decimal places
    }

    /**
     * Generate a random sampling rate from [samplingRateLower] to [samplingRateHigher], in seconds
     *
     * @return  A random sampling rate
     */
    private fun generateSamplingRate(): Int {
        return random.nextInt(samplingRateLower, samplingRateHigher + 1)
    }

    /**
     * Generate a random battery percentage from 0 to 100
     *
     * @return  A random battery percentage
     */
    private fun generateBattery(): Int {
        return random.nextInt(0, 101)
    }
}