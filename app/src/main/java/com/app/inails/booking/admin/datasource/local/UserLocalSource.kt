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
    private val shareIOScope: ShareIOScope,
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
    fun getSalonID(): Int? {
        return user?.admin?.salon_id
    }

    fun isLogin() = user != null
    fun saveToken(token: String) {
        appCache.token = token
    }

    fun clearToken() {
        appCache.token = ""
    }
    fun clearAccount() {
        appCache.email = ""
        appCache.password = ""
    }

    fun getUserDto(): UserDTO = user?: UserDTO()

    fun getUserLive(): MutableLiveData<UserDTO> {
        return userLive
    }

    fun saveUser(userDTO: UserDTO) {
        user = userDTO
        shareIOScope.launch {
            userLive.post(userDTO)
        }
    }

    fun updateEmailFeedbackUser(email: String){
        user?.admin?.salon?.email = email
    }

    // When log out
    fun clearUser(){
        user = null
    }

    fun logout() {
        user = null
    }

}