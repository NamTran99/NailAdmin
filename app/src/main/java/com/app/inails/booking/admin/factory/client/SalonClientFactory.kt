package com.app.inails.booking.admin.factory.client

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.factory.helper.FactoryHelper
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.client.SalonClientDTO
import com.app.inails.booking.admin.model.ui.client.ISalonClient
import com.app.inails.booking.admin.model.ui.client.ISalonDTOOwner
import com.app.inails.booking.admin.model.ui.client.ISalonDetailClient

@Inject(ShareScope.Singleton)
class SalonClientFactory(textFormatter: TextFormatter) : FactoryHelper(textFormatter) {

    private fun create(salonDTO: SalonClientDTO): ISalonClient {
        return object : ISalonClient, ISalonDTOOwner {
            override val salonName: String
                get() = salonDTO.name.safe()
            override val slug: String
                get() = salonDTO.slug.safe()
            override val value: SalonClientDTO get() = salonDTO
            override val address: String
                get() = salonDTO.address.toString().safe()
            override val phoneNumber: String
                get() = textFormatter.formatPhoneUS(salonDTO.phone)
            override val rating: Float
                get() = salonDTO.rating.safe()
        }
    }

    fun createSalons(dtoData: List<SalonClientDTO>): List<ISalonClient> {
        return dtoData.map(::create)
    }

    fun createDetail(salonDTO: SalonClientDTO): ISalonDetailClient {
        return object : ISalonDetailClient, ISalonClient by create(salonDTO) {
            override val ownerName: String
                get() = salonDTO.partner?.name.safe()
            override val des: String
                get() = salonDTO.description.safe()
            override val lat: Float
                get() = salonDTO.lat
            override val lng: Float
                get() = salonDTO.lng
            override val images: List<String>?
                get() = salonDTO.images?.map { it.image }
            override val schedules: Pair<List<Pair<String, String>>?, String>
                get() = createSchedule(salonDTO)
            override val imagesBefore: List<String>?
                get() = salonDTO.gallery?.filter { it.typeName == "before" }?.map { it.image }
            override val imagesAfter: List<String>?
                get() = salonDTO.gallery?.filter { it.typeName == "after" }?.map { it.image }
            override val email: String
                get() = displaySafe(salonDTO.email)
        }
    }

    fun createSchedule(
        salon: SalonClientDTO?,
        isDownLine: Boolean = false
    ): Pair<List<Pair<String, String>>?, String> {
        val schedules = salon?.schedules?.map {
            Pair(
                it.dayName,
                textFormatter.formatSalonScheduleClient(it)
            )
        }
        return Pair(schedules, textFormatter.formatBusinessHours(salon, isDownLine))
    }
}