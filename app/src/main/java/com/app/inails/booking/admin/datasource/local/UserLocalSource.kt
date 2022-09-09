package com.app.inails.booking.admin.datasource.local

import android.content.Context
import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.persistent.cache.GsonCaching
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.helper.ShareIOScope
import com.app.inails.booking.admin.model.response.UserDTO
import kotlinx.coroutines.launch

@Inject(ShareScope.Singleton)
class UserLocalSource(
    shareIOScope: ShareIOScope,
    context: Context,
    private val appCache: AppCache,
) {

    private val userLive = MutableLiveData<UserDTO>()

    init {
        shareIOScope.launch { userLive.post(getUserDto()) }
    }

    private val caching = GsonCaching(context)
    private var user: UserDTO? by caching.reference(UserDTO::class.java.name)

    fun getToken(): String? {
        return user?.token
    }

    fun isLogin() = user != null

    fun clearAccount() {
        appCache.email = ""
        appCache.password = ""
    }

    fun getUserDto(): UserDTO? = user

    fun getUserLive(): MutableLiveData<UserDTO> {
        return userLive
    }

    fun saveUser(userDTO: UserDTO) {
        user = userDTO
        userLive.post(userDTO)
    }

    fun logout() {
        user = null
        appCache.tokenPush = ""
    }

}