package com.app.inails.booking.admin.repository.aboutapp

import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AboutAppApi
import com.app.inails.booking.admin.factory.BannerFactory
import com.app.inails.booking.admin.model.response.client.UserOwnerDTO

import com.app.inails.booking.admin.model.ui.IIntro

@Inject(ShareScope.Activity)
class BannerIntroGuidanceRepo(
    private val aboutAppApi: AboutAppApi,
    private val bannerFactory: BannerFactory
) {
    val introResults = MutableLiveData<List<IIntro>>()
    val adsResults = MutableLiveData<List<IIntro>>()
    val guideResults = MutableLiveData<List<IIntro>>()
    suspend fun getIntroList() {
        introResults.post(
            bannerFactory.createListIntro(aboutAppApi.getListBanner(type = TYPE_INTRO).await())
        )
    }

    suspend fun getAdsList() {
        adsResults.post(
            bannerFactory.createListIntro(aboutAppApi.getListBanner(type = TYPE_ADS).await())
        )
    }

    suspend fun getGuideList() {
        guideResults.post(
            bannerFactory.createListIntro(aboutAppApi.getListBanner(type = TYPE_GUIDE).await())
        )
    }

    companion object {
        const val TYPE_INTRO = 1
        const val TYPE_ADS = 2
        const val TYPE_GUIDE = 3
    }
}


@Inject(ShareScope.Activity)
class GetOwnerInformation(
    private val userLocalSource: UserLocalSource,
) {
    operator fun invoke(): UserOwnerDTO? {
        return userLocalSource.getUserDto()
    }
}
