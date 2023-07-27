package com.app.inails.booking.admin.views.me.signup

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.app.AppConfig
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentSignUpAccountBinding
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.SalonLocalSource
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.model.response.TimeZoneForm
import com.app.inails.booking.admin.model.response.client.UserOwnerDTO
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToMain
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.me.FetchTimeZone
import com.app.inails.booking.admin.views.me.dialogs.SignUpSuccessDialogOwner
import com.app.inails.booking.admin.views.widget.topbar.NoTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.gson.Gson
import okhttp3.MultipartBody

class SignUpManiAccountFragment : BaseFragment(R.layout.fragment_sign_up_account), TopBarOwner,
    MessageDialogOwner, SignUpSuccessDialogOwner {
    val viewModel by viewModel<SignUpVM>()
    val binding by viewBinding(FragmentSignUpAccountBinding::bind)
    val form: SignUpManicuristForm get() = viewModel.form

    @SuppressLint("StringFormatInvalid")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            NoTopBarState()
        )

        binding.apply {
            etPhone.inputTypePhoneUS()
            btBack.onClick {
                onBackPress()
            }
            btSignUp.onClick {
                form.apply {
                    name = etName.getTextString()
                    phone = etPhone.getTextString().convertPhoneToNormalFormat()
                    password = etPassword.getTextString()
                }
                viewModel.signUpMani()
            }
            viewModel.apply {
                successNoti.bind {
                    success(it)
                    redirectToMain()
                }
            }
        }
    }

}

class SignUpVM(
    private val signUpRepo: SignUpRepo,
    private val fetchTimeZone: FetchTimeZone
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val salonForm = SignUpForm()
    val form = SignUpManicuristForm()
    val timeZoneForm = TimeZoneForm()

    val timeZoneResult = fetchTimeZone.result
    val signUpResult = signUpRepo.result
    val nextStep = signUpRepo.resultNextStep
    val backStep = SingleLiveEvent<Any>()
    val successNoti = SingleLiveEvent<Int>()

    fun signUpMani() = launch(loading, error) {
        signUpRepo.signUp(form)
        successNoti.post(R.string.success_register_mani)
    }

    fun checkValidStep1() = launch(loading, error) {
        signUpRepo.checkValidateStep1(salonForm)
    }

    fun checkValidStep2() = launch(loading, error) {
        signUpRepo.checkValidateStep2(salonForm)
    }

    fun getTimeZone() = launch(loading, error) {
        fetchTimeZone(timeZoneForm)
    }

    fun submit() = launch(loading, error) {
        signUpRepo.uploadServiceImages(salonForm.services).forEach {
            salonForm.services[it.first].avatar = it.second
        }
        signUpRepo.uploadStaffImages(salonForm.staffs).forEach {
            salonForm.staffs[it.first].avatar = it.second
        }
        signUpRepo(salonForm)
    }
}

@Inject(ShareScope.Fragment)
class SignUpRepo(
    val context: Context,
    private val meApi: MeApi,
    private val userLocalSource: UserLocalSource,
    private val appCache: AppCache,
    private val salonLocalSource: SalonLocalSource,
) {
    val result = MutableLiveData<UserOwnerDTO>()
    val resultNextStep = SingleLiveEvent<Any>()


    suspend fun uploadServiceImages(list: List<SalonService>): List<Pair<Int, String>> {
        var count = 0
        val listPosition = mutableListOf<Int>()
        if (list.none { it.imageUri.isNotEmpty() }) return arrayListOf()
        val imageParts = mutableListOf<MultipartBody.Part?>()
        list.mapIndexed { index, item ->
            if (item.imageUri.isNotEmpty()) {
                listPosition.add(index)
                imageParts.add(
                    context.getFilePath(item.imageUri.toUri())!!.scalePhotoLibrary(context)
                        .toImagePart("images[${count++}]")
                )
            }
        }

        val a = meApi.uploadMultipleImage(
            RequestBodyBuilder()
                .put("type", 2).buildMultipart(),
            images = imageParts.toTypedArray()
        ).await()
        return a.mapIndexed { index, item ->
            listPosition[index] to item
        }
    }

    suspend fun uploadStaffImages(list: List<SalonStaff>): List<Pair<Int, String>> {
        var count = 0
        val listPosition = mutableListOf<Int>()
        val imageParts = mutableListOf<MultipartBody.Part?>()
        if (list.none { it.imageUri.isNotEmpty() }) return arrayListOf()

        list.mapIndexed { index, item ->
            if (item.imageUri.isNotEmpty()) {
                listPosition.add(index)
                imageParts.add(
                    context.getFilePath(item.imageUri.toUri())!!.scalePhotoLibrary(context)
                        .toImagePart("images[${count++}]")
                )
            }
        }

        val a = meApi.uploadMultipleImage(
            RequestBodyBuilder()
                .put("type", 2).buildMultipart(),
            images = imageParts.toTypedArray()
        ).await()
        return a.mapIndexed { index, item ->
            listPosition[index] to item
        }
    }

    suspend fun checkValidateStep1(form: SignUpForm) {
        form.type = "partner"
        form.validateStep1()
        resultNextStep.post(
            meApi.checkDataSignUp(form).await()
        )
    }

    suspend fun checkValidateStep2(form: SignUpForm) {
        form.type = "salon"
        form.validateStep2()
        resultNextStep.post(
            meApi.checkDataSignUp(form).await()
        )
    }

    suspend operator fun invoke(form: SignUpForm) {
        var paidImage:
                List<MultipartBody.Part?> = listOf()
        paidImage = form.paidMenuImages.filter { !it.image.contains("http") }
            .mapIndexed { index, uriLink ->
                context.getFilePath(uriLink.image.toUri())!!.scalePhotoLibrary(context)
                    .toImagePart("fileList")
            }
        val imageParts =
            form.images.filter { !it.image.contains("http") }.mapIndexed { index, uriLink ->
                context.getFilePath(uriLink.image.toUri())!!.scalePhotoLibrary(context)
                    .toImagePart("images")
            }.toMutableList().apply {
                addAll(paidImage)
            }.toTypedArray()
        result.post(
            meApi.signUp(
                RequestBodyBuilder()
                    .put("phone", form.phone)
                    .put("password", form.password)
                    .put("salon_name", form.salon_name)
                    .put("salon_phone", form.salon_phone)
                    .putIf(form.salon_email.isNotBlank(), "salon_email", form.salon_email)
                    .put("salon_address", form.salon_address)
                    .put("salon_state", form.salon_state)
                    .put("salon_city", form.salon_city)
                    .putIf(form.salon_zipcode.isNotEmpty(), "salon_zipcode", form.salon_zipcode)
                    .put("salon_country", form.salon_country)
                    .put("schedules", form.schedules.toString())
                    .putIf(form.staffs.isNotEmpty(), "staffs", Gson().toJson(form.staffs))
                    .putIf(form.services.isNotEmpty(), "services", Gson().toJson(form.services))
                    .put("salon_tz", form.salon_tz)
                    .put("salon_timezone", form.salon_timezone)
                    .put("salon_lat", form.salon_lat)
                    .put("salon_lng", form.salon_lng)
                    .put("salon_description", form.salon_description)
                    .put("device_token", appCache.deviceToken)
                    .put("device_type", "android")
                    .put("device_info", AppConfig.phoneInfo)
                    .put("name", form.admin_name)
//                    .put("input_option", form.input_option)
                    .put("lang", userLocalSource.getLanguageWithDefault())
                    .buildMultipart(),
                images = imageParts
            ).await().apply {
                userLocalSource.saveUserOwner(this)
                salonLocalSource.setSalon(this.admin?.salon)
            })
    }

    suspend fun signUp(form: SignUpManicuristForm) {
        form.validate()
        val user = meApi.signUpManicurist(form).await()
        userLocalSource.saveManicuristAccount(user)
        userLocalSource.clearClientAccount()
    }
}