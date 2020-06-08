package com.example.coolerthanyou.repository

import com.example.coolerthanyou.datasource.FreezerRoomDataSource
import com.example.coolerthanyou.model.Freezer
import javax.inject.Inject

/**
 * Room-specific implementation of the [FreezerRepository]
 *
 * @property freezerDao The DAO for the Freezer entity
 */
class FreezerRoomRepository @Inject constructor(private val freezerDao: FreezerRoomDataSource) : FreezerRepository {

    override fun getAllFreezers(): List<Freezer> {
        return freezerDao.getAllFreezers()
    }

    override fun getFreezerById(id: String): Freezer {
        return freezerDao.getFreezerById(id)
    }

    override fun getFreezerByName(name: String): Freezer {
        return freezerDao.getFreezerByName(name)
    }

    override fun addFreezer(freezer: Freezer) {
        return freezerDao.addFreezer(freezer)
    }

    override fun deleteFreezer(freezer: Freezer) {
        return freezerDao.deleteFreezer(freezer)
    }
}