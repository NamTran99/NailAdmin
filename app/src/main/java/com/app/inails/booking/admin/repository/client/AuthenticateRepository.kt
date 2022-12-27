package com.app.inails.booking.admin.repository.client

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.SalonLocalSource
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.datasource.remote.clients.AuthenticateClientApi

@Inject(ShareScope.Activity)
class AuthenticateRepository(
    private val userLocalSource: UserLocalSource,
    private val salonLocalSource: SalonLocalSource,
    private val authenticateApi: AuthenticateClientApi,
    private val appCache: AppCache
) {
    fun isLogged() = userLocalSource.isLogin()
    fun isOwnerLogged() = userLocalSource.isOwnerLogin()
    
    suspend fun logout() {
        authenticateApi.logout(appCache.clientTokenPush).await()
//        if (userLocalSource.isOwnerLogin())
            logoutCustomer()
//        else logoutAll()
    }

    fun logoutCustomer() {
        userLocalSource.logoutCustomer()
    }

    fun logoutAll() {
        userLocalSource.logout()
        salonLocalSource.clear()
    }
}