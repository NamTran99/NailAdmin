package com.app.inails.booking.admin.repository.auth

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.SalonLocalSource
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.client.UserOwnerDTO

import com.app.inails.booking.admin.model.ui.LoginOwnerForm

@Inject(ShareScope.Activity)
class LoginRepo(
    private val userLocalSource: UserLocalSource,
    private val salonLocalSource: SalonLocalSource,
    private val authenticateApi: AuthenticateApi,
    private val textFormatter: TextFormatter,
	private val appCache: AppCache
) {

    enum class LoginType{
        Owner, Manicurist
    }
    suspend operator fun invoke(form: LoginOwnerForm, loginType: LoginType) {
        form.validate()
        form.lang = userLocalSource.getLanguage()?:"en"
        form.phone = textFormatter.formatPhoneNumber(form.phone)
		form.deviceToken = appCache.deviceToken
        if(loginType == LoginType.Owner){
            val user = authenticateApi.loginOwner(form).await()
            userLocalSource.saveUserOwner(user)
            salonLocalSource.setSalon(user.admin?.salon)
            userLocalSource.setAppMode(UserLocalSource.AppMode.Owner)
            userLocalSource.clearClientAccount()
        }else{
            val user = authenticateApi.loginManicurist(form).await()
            userLocalSource.saveManicuristAccount(user)
            userLocalSource.clearClientAccount()

        }
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
