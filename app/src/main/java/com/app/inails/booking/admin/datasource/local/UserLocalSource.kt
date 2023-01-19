package com.app.inails.booking.admin.datasource.local

import android.content.Context
import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.persistent.cache.GsonCaching
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.extention.displaySafe1
import com.app.inails.booking.admin.helper.ShareIOScope
import com.app.inails.booking.admin.model.response.SalonDTO
import com.app.inails.booking.admin.model.response.UserDTO
import com.app.inails.booking.admin.model.response.client.UserClientDTO
import com.app.inails.booking.admin.model.response.client.UserOwnerDTO
import com.esafirm.imagepicker.helper.LocaleManager
import kotlinx.coroutines.launch

@Inject(ShareScope.Singleton)
class UserLocalSource(
    private val shareIOScope: ShareIOScope,
    context: Context,
    private val appCache: AppCache,
) {

    private val caching = GsonCaching(context)

    private val userLive = MutableLiveData<UserDTO>()
    private val userClientLive = MutableLiveData<UserClientDTO?>()
    private val allUserLive = MutableLiveData<Pair<UserOwnerDTO?, UserClientDTO?>>()

    private var userClient: UserClientDTO? by caching.reference(UserClientDTO::class.java.name)
    private var userOwner: UserOwnerDTO? by caching.reference(UserOwnerDTO::class.java.name)
    private var isOwnerMode: Boolean? by caching.reference("AppMode")

    private var user: UserDTO? by caching.reference(UserDTO::class.java.name)
    private var isFirstOpenApp: Boolean? by caching.reference("fistOpenApp")

    init {
        shareIOScope.launch {
            userLive.post(getUserDto())
            allUserLive.post(getUserOwnerDto() to getUserClientDto())
        }
    }

    fun setOwnerMode(isOwner: Boolean) {
        isOwnerMode = isOwner
    }

    fun getOwnerMode() = isOwnerMode ?: true

    fun getUserOwnerDto(): UserOwnerDTO? = userOwner
    fun getUserClientDto(): UserClientDTO? = userClient

    fun getAllUserLive(): MutableLiveData<Pair<UserOwnerDTO?, UserClientDTO?>> {
        return allUserLive
    }

    fun getSalonPhone() = user?.admin?.phone ?: ""

    fun getToken(): String? {
        return userClient?.token ?: user?.token
    }

    fun getSalonID(): Int? {
        return user?.admin?.salon_id
    }

    fun isLogin() = user != null
    fun saveToken(token: String) {
        appCache.token = token
    }

    fun isOwnerLogin() = user != null

    fun clearToken() {
        appCache.token = ""
    }

    fun clearAccount() {
        appCache.email = ""
        appCache.password = ""
    }

    fun clearClientAccount() {
        userClient = null
    }

    fun getUserDto(): UserDTO? = user
    fun getSalonDto(): SalonDTO? = user?.admin?.salon
    fun changeUserSlug(slug: String?) {
        user?.admin?.salon?.slug = slug ?: ""
    }

    fun changeSalonName(salonName: String?) {
        user?.admin?.salon?.name = salonName
    }

    fun getUserLive(): MutableLiveData<UserDTO> {
        return userLive
    }

    fun getUserClientLive(): MutableLiveData<UserClientDTO?> {
        return userClientLive
    }

    fun saveUser(userDTO: UserDTO) {
        user = userDTO
        shareIOScope.launch {
            userLive.post(userDTO)
        }
    }

    fun saveUserClient(userDTO: UserClientDTO?) {
        userClient = userDTO
        shareIOScope.launch {
            userClientLive.post(userDTO)
            allUserLive.post(getUserOwnerDto() to getUserClientDto())
        }
    }

    fun saveUserOwner(userOwner: UserOwnerDTO?) {
        this.userOwner = userOwner
        shareIOScope.launch { allUserLive.post(getUserOwnerDto() to getUserClientDto()) }
    }


    fun logout() {
        saveUserClient(null)
        saveUserOwner(null)
    }


    fun logoutCustomer() {
        saveUserClient(null)
    }

    fun updateEmailFeedbackUser(email: String) {
        user?.admin?.salon?.email = email
    }

    // is first login
    fun saveOpenAppAlready() {
        isFirstOpenApp = false
    }

    fun getIsFirstOpenApp(): Boolean? {
        return isFirstOpenApp
    }

    // set language

    fun isVietNamLanguage():Boolean{
        return appCache.language == "vi"
    }

    fun setLanguage(mLanguage: String): Boolean {
        val isChangeSuccess = mLanguage != appCache.language
        appCache.language = mLanguage
        return isChangeSuccess
    }

    fun clearLanguage() {
        appCache.language = null
    }

    fun getLanguage() = if(isOwnerMode != false) appCache.language   else "en"
    fun getLanguageWithDefault() = if(isOwnerMode != false)  appCache.language?: LocaleManager.mLanguage else "en"

    // isCloseAll instance activity

    fun getIsCloseInstanceNavigateActivity()= appCache.isCloseAllMainNavigationActivity?:false

//    fun setC
    // When log out
    fun clearUser() {
        user = null
    }

    // salon

}