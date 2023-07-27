package com.app.inails.booking.admin.datasource.local

import android.content.Context
import android.support.core.livedata.post
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.persistent.cache.GsonCaching
import androidx.lifecycle.MutableLiveData
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.helper.ShareIOScope
import com.app.inails.booking.admin.model.response.StateDTO
import com.app.inails.booking.admin.model.response.client.UserClientDTO
import com.app.inails.booking.admin.model.response.client.UserOwnerDTO
import com.app.inails.booking.admin.model.ui.IState
import com.esafirm.imagepicker.helper.LocaleManager
import kotlinx.coroutines.launch

@Inject(ShareScope.Singleton)
class UserLocalSource(
    private val shareIOScope: ShareIOScope,
    context: Context,
    private val appCache: AppCache,
) {

    private val caching = GsonCaching(context)

    private val userLive = MutableLiveData<UserOwnerDTO>()
    private val userClientLive = MutableLiveData<UserClientDTO?>()
    private val allUserLive = MutableLiveData<Pair<UserOwnerDTO?, UserClientDTO?>>()

    private var userClient: UserClientDTO? by caching.reference(UserClientDTO::class.java.name)
    private var userOwner: UserOwnerDTO? by caching.reference(UserOwnerDTO::class.java.name)

    //    private var ownerUser: OwnerDTO? by caching.reference(OwnerDTO::class.java.name)
    private var manicuristUser: UserClientDTO? by caching.reference("mani")
    private var appMode: AppMode? by caching.reference("AppMode")
    private var isFirstOpenApp: Boolean? by caching.reference("fistOpenApp")

    enum class AppMode {
        Owner, Manicurist, Client
    }

    init {
        shareIOScope.launch {
            userLive.post(getUserDto())
            allUserLive.post(getUserOwnerDto() to getUserClientDto())
        }
    }

    private var listState: List<IState>? by caching.reference(StateDTO::class.java.name)

    fun isExistListState(): Boolean {
        return !listState.isNullOrEmpty()
    }

    fun getListStateLocal(): List<IState>? {
        return listState
    }

//    fun getUserId() = ownerUser?.?.id?: 0

    @JvmName("setOwnerMode1")
    fun setAppMode(mode: AppMode) {
        appMode = mode
    }

    @JvmName("getAppMode1")
    fun getAppMode() = appMode

    fun getUserId() =
        manicuristUser?.user?.id.safe()


    fun isOwnerMode() = appMode == AppMode.Owner

    fun getUserOwnerDto(): UserOwnerDTO? = userOwner
    fun getUserClientDto(): UserClientDTO? = userClient

    fun getAllUserLive(): MutableLiveData<Pair<UserOwnerDTO?, UserClientDTO?>> {
        return allUserLive
    }

    fun getSalonPhone() = this.userOwner?.admin?.phone ?: ""

    fun getToken(): String {
        return appCache.token
    }

    fun getSalonID(): Int {
        return this.userOwner?.admin?.salon_id ?: 0
    }

    fun isLogin() = appCache.token != ""
    fun saveToken(token: String) {
        appCache.token = token
    }

    fun isOwnerLogin() = this.userOwner != null

    fun clearToken() {
        appCache.token = ""
    }

    fun clearClientAccount() {
        userClient = null
    }

    @JvmName("getManicuristUser1")
    fun getManicuristUser() = this.manicuristUser

    fun getUserDto(): UserOwnerDTO? = this.userOwner

    fun changeSalonName(salonName: String?) {
        this.userOwner?.admin?.salon?.name = salonName
    }

    fun changeOwnerName(name: String) {
        this.userOwner?.admin?.name = name
        this.userOwner= this.userOwner
    }

    fun getOwnerName() =
        this.userOwner?.admin?.name ?: ""

    fun approveUser() {
        userOwner = userOwner?.apply {
            admin?.is_approve = 1
        }
    }

    fun saveManicuristAccount(manicuristDTO: UserClientDTO) {
        manicuristUser = manicuristDTO
        appMode = UserLocalSource.AppMode.Manicurist
        saveToken(manicuristDTO.token)
    }

    fun getManicuristID() = manicuristUser?.user?.id.safe()

    fun saveUserClient(userDTO: UserClientDTO) {
        userClient = userDTO
        saveToken(userDTO.token)
        shareIOScope.launch {
            userClientLive.post(userDTO)
            allUserLive.post(getUserOwnerDto() to getUserClientDto())
        }
    }

    fun saveUserOwner(userOwner: UserOwnerDTO) {
        this.userOwner = userOwner
        appMode = UserLocalSource.AppMode.Owner
        saveToken(userOwner.token)
        shareIOScope.launch { allUserLive.post(getUserOwnerDto() to getUserClientDto()) }
    }

    fun changeNameManicurist(name: String) {
        this.manicuristUser?.user?.name = name
    }

    fun updateEmailFeedbackUser(email: String) {
        this.userOwner?.admin?.salon?.email = email
    }

    // is first login
    fun saveOpenAppAlready() {
        isFirstOpenApp = false
    }

    fun getIsFirstOpenApp(): Boolean? {
        return isFirstOpenApp
    }

    // set language

    fun isVietNamLanguage(): Boolean {
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

    fun getLanguage() = if (appMode != AppMode.Client) appCache.language else "en"
    fun getLanguageWithDefault() =
        if (appMode != AppMode.Client) appCache.language ?: LocaleManager.mLanguage else "en"

    // isCloseAll instance activity

    fun getIsCloseInstanceNavigateActivity() = appCache.isCloseAllMainNavigationActivity ?: false

    //    fun setC
    // When log out

    fun logOutClient() {
        this.userClient = null
        this.clearToken()
    }

    fun clearUser() {
        this.clearToken()
        this.appMode = null
        this.userOwner = null
        this.manicuristUser = null
        this.userClient = null
    }

}