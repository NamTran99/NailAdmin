package com.app.inails.booking.admin.repository.auth

import android.support.di.Inject
import android.support.di.ShareScope
import android.util.Log
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi

@Inject(ShareScope.Activity)
class LogoutRepo(
    private val userLocalSource: UserLocalSource,
    private val authenticateApi: AuthenticateApi,
    private val appCache: AppCache
) {
    suspend operator fun invoke() {
        with(userLocalSource) {
            clearToken()
            clearUser()
            authenticateApi.logout(appCache.deviceToken).await()
        }
    }
}