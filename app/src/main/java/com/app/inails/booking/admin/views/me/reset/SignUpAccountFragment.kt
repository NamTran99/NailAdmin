package com.app.inails.booking.admin.views.me.reset

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.post
import android.support.core.route.clear
import android.support.core.route.open
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentSignUpAccountBinding
import com.app.inails.booking.admin.datasource.local.AppCache
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.AuthenticateApi
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.model.response.TimeZoneForm
import com.app.inails.booking.admin.model.response.UserDTO
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.utils.TimeUtils
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.extension.ShowZoomSingleImageActivity
import com.app.inails.booking.admin.views.main.MainActivity
import com.app.inails.booking.admin.views.me.EditScheduleArgs
import com.app.inails.booking.admin.views.me.EditScheduleFragment
import com.app.inails.booking.admin.views.me.FetchTimeZone
import com.app.inails.booking.admin.views.me.adapters.SalonScheduleAdapter
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoAdapter
import com.app.inails.booking.admin.views.me.dialogs.SignUpSuccessDialogOwner
import com.app.inails.booking.admin.views.widget.DisplayType
import com.app.inails.booking.admin.views.widget.SalonServiceView
import com.app.inails.booking.admin.views.widget.SalonStaffView
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.libraries.places.api.model.Place
import com.google.gson.Gson
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import kotlin.math.roundToInt

class SignUpAccountFragment : BaseFragment(R.layout.fragment_sign_up_account), TopBarOwner,
    MessageDialogOwner, SignUpSuccessDialogOwner {
    val viewModel by viewModel<SignUpVM>()
    val binding by viewBinding(FragmentSignUpAccountBinding::bind)
    private lateinit var imageAdapter: UploadPhotoAdapter
    private lateinit var scheduleAdapter: SalonScheduleAdapter
    val signUpForm: SignUpForm
        get() = viewModel.salonForm

    var staffImage =  ArrayList<String>()
    var serviceImage =  ArrayList<String>()

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val pathImage =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf<Uri>()
                imageAdapter.changePath(pathImage.map { pathUri -> AppImage(path = pathUri.toString()) }
                    .apply {
                        signUpForm.images = this.toMutableList()
                    })
            }
        }

    private val staffImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val pathImage =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf<Uri>()
                staffImage =
                    ArrayList(pathImage.map { pathUri -> pathUri.toString() })
                binding.salonStaffView.updateAvatar(staffImage.getOrNull(0))
            }
        }

    private val serviceImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val pathImage =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf<Uri>()
                serviceImage =
                    ArrayList(pathImage.map { pathUri -> pathUri.toString() })
                binding.salonServiceView.updateAvatar(serviceImage.getOrNull(0))
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_sign_up,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        binding.apply {
            salonStaffView.apply {
                onCLickImage = {
                    if(staffImage.isEmpty()){
                        FishBun.with(this@SignUpAccountFragment)
                            .setImageAdapter(GlideAdapter())
                            .setMaxCount(1)
                            .setSelectedImages(ArrayList(staffImage.map{it.toUri()}))
                            .setActionBarColor(
                                ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                                ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                                true
                            )
                            .setActionBarTitleColor(Color.parseColor("#ffffff"))
                            .startAlbumWithActivityResultCallback(staffImageResult)
                    }else{
                        open<ShowZoomSingleImageActivity>(Routing.ShowZoomSingleImage(staffImage[0]))
                    }
                }
                onCLickRemoveImage = {
                    staffImage.clear()
                }
            }

            salonServiceView.apply {
                salonServiceView.apply {
                    onCLickImage = {
                        if(serviceImage.isEmpty()){
                            FishBun.with(this@SignUpAccountFragment)
                                .setImageAdapter(GlideAdapter())
                                .setMaxCount(1)
                                .setSelectedImages(ArrayList(serviceImage.map{it.toUri()}))
                                .setActionBarColor(
                                    ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                                    ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                                    true
                                )
                                .setActionBarTitleColor(Color.parseColor("#ffffff"))
                                .startAlbumWithActivityResultCallback(serviceImageResult)
                        }else{
                            open<ShowZoomSingleImageActivity>(Routing.ShowZoomSingleImage(serviceImage[0]))
                        }
                    }
                    onCLickRemoveImage = {
                        serviceImage.clear()
                    }
                }

            }

            edtAccPhone.inputTypePhoneUS()
            edtSalonPhone.inputTypePhoneUS()
            scheduleAdapter = SalonScheduleAdapter(rcvSchedule).apply {
                submit(signUpForm.schedules)
            }
            imageAdapter = UploadPhotoAdapter(rvImages).apply {
                onAddImagesAction = {
                    FishBun.with(this@SignUpAccountFragment)
                        .setImageAdapter(GlideAdapter())
                        .setMaxCount(20)
                        .setMinCount(1)
                        .setSelectedImages(ArrayList(signUpForm.images.map { it.path.toUri() }))
                        .setActionBarColor(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            true
                        )
                        .setActionBarTitleColor(Color.parseColor("#ffffff"))
                        .startAlbumWithActivityResultCallback(startForResult)
                }

                onRemoveImageAction = {
                    signUpForm.images.remove(it)
                }
            }

            tvEditSchedule.onClick {
                val editScheduleArgs = EditScheduleArgs(
                    signUpForm.schedules,
                    "${signUpForm.zoneID} UTC ${signUpForm.offsetDisplay}"
                )
                Router.run { redirectToEditScheduleSalon(editScheduleArgs) }
            }

            btnSignUp.onClick {
                signUpForm.run {
                    phone = edtAccPhone.getTextString().convertPhoneToNormalFormat()
                    password = edtAccPassword.getTextString()
                    salon_name = edtSalonName.getTextString()
                    salon_address = edtSalonAddress.getTextString()
                    salon_phone = edtSalonPhone.getTextString().convertPhoneToNormalFormat()
                    salon_description = edtDescription.text.toString()
                    admin_name = edtAccName.text.toString()
                    salon_email = edtSalonEmail.text.toString()
                }
                viewModel.submit()
            }

            tvAddService.onClick {
                if (salonServiceView.checkValidate()) {
                    signUpForm.services.add(salonServiceView.service)
                    addService(salonServiceView.service)
                    salonServiceView.resetView()
                }
            }
            tvAddStaff.onClick {
                if (salonStaffView.checkValidate()) {
                    signUpForm.staffs.add(salonStaffView.staffData)
                    addStaff(salonStaffView.staffData)
                    salonStaffView.resetView()
                }
            }
            edtSalonAddress.onClick {
                appSettings.openPlaceAutoComplete("", ::onPlaceSelected)
            }
        }

        viewModel.apply {
            //set up data

            servicePriceDefaultResult.bind {
                binding.tvLink.text = Html.fromHtml(requireContext().getString(R.string.test_link, it), Html.FROM_HTML_MODE_COMPACT)
                binding.tvLink.movementMethod = LinkMovementMethod.getInstance()
            }

            salonForm.services.forEach {
                addService(it)
            }

            salonForm.staffs.forEach {
                addStaff(it)
            }
            timeZoneResult.bind {
                val oldTimezone = salonForm.getTimeZoneDisplay(false)
                signUpForm.run {
                    zoneID = it.timeZoneId
                    offsetDisplay = TimeUtils.getTimeOffset(it.timeZoneId)
                    salon_tz = "UTC ${offsetDisplay}"

                }
                val newTimeZone = salonForm.getTimeZoneDisplay(false)
                binding.tvBusinessHour.text = salonForm.getTimeZoneDisplay(true)

                if (oldTimezone != newTimeZone) {
                    messageDialog.show(
                        "Notice",
                        "Your timezone has changed from $oldTimezone to ${newTimeZone}\nPlease check your business hour before saving information!"
                    )
                }
            }
            signUpResult.bind {
                signupDialog.show {
                    open<MainActivity>().clear()
                }
            }
        }

        parentFragmentManager.setFragmentResultListener(
            EditScheduleFragment.REQUEST_KEY, viewLifecycleOwner
        ) { requestKey, result ->
            val listSchedule = result.getSerializable("schedules") as List<ISchedule>
            listSchedule.forEach {
                it.startTimeFormat = it.startTime.toTimeDisplay()
                it.endTimeFormat = it.endTime.toTimeDisplay()
                it.timeFormat = formatSalonSchedule(requireContext(), it)
            }
            signUpForm.schedules = listSchedule.toMutableList()
            scheduleAdapter.submit(listSchedule)
        }
        binding.salonServiceView.postDelayed(
            {
                binding.salonServiceView.resetView()
                binding.salonStaffView.resetView()
            }, 50
        )

    }

    private fun onPlaceSelected(place: Place) {
        binding.edtSalonAddress.setText(place.address.toString())
        val geocoder = Geocoder(requireContext())
        val listAddress = geocoder.getFromLocation(place.latLng.latitude, place.latLng.longitude, 1)

        viewModel.apply {
            salonForm.run {
                salon_lat = place.latLng.latitude
                salon_lng = place.latLng.longitude
                if (listAddress!!.isNotEmpty()) {
                    listAddress[0]!!.apply {
                        salon_country = this.countryCode ?: salon_country
                        salon_zipcode = this.postalCode ?: salon_zipcode
                        salon_state = this.adminArea ?: salon_state
                        salon_city = this.locality ?: this.subAdminArea ?: salon_city
                    }
                }
            }

            timeZoneForm.run {
                key = getString(R.string.google_api_key)
                location = "${place.latLng.latitude},${place.latLng.longitude}"
            }
            getTimeZone()
        }
    }

    private fun addService(service: ISalonService) {
        val manicuristService = SalonServiceView(requireContext())
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            resources.getDimension(R.dimen.size_50).roundToInt()
        )
        params.setMargins(0, 35, 0, 0)
        binding.layoutAddService.addView(manicuristService, params)
        manicuristService.displayType = DisplayType.DisplayService
        manicuristService.service = service
        manicuristService.tag = service
        manicuristService.onCLickImage = {
            open<ShowZoomSingleImageActivity>(Routing.ShowZoomSingleImage(it))
        }
        manicuristService.onClickDelete = { view: View? ->
            if (manicuristService.parent != null) {
                (manicuristService.parent as LinearLayout).removeView(manicuristService)
            }
            signUpForm.services.remove(manicuristService.tag as ISalonService)
        }
        manicuristService.parent.requestChildFocus(manicuristService, manicuristService)
    }

    private fun addStaff(staff: ISalonStaff) {
        val staffView = SalonStaffView(requireContext())
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            resources.getDimension(R.dimen.size_50).roundToInt()
        )
        params.setMargins(0, 15, 0, 0)
        binding.layoutAddStaff.addView(staffView, params)
        staffView.displayType = DisplayType.DisplayService
        staffView.onCLickImage = {
            open<ShowZoomSingleImageActivity>(Routing.ShowZoomSingleImage(it))
        }
        staffView.staffData = staff
        staffView.tag = staff
        staffView.onClickDelete = { view: View? ->
            if (staffView.parent != null) {
                (staffView.parent as LinearLayout).removeView(staffView)
            }
            signUpForm.staffs.remove(staffView.tag as ISalonStaff)
        }
        staffView.parent.requestChildFocus(staffView, staffView)
    }
}

private fun formatSalonSchedule(context: Context, it: ISchedule): String {
    return if (it.startTimeFormat.isNullOrEmpty() || it.startTimeFormat.isNullOrEmpty()) context.getString(
        R.string.not_open
    )
    else "${it.startTimeFormat} - ${it.endTimeFormat}"
}

class SignUpVM(
    private val signUpRepo: SignUpRepo,
    private val fetchTimeZone: FetchTimeZone
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    init {
        getValueServiceDefault()
    }

    val salonForm = SignUpForm()
    val timeZoneForm = TimeZoneForm()

    val timeZoneResult = fetchTimeZone.result
    val signUpResult = signUpRepo.result
    val servicePriceDefaultResult = signUpRepo.resultServicePriceDefault

    fun getTimeZone() = launch(loading, error) {
        fetchTimeZone(timeZoneForm)
    }

    private fun getValueServiceDefault() = launch(loading, error) {
        signUpRepo.getValueServiceDefault()
    }

    fun submit() = launch(loading, error) {
        signUpRepo.uploadServiceImages(salonForm.services).forEachIndexed { index, s ->
            salonForm.services[index].avatar = s
        }
        signUpRepo.uploadStaffImages(salonForm.staffs).forEachIndexed { index, s ->
            salonForm.staffs[index].avatar = s
        }
        signUpRepo(salonForm)
    }
}

@Inject(ShareScope.Fragment)
class SignUpRepo(
    val context: Context,
    private val meApi: MeApi,
    private val userLocalSource: UserLocalSource,
    private val appCache: AppCache
) {
    val result = MutableLiveData<UserDTO>()
    val resultServicePriceDefault = MutableLiveData<String>()

    suspend fun uploadServiceImages(list: List<ISalonService>): ArrayList<String> {
        val imageParts =
            list.filter { !it.imageUri.contains("http") }.mapIndexed { index, uriLink ->
                context.getFilePath(uriLink.imageUri.toUri())!!.scalePhotoLibrary(context)
                    .toImagePart("images[$index]")
            }.toTypedArray()

        return meApi.uploadMultipleImage(
            RequestBodyBuilder()
                .put("type", 2).buildMultipart(),
            images = imageParts
        ).await()
    }

    suspend fun uploadStaffImages(list: List<ISalonStaff>): ArrayList<String> {
        val imageParts =
            list.filter { !it.imageUri.contains("http") }.mapIndexed { index, uriLink ->
                context.getFilePath(uriLink.imageUri.toUri())!!.scalePhotoLibrary(context)
                    .toImagePart("images[$index]")
            }.toTypedArray()

        return meApi.uploadMultipleImage(
            RequestBodyBuilder()
                .put("type", 1).buildMultipart(),
            images = imageParts
        ).await()
    }

    suspend operator fun invoke(form: SignUpForm) {
        form.validate()
        val imageParts =
            form.images.filter { !it.path.contains("http") }.mapIndexed { index, uriLink ->
                context.getFilePath(uriLink.path.toUri())!!.scalePhotoLibrary(context)
                    .toImagePart("images")
            }.toTypedArray()
        result.post(
            meApi.signUp(
                RequestBodyBuilder()
                    .put("phone", form.phone)
                    .put("password", form.password)
                    .put("salon_name", form.salon_name)
                    .put("salon_phone", form.salon_phone)
                    .put("salon_email", form.salon_email)
                    .put("salon_address", form.salon_address)
                    .put("salon_state", form.salon_state)
                    .put("salon_city", form.salon_city)
                    .put("salon_zipcode", form.salon_zipcode)
                    .put("salon_country", form.salon_country)
                    .put("schedules", form.schedules.toString())
                    .put("staffs", Gson().toJson(form.staffs))
                    .put("services", Gson().toJson(form.services))
                    .put("salon_tz", form.salon_tz)
                    .put("salon_timezone", "UTC")
                    .put("salon_lat", form.salon_lat)
                    .put("salon_lng", form.salon_lng)
                    .put("salon_description", form.salon_description)
                    .put("device_token", appCache.deviceToken)
                    .put("device_type", "android")
                    .put("name", form.admin_name)
                    .put("lang", userLocalSource.getLanguageWithDefault())
                    .buildMultipart(),
                images = imageParts
            ).await().apply {
                userLocalSource.saveUser(this)
                userLocalSource.saveToken(this.token)
            })

    }

    suspend fun getValueServiceDefault() {
        resultServicePriceDefault.post(
            meApi.getValueServiceDefault().await().value
        )
    }
}