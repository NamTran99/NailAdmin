package com.app.inails.booking.admin.datasource.local

import android.content.Context
import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.persistent.cache.GsonCaching
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.helper.ShareIOScope
import com.app.inails.booking.admin.model.response.client.BookingClientDTO
import com.app.inails.booking.admin.model.response.client.SalonClientDTO
import kotlinx.coroutines.launch

@Inject(ShareScope.Singleton)
class SalonLocalSource(
    context: Context,
    private val appCache: AppCache,
    private val shareIOScope: ShareIOScope
) {
    private val caching = GsonCaching(context)
    private var salonSelected: SalonClientDTO? by caching.reference(SalonClientDTO::class.java.name)
    private val salonLive = MutableLiveData<SalonClientDTO?>()

    fun getSalonSlug() = salonSelected?.slug ?: ""
    fun getSalonId() = salonSelected?.id ?: ""
    fun getSalonName() = salonSelected?.name ?: ""

    init {
        shareIOScope.launch {
            salonLive.post(getSalonDto())
        }
    }

    private fun getSalonDto(): SalonClientDTO? =salonSelected

    fun clear() {
        setSalon(null)
    }

    fun setSalon(salon: SalonClientDTO?) {
        this.salonSelected = salon
        shareIOScope.launch {
            salonLive.post(salon)
        }
    }

    fun isExistSalon() = salonSelected != null

    fun setAppointmentCurrent(appointment: BookingClientDTO) {
        appCache.bookingCurrent = appointment
    }

    fun getSalonLive(): MutableLiveData<SalonClientDTO?> {
        return salonLive
    }
}