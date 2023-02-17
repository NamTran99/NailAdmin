package com.app.inails.booking.admin.datasource.local.dao

import android.support.di.Inject
import android.support.di.ShareScope
import android.support.persistent.disk.QueryOptions
import com.app.inails.booking.admin.model.response.SalonDTO
 

@Inject(ShareScope.Singleton)
class SalonDAO(daoProvider: DaoProvider) {

    private val dao = daoProvider.salonDAO

    suspend fun save(data: SalonDTO) {
        if (find(data.id) != null)
            removeByID(data.id)
        dao.save(data)
    }

    suspend fun saveAll(data: List<SalonDTO>) {
        dao.removeAll()
        dao.saveAll(data)
    }

    suspend fun getAll(): List<SalonDTO> {
        return dao.findAll()
    }

    suspend fun find(id: Long): SalonDTO? {
        return dao.findById(id.toString())
    }

    suspend fun findAllBy(key: String): List<SalonDTO> {
        return dao.findAllWith(
            QueryOptions(search = "%${key}%")
        ).data
    }

    suspend fun update(salons: List<SalonDTO>) {
        salons.forEach { save(it) }
    }

    suspend fun removeByID(id: Long) {
        dao.remove(id.toString())
    }
}