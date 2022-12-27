package com.app.inails.booking.admin.factory.client

import android.support.core.livedata.map
import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.datasource.local.SalonLocalSource
import com.app.inails.booking.admin.datasource.local.dao.SalonDAO
import com.app.inails.booking.admin.datasource.remote.clients.SalonApi
import com.app.inails.booking.admin.factory.SalonFactory
import com.app.inails.booking.admin.model.response.SalonDTO
import com.app.inails.booking.admin.model.response.client.SalonClientDTO
import com.app.inails.booking.admin.model.ui.ISalon
import com.app.inails.booking.admin.model.ui.client.ISalonClient

@Inject(ShareScope.FragmentOrActivity)
class FetchAllSalonRepository(
    private val salonApi: SalonApi,
    private val salonFactory: SalonClientFactory,
    private val salonLocalSource: SalonLocalSource,
    private val salonDAO: SalonDAO
) {
    val results = MutableLiveData<Pair<List<ISalonClient>, Boolean>>()

    suspend operator fun invoke(key: String, page: Int = 1) {
        val salons = salonApi.search(key, page).await()
        if (page == 1)
            salonDAO.saveAll(salons)
        else salonDAO.update(salons)
        results.post(Pair(salonFactory.createSalons(salons), page > 1))
    }

    fun saveSalonSelected(salon: SalonClientDTO?) {
        salonLocalSource.setSalon(salon)
    }

    fun isExistSalon() = salonLocalSource.isExistSalon()

    fun getSalonName() = salonLocalSource.getSalonName()

    fun getSalonLive(): LiveData<Pair<Long?, String?>> {
        return salonLocalSource.getSalonLive().map { Pair(it?.id, it?.name) }
    }

    fun getSalonScheduleLive(): LiveData<Pair<List<Pair<String, String>>?, String>> {
        return salonLocalSource.getSalonLive()
            .map { salonFactory.createSchedule(it, true) }
    }

    fun getSalonSelectedID() = salonLocalSource.getSalonId()
}