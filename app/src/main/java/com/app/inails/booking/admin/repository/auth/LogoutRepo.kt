package com.app.inails.booking.admin.repository.auth

import android.content.Context
import android.support.di.Inject
import android.support.di.ShareScope
import android.util.Log
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.notification.NotificationsManager
import com.app.inails.booking.admin.notification.NotificationsManagerClient

@Inject(ShareScope.Activity)
class LogoutRepo(
    private val userLocalSource: UserLocalSource,
    private val authenticateApi: AuthenticateApi,
    private val appCache: AppCache,
    private val context: Context
) {
    suspend operator fun invoke() {
        with(userLocalSource) {
            clearUser()
            clearToken()
            NotificationsManager(context).cancelAll()
            NotificationsManagerClient(context).cancelAll()
            authenticateApi.logout(appCache.deviceToken).await()
        }
    }
}