package com.app.inails.booking.admin.datasource.local.dao

import android.support.di.Inject
import android.support.di.ShareScope
import android.support.persistent.disk.QueryOptions
import com.app.inails.booking.admin.model.response.client.SalonClientDTO

@Inject(ShareScope.Singleton)
class SalonDAO(daoProvider: DaoProvider) {

    private val dao = daoProvider.salonDAO

    suspend fun save(data: SalonClientDTO) {
        if (find(data.id) != null)
            removeByID(data.id)
        dao.save(data)
    }

    suspend fun saveAll(data: List<SalonClientDTO>) {
        dao.removeAll()
        dao.saveAll(data)
    }

    suspend fun getAll(): List<SalonClientDTO> {
        return dao.findAll()
    }

    suspend fun find(id: Long): SalonClientDTO? {
        return dao.findById(id.toString())
    }

    suspend fun findAllBy(key: String): List<SalonClientDTO> {
        return dao.findAllWith(
            QueryOptions(search = "%${key}%")
        ).data
    }

    suspend fun update(salons: List<SalonClientDTO>) {
        salons.forEach { save(it) }
    }

    suspend fun removeByID(id: Long) {
        dao.remove(id.toString())
    }
}