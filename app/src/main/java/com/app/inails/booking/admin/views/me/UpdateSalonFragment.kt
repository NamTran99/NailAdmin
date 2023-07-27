package com.app.inails.booking.admin.views.me

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.*
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentUpdateSalonBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.GoogleApi
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.exception.toDateLocalCustom
import com.app.inails.booking.admin.exception.toDateUTC
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.factory.SalonFactory
import com.app.inails.booking.admin.factory.TimeZoneFactory
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.model.response.AppImage
import com.app.inails.booking.admin.model.response.TimeZoneForm
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToApplyVoucherSalon
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.utils.TimeUtils
import com.app.inails.booking.admin.views.booking.dialog.VoucherDetailDialogOwner
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.me.EditScheduleFragment.Companion.REQUEST_KEY
import com.app.inails.booking.admin.views.me.adapters.SalonScheduleAdapter
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoAdapter
import com.app.inails.booking.admin.views.me.adapters.VoucherAdapter
import com.app.inails.booking.admin.views.widget.topbar.ExtensionButton
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.libraries.places.api.model.Place
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import retrofit2.await


class UpdateSalonFragment : BaseFragment(R.layout.fragment_update_salon), TopBarOwner,
    MessageDialogOwner, VoucherDetailDialogOwner {
    val viewModel by viewModel<UpdateSalonViewModel>()
    val binding by viewBinding(FragmentUpdateSalonBinding::bind)
    private lateinit var imageAdapter: UploadPhotoAdapter
    private lateinit var scheduleAdapter: SalonScheduleAdapter
    private lateinit var voucherAdapter: VoucherAdapter

    var pathServerImage = ArrayList<AppImage>()
    var pathLocalImage = ArrayList<AppImage>()
    var allImage = ArrayList<AppImage>()

    var firstTimeOpen = true

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val pathImage =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf<Uri>()
                pathLocalImage.clear()
                pathLocalImage.addAll(pathImage.filter { !it.toString().contains("http") }
                    .map { pathUri -> AppImage(image = pathUri.toString()) })
                allImage.clear()
                allImage.addAll(pathServerImage)
                allImage.addAll(pathLocalImage)
                imageAdapter.changePath(allImage)
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_update_salon_information,
                onBackClick = {
                    confirmDialog.show(
                        title = getString(R.string.tittle_exit_update_salon),
                        message = getString(R.string.message_exit),
                        function = {
                            activity?.onBackPressed()
                        }
                    )
                },
                extensionButton = ExtensionButton(
                    isShow = true,
                    onclick = {
                        with(binding) {
                            viewModel.salonForm.apply {
                                allImageCount = allImage.size
                                name = etSalonName.text.toString()
                                owner_name = etOwnerName.text.toString()
                                address = etAddress.text.toString()
                                phone = etPhone.text.toString()
                                images = allImage.toList()
                                description = etDescription.text.toString()
                                schedules = viewModel.listSchedules.map {
                                    ScheduleForm(
                                        day = it.day,
                                        startTime = it.startTime.convertTime(fromZoneID = zoneID),
                                        endTime = it.endTime.convertTime(fromZoneID = zoneID)
                                    )
                                }
                            }
                            viewModel.updateSalon()
                        }
                    },
                    content = R.string.btn_save
                )
            )
        )

        with(binding) {
            voucherDetailDialog.onClickDeleteVoucher = {
                viewModel.deleteVoucher(it)
            }
            voucherAdapter = VoucherAdapter(rcVoucher).apply {
                onItemCLick = {
                    voucherDetailDialog.show(it, true)
                }
            }
            btnAddVoucher.onClick {
                redirectToApplyVoucherSalon(Routing.VoucherApply.apply {
                    listOfCode = listCode
                })
            }
            tvBusinessHour.text = viewModel.salonForm.fullTimeZoneDisplay1
            etPhone.inputTypePhoneUS()
            scheduleAdapter = SalonScheduleAdapter(rcvSchedule)
            imageAdapter = UploadPhotoAdapter(rvImages).apply {
                onAddImagesAction = {
                    FishBun.with(this@UpdateSalonFragment)
                        .setImageAdapter(GlideAdapter())
                        .setMaxCount(20)
                        .setMinCount(1)
                        .setSelectedImages(ArrayList(allImage.map { it.image.toUri() }))
                        .setActionBarColor(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            true
                        )
                        .setActionBarTitleColor(Color.parseColor("#ffffff"))
                        .startAlbumWithActivityResultCallback(startForResult)
                }

                onRemoveImageAction = {
                    if (it.id == null) {
                        pathLocalImage.remove(it)
                    } else {
                        viewModel.salonForm.deleteImage.add(it.id)
                        pathServerImage.remove(it)
                    }
                    allImage.remove(it)
                }
                changePath(allImage)
            }

            tvEditSchedule.onClick {
                val editScheduleArgs = EditScheduleArgs(
                    viewModel.listSchedules,
                    "${viewModel.salonForm.zoneID} ${viewModel.salonForm.offsetDisplay}"
                )
                Router.run { redirectToEditScheduleSalon(editScheduleArgs) }
            }
            etAddress.setOnClickListener {
                appSettings.openPlaceAutoComplete("", ::onPlaceSelected)
            }
        }

        with(viewModel) {
            timeZoneResult.bind {
                val oldTimezone = salonForm.fullTimeZoneDisplay2
                salonForm.run {
                    setUpDataTimeZone(TimeUtils.getTimeOffset(it.timeZoneId), it.timeZoneId)
                }
                binding.tvBusinessHour.text = salonForm.fullTimeZoneDisplay1

                if (oldTimezone != salonForm.fullTimeZoneDisplay2) {
                    messageDialog.show(
                        R.string.notice,
                        getString(
                            R.string.change_time_zone,
                            oldTimezone,
                            salonForm.fullTimeZoneDisplay2
                        )
                    )
                }
            }
            scheduleAdapter.submit(listSchedules)
            salonDetail.bind(::displays)
            updateSalonStatus.bind {
                success(R.string.update_success)
                activity?.onBackPressed()
            }

        }

        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY, viewLifecycleOwner
        ) { requestKey, result ->
            val listSchedule = result.getSerializable("schedules") as List<ISchedule>
            listSchedule.forEach {
                it.startTimeFormat = it.startTime.toTimeDisplay()
                it.endTimeFormat = it.endTime.toTimeDisplay()
                it.timeFormat = formatSalonSchedule(it)
            }
            viewModel.listSchedules = listSchedule
            scheduleAdapter.submit(listSchedule)
        }

        if (firstTimeOpen) {
            refreshView()
            firstTimeOpen = false
        }
    }

    private fun onPlaceSelected(place: Place) {
        binding.etAddress.setText(place.address?.toString())
        val geocoder = Geocoder(requireContext())
        val listAddress = geocoder.getFromLocation(place.latLng.latitude, place.latLng.longitude, 1)

        viewModel.apply {
            salonForm.run {
                lat = place.latLng!!.latitude
                long = place.latLng!!.longitude
                if (listAddress!!.isNotEmpty()) {
                    listAddress[0]!!.apply {
                        country = this.countryCode ?: country
                        zipCode = this.postalCode ?: zipCode
                        state = this.adminArea ?: state
                        city = this.locality ?: this.subAdminArea ?: city
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

    private fun formatSalonSchedule(it: ISchedule): String {
        return if (it.startTimeFormat.isNullOrEmpty() || it.startTimeFormat.isNullOrEmpty()) requireContext().getString(
            R.string.not_open
        )
        else "${it.startTimeFormat} - ${it.endTimeFormat}"
    }

    private fun displays(item: ISalonDetail) = with(binding) {
        etOwnerName.setText(item.ownerName)
        etSalonName.setText(item.salonName)
        etPhone.setText(item.phoneNumber)
        etAddress.setText(item.address)
        tvBusinessHour.text = item.tzDisplay1
        etDescription.setText(item.des)
        if (pathServerImage.isEmpty()) {
            pathServerImage.addAll(ArrayList(item.images))
            allImage.addAll(pathServerImage)
            imageAdapter.changePath(allImage)
        }
        scheduleAdapter.submit(item.schedules)
        viewModel.listSchedules = item.schedules ?: listOf()

        // Save Old Data
        viewModel.salonForm.run {
            fullTimeZoneDisplay2 = item.tzDisplay2
            zoneID = item.zoneID
            offsetDisplay = item.zoneOffSet
            fullTimeZoneDisplay1 = item.tzDisplay1
            id = item.salonID.toInt()
            lat = item.lat.toDouble()
            long = item.lng.toDouble()
            email = item.email
            state = item.state
            zipCode = item.zipCode
            country = item.country
            city = item.city
        }
    }

    private fun refreshView() {
        viewModel.getDetailSalon()
        viewModel.getListVoucher()
    }

    var listCode = listOf<String>()

    override fun onResume() {
        super.onResume()
        viewModel.voucherResult.bind {
            listCode = it.map { voucher -> voucher.code }
            voucherAdapter.submit(it)
        }
        appEvent.voucherApply.bind {
            it?.let {
                viewModel.addVoucher(it)
            }
        }
    }
}

class UpdateSalonViewModel(
    private val profileRepository: ProfileRepository,
    private val updateSalonRepository: UpdateSalonRepository,
    private val fetchTimeZone: FetchTimeZone,
    val addVoucherRepo: VoucherRepo,
    val context: Context
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val salonForm = SalonForm()
    val timeZoneForm = TimeZoneForm()
    val timeZoneResult = fetchTimeZone.result
    val salonDetail = profileRepository.result
    val updateSalonStatus = updateSalonRepository.result
    val listVoucherDeleteID = mutableListOf<Int>()
    var listSchedules = listOf<ISchedule>() /// use for send to EditSchedules Fragment
    private val voucherAdd = MutableLiveData<MutableList<VoucherForm>>(mutableListOf())

    val voucherResult: LiveData<List<IVoucher>> =
        combine(updateSalonRepository.voucherResult, voucherAdd) { list1, list2 ->
            val listVoucher = mutableListOf<IVoucher>()
            listVoucher.addAll(list2)
            listVoucher.addAll(list1)
            listVoucher.toList()
        }

    fun deleteVoucher(voucher: IVoucher) {
        if (voucher.id == -1) {
            voucherAdd.value?.remove(voucher)
            voucherAdd.forceRefresh()
        } else {
            updateSalonRepository.voucherResult.value?.remove(voucher)
            updateSalonRepository.voucherResult.forceRefresh()
            listVoucherDeleteID.add(voucher.id)
        }
    }

    fun addVoucher(voucher: VoucherForm) {
        voucher.startDate = voucher.startDate.toDateLocalCustom()
        voucher.endDate = voucher.endDate.toDateLocalCustom()
        voucherAdd.value?.add(voucher)
        voucherAdd.forceRefresh()
    }

    fun getListVoucher() = launch(loading, error) {
        updateSalonRepository.getListVoucher()
    }

    fun getDetailSalon() = launch(loading, error) {
        profileRepository()
    }

    fun updateSalon() = launch(loading, error) {
        addVoucherRepo.deleteMultiVoucher(listVoucherDeleteID.toString())
        updateSalonRepository(salonForm)
        voucherAdd.value?.forEach {
            addVoucherRepo(it)
        }
    }

    fun getTimeZone() = launch(loading, error) {
        fetchTimeZone(timeZoneForm)
    }
}

@Inject(ShareScope.Fragment)
class UpdateSalonRepository(
    private val meApi: MeApi,
    private val textFormatter: TextFormatter,
    private val salonFactory: SalonFactory,
    private val localSource: UserLocalSource,
    val context: Context
) {
    val result = MutableLiveData<Any>()
    val voucherResult = MutableLiveData<MutableList<IVoucher>>()

    suspend fun getListVoucher() {
        voucherResult.post(
            meApi.getListVoucher(salonID =localSource.getSalonID()?:0).await().map {
                SalonFactory.createVoucher(it, context)
            }.sortedByDescending { it.status }.reversed().toMutableList()
        )
    }

    suspend operator fun invoke(salonForm: SalonForm) {
        salonForm.validate()
        val imageParts =
            salonForm.images.filter { !it.image.contains("http") }.mapIndexed { index, uriLink ->
                context.getFilePath(uriLink.image.toUri())!!.scalePhotoLibrary(context)
                    .toImagePart("images")
            }.toTypedArray()
        result.post(
            salonFactory.createDetail(meApi.updateSalon(
                RequestBodyBuilder()
                    .put("id", salonForm.id)
                    .put("name", salonForm.name)
                    .put("owner_name", salonForm.owner_name)
                    .put("phone", textFormatter.formatPhoneNumber(salonForm.phone))
                    .put("state", salonForm.state)
                    .put("city", salonForm.city)
                    .put("address", salonForm.address)
                    .put("zipcode", salonForm.zipCode)
                    .put("country", salonForm.country)
                    .put("description", salonForm.description)
                    .put("schedules", salonForm.schedules.toString())
                    .put("delete_images", salonForm.deleteImage)
                    .put("tz", salonForm.offsetDisplay)
                    .put("timezone", salonForm.zoneID)
                    .put("lat", salonForm.lat)
                    .put("lng", salonForm.long)
                    .buildMultipart(),
                images = imageParts
            ).await().apply {
                localSource.changeSalonName(name)
            })
        )
    }
}

@Inject(ShareScope.Fragment)
class FetchTimeZone(
    private val googleApi: GoogleApi,
    private val timeZoneFactory: TimeZoneFactory,
) {
    val result = MutableLiveData<ITimeZone>()
    suspend operator fun invoke(timeZone: TimeZoneForm) {
        result.post(
            timeZoneFactory.createTimeZone(
                googleApi.getTimeZone(
                    location = timeZone.location,
                    timestamp = timeZone.timestamp,
                    key = timeZone.key
                ).await()
            )
        )
    }
}

@Inject(ShareScope.Fragment)
class VoucherRepo(
    private val meApi: MeApi,
) {
    val addVoucherResult = SingleLiveEvent<Any>()
    val deleteVoucherResult = SingleLiveEvent<Any>()
    suspend operator fun invoke(voucherForm: VoucherForm) {
        voucherForm.startDate = voucherForm.startDate.toDateUTC()
        voucherForm.endDate = voucherForm.endDate.toDateUTC()
        voucherForm.validate()
        addVoucherResult.post(
            meApi.addVoucher(voucherForm).await()
        )
    }

    suspend fun deleteMultiVoucher(listID: String) {
        deleteVoucherResult.post(
            meApi.deleteMultiVoucher(listID).await()
        )
    }
}

