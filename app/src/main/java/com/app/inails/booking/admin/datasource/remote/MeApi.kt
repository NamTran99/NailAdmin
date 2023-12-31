package com.app.inails.booking.admin.datasource.remote

import android.support.di.InjectBy
import android.support.di.Injectable
import android.support.di.ShareScope
import com.app.inails.booking.admin.helper.network.ApiAsync
import com.app.inails.booking.admin.model.response.*
import com.app.inails.booking.admin.model.response.client.UserClientDTO
import com.app.inails.booking.admin.model.response.client.UserOwnerDTO
import com.app.inails.booking.admin.model.ui.SignUpForm
import com.app.inails.booking.admin.model.ui.SignUpManicuristForm
import com.app.inails.booking.admin.model.ui.UpdateUserPasswordForm
import com.app.inails.booking.admin.model.ui.VoucherForm
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * http://api.booking.kendemo.com:3005/api/v1/
 */
@InjectBy(MeApiImpl::class, ShareScope.Singleton)
interface MeApi : Injectable {
    @POST("salon/change-password")
    fun changePasswordOwner(@Body updateUserPassword: UpdateUserPasswordForm): ApiAsync<Any>
    @FormUrlEncoded
    @POST("manicurist/update")
    fun updateManicurist(@Field("name") name: String): ApiAsync<Any>

    @POST("manicurist/change-password")
    fun changePasswordManicurist(@Body updateUserPassword: UpdateUserPasswordForm): ApiAsync<Any>

    @FormUrlEncoded
    @POST("setting/setting-email-receive-feedback")
    fun settingEmailReceiveFeedback(@Field("email") email: String): ApiAsync<Any>

    @GET("salon/{id}")
    fun getSalonDetail(@Path("id") salonID: Int): ApiAsync<SalonDTO>

    @Multipart
    @POST("salon/update-salon")
    fun updateSalon(
        @PartMap buildMultipart: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part vararg images: MultipartBody.Part?
    ): ApiAsync<SalonDTO>

    @Multipart
    @POST("salon/sign-up")
    fun signUp(
        @PartMap buildMultipart: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part vararg images: MultipartBody.Part?,
    ): ApiAsync<UserOwnerDTO>

    @POST("manicurist/sign-up")
    fun signUpManicurist(
        @Body signUpManicuristForm: SignUpManicuristForm
    ): ApiAsync<UserClientDTO>

    @Multipart
    @POST("salon/register-support")
    fun signUpSupport(
        @PartMap buildMultipart: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part vararg images: MultipartBody.Part?,
    ): ApiAsync<UserOwnerDTO>

    @FormUrlEncoded
    @POST("setting/get-value-default")
    fun getValueServiceDefault(@Field("type") type: String ="contact"): ApiAsync<ContactDTO>

    @GET("check-version")
    fun checkVersion(@Query("platform") platform:String = "android", @Query("app_type") appType: String = "owner"): ApiAsync<VersionDTO>

    @FormUrlEncoded
    @POST("salon/delete-salon-gallery")
    fun deleteSalonGallery(@Field("delete_images") listImageID: String): ApiAsync<Any>

    @Multipart
    @POST("upload/upload-multiple-image")
    fun uploadMultipleImage(
        @PartMap buildMultipart: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part vararg images: MultipartBody.Part?
    ): ApiAsync<ArrayList<String>>

    @FormUrlEncoded
    @POST("salon/change-language")
    fun changeLanguage(@Field("device_token") deviceToken: String, @Field("language") language: String ): ApiAsync<Any>

    @POST("voucher/add-voucher")
    fun addVoucher(@Body voucherForm: VoucherForm): ApiAsync<Any>

    @GET("voucher/list-vouchers")
    fun getListVoucher(@Query("num_per_page") platform:Int = 100,@Query("page") page:Int = 1, @Query("salon_id") salonID: Int): ApiAsync<List<VoucherDTO>>

    @FormUrlEncoded
    @POST("voucher/delete-vouchers")
    fun deleteMultiVoucher(@Field("ids") listID: String): ApiAsync<Any>

    @POST("salon/check-data-sign-up")
    fun checkDataSignUp(@Body signUpForm: SignUpForm): ApiAsync<Any>
}

class MeApiImpl(private val retrofit: Retrofit) :
    MeApi by retrofit.create(MeApi::class.java)