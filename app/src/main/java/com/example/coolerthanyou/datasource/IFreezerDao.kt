package com.example.coolerthanyou.datasource

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
     * Get a single freezer by its name
     *
     * @param name  The name of the freezer
     * @return  A [Freezer] with the name matching [name]
     */
    fun getFreezerByName(name: String): Freezer

    /**
     * Get a single freezer by its bluetooth address
     *
     * @param address   The bluetooth address
     * @return  A [Freezer] with the bluetooth address matching [address]
     */
    fun getFreezerByAddress(address: String): Freezer

    /**
     * Get the Bluetooth addresses for all [Freezer]s in the database
     *
     * @return  A list of all [Freezer]s' Bluetooth addresses.
     */
    fun getAllBluetooth(): List<String>

    /**
     * Add freezers to the database
     *
     * @param freezer   The [Freezer] to add
     */
    fun insertAllFreezers(vararg freezers: Freezer)

    /**
     * Update freezers in the database
     *
     * @param freezers  The [Freezer]s to update
     */
    fun updateAllFreezers(vararg freezers: Freezer)

    /**
     * Delete a freezer from the database
     *
     * @param freezer   The [Freezer] to delete
     */
    fun deleteFreezer(freezer: Freezer)

    /**
     * Insert freezer records to the database
     *
     * @param freezerRecord The [FreezerRecord]s to add
     */
    fun insertFreezerRecord(vararg freezerRecord: FreezerRecord)

    /**
     * Insert a freezer record based on its data
     *
     * @param address   The BLE address of the freezer
     * @param temp      The current temperature
     * @param humid     The current humidity
     * @param battery   The current battery level
     */
    fun insertFreezerRecord(address: String, temp: Float, humid: Float, battery: Int)
}