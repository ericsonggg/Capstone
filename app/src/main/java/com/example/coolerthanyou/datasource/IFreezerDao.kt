package com.example.coolerthanyou.datasource

import com.example.coolerthanyou.model.Alert
import com.example.coolerthanyou.model.Freezer
import com.example.coolerthanyou.model.FreezerRecord

/**
 * Data source (DAO) for the [Freezer] and [FreezerRecord] models.
 */
interface IFreezerDao {

    /**
     * Get all freezers in the database
     *
     * @return  [List] of [Freezer]
     */
    fun getAllFreezers(): List<Freezer>

    /**
     * Get a single freezer by its boxId
     *
     * @param boxId The boxId of the freezer
     * @return  A [Freezer] with the boxId matching [boxId]
     */
    fun getFreezerById(boxId: Long): Freezer?

    /**
     * Get the Bluetooth addresses for all [Freezer]s in the database
     *
     * @return  A list of all [Freezer]s' Bluetooth addresses.
     */
    fun getAllBluetooth(): List<String>

    /**
     * Get all freezer records in the database
     *
     * @return  A list of all freezer records
     */
    fun getAllFreezerRecords(): List<FreezerRecord>

    /**
     * Get one (or none) records per freezer
     *
     * @return  A list of unique records
     */
    fun getAllUniqueRecords(): List<FreezerRecord>

    /**
     * Get max [numRecords] [FreezerRecord]s for the freezer with boxId
     *
     * @param boxId     The boxId of the freezer
     * @param numRecords    The max number of records to return
     * @return  A list of records for the Freezer, with
     */
    fun getRecordsForFreezer(boxId: Long, numRecords: Int): List<FreezerRecord>

    /**
     * Get all alerts in the database
     *
     * @return  A list of all [Alert]s
     */
    fun getAllAlerts(): List<Alert>

    /**
     * Get all alerts belonging to the Freezer with boxID
     *
     * @param boxId The freezer's boxId
     * @return  All alerts belonging to it
     */
    fun getAllAlertsForFreezer(boxId: Long): List<Alert>

    /**
     * Add freezers to the database
     *
     * @param freezers   The [Freezer] to add
     */
    fun insertAllFreezers(vararg freezers: Freezer)

    /**
     * Insert freezer records to the database, and trigger alerts if invalid
     *
     * @param freezerRecords The [FreezerRecord]s to add
     */
    fun insertAndValidateAllFreezerRecords(vararg freezerRecords: FreezerRecord)

    /**
     * Insert a freezer record based on its data, and trigger an alert if invalid
     *
     * @param address   The BLE address of the freezer
     * @param temp      The current temperature
     * @param humid     The current humidity
     * @param battery   The current battery level
     */
    fun insertAndValidateFreezerRecord(address: String, temp: Float, humid: Float, battery: Int)

    /**
     * Insert alerts to the database
     *
     * @param alerts The [Alert]s to add
     */
    fun insertAllAlerts(vararg alerts: Alert)

    /**
     * Update freezers in the database
     *
     * @param freezers  The [Freezer]s to update
     */
    fun updateAllFreezers(vararg freezers: Freezer)

    /**
     * Update the freezer, inserting it if an entry does not already exist
     *
     * @param freezer   The freezer to update
     */
    fun updateFreezerAndInsertIfNotExists(freezer: Freezer)

    /**
     * Update alerts in the database
     *
     * @param alerts    The [Alert]s to update
     */
    fun updateAllAlerts(vararg alerts: Alert)

    /**
     * Delete a freezer from the database
     *
     * @param freezer   The [Freezer] to delete
     */
    fun deleteFreezer(freezer: Freezer)

    /**
     * Delete all freezer related data, including records and alerts
     *
     * @param freezer   The [Freezer]'s data to delete
     */
    fun deleteFreezerRelatedData(freezer: Freezer)

    /**
     * Delete a freezer record from the database
     *
     * @param freezerRecord The [FreezerRecord] to delete
     */
    fun deleteFreezerRecord(freezerRecord: FreezerRecord)

    /**
     * Delete an alert from the database
     *
     * @param alert The [Alert] to delete
     */
    fun deleteAlert(alert: Alert)
}