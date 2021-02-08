package com.example.coolerthanyou.datasource

import androidx.room.Query
import com.example.coolerthanyou.model.Freezer

/**
 * Data source (DAO) for the [Freezer] model.
 */
interface IFreezerDao {

    /**
     * Get all freezers in the database
     *
     * @return  [List] of [Freezer]
     */
    fun getAll(): List<Freezer>

    /**
     * Get a single freezer by its boxId
     *
     * @param id    The boxId of the freezer
     * @return  A [Freezer] with the boxId matching [id]
     */
    fun getById(id: String): Freezer

    /**
     * Get a single freezer by its name
     *
     * @param name  The name of the freezer
     * @return  A [Freezer] with the name matching [name]
     */
    fun getByName(name: String): Freezer

    /**
     * Get the Bluetooth addresses for all [Freezer]s in the database
     *
     * @return  A list of all [Freezer]s' Bluetooth addresses.
     */
    @Query("SELECT bluetoothAddress FROM freezer")
    fun getAllBluetooth(): List<String>

    /**
     * Add freezers to the database
     *
     * @param freezer   The [Freezer] to add
     */
    fun insertAll(vararg freezers: Freezer)

    /**
     * Update freezers in the database
     *
     * @param freezers  The [Freezer]s to update
     */
    fun updateAll(vararg freezers: Freezer)

    /**
     * Delete a freezer from the database
     *
     * @param freezer   The [Freezer] to delete
     */
    fun delete(freezer: Freezer)
}