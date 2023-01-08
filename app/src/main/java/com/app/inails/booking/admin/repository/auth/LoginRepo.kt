package com.app.inails.booking.admin.repository.auth

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.SalonLocalSource
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.UserDTO
import com.app.inails.booking.admin.model.ui.LoginOwnerForm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Inject(ShareScope.Activity)
class LoginRepo(
    private val userLocalSource: UserLocalSource,
    private val salonLocalSource: SalonLocalSource,
    private val authenticateApi: AuthenticateApi,
    private val textFormatter: TextFormatter,
	private val appCache: AppCache
) {
    suspend operator fun invoke(form: LoginOwnerForm) {
        form.validate()
        form.lang = userLocalSource.getLanguage()?:"en"
        form.phone = textFormatter.formatPhoneNumber(form.phone)
		form.deviceToken = appCache.deviceToken
        val user = authenticateApi.login(form).await()
        userLocalSource.saveUser(user)
        userLocalSource.clearClientAccount()
        userLocalSource.saveToken(user.token)
        CoroutineScope(Dispatchers.IO).launch {
            val user1 = authenticateApi.loginClient(form).await()
            salonLocalSource.setSalon(user1.admin?.salon)
        }
    }
}


@Inject(ShareScope.Activity)
class GetOwnerInformation(
    private val userLocalSource: UserLocalSource,
) {
    operator fun invoke(): UserDTO? {
        return userLocalSource.getUserDto()
    }
}
