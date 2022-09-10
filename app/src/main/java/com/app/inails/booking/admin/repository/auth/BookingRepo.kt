package com.app.inails.booking.admin.repository.auth

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.model.ui.LoginForm

@Inject(ShareScope.Activity)
class BookingRepo(
    private val userLocalSource: UserLocalSource,
    private val authenticateApi: AuthenticateApi,
	private val appCache: AppCache
) {
    suspend operator fun invoke(form: LoginForm) {
        form.validate()
		form.deviceToken = appCache.tokenPush
        val user = authenticateApi.login(form).await()
        userLocalSource.saveUser(user)
    }
}