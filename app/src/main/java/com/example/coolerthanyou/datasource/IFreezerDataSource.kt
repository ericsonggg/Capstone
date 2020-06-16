package com.example.coolerthanyou.datasource

import com.example.coolerthanyou.model.Freezer

/**
 * Data source for the [Freezer] model.
 */
interface IFreezerDataSource {

    /**
     * Get all freezers in the database
     *
     * @return  [List] of [Freezer]
     */
    fun getAllFreezers(): List<Freezer>

    /**
     * Get a single freezer by its boxId
     *
     * @param id    The boxId of the freezer
     * @return  A [Freezer] with the boxId matching [id]
     */
    fun getFreezerById(id: String): Freezer

    /**
     * Get a single freezer by its name
     *
     * @param name  The name of the freezer
     * @return  A [Freezer] with the name matching [name]
     */
    fun getFreezerByName(name: String): Freezer

    /**
     * Add a freezer to the database
     *
     * @param freezer   The [Freezer] to add
     */
    fun addFreezer(freezer: Freezer)

    /**
     * Delete a freezer from the database
     *
     * @param freezer   The [Freezer] to delete
     */
    fun deleteFreezer(freezer: Freezer)
}