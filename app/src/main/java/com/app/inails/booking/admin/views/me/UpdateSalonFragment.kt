package com.app.inails.booking.admin.views.me

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentUpdateSalonBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.factory.SalonFactory
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.utils.TimeUtils
import com.app.inails.booking.admin.views.me.EditScheduleFragment.Companion.REQUEST_KEY
import com.app.inails.booking.admin.views.me.adapters.SalonScheduleAdapter
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.libraries.places.api.model.Place
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class UpdateSalonFragment : BaseFragment(R.layout.fragment_update_salon), TopBarOwner {
    val viewModel by viewModel<UpdateSalonViewModel>()
    val binding by viewBinding(FragmentUpdateSalonBinding::bind)
    private lateinit var imageAdapter: UploadPhotoAdapter
    private lateinit var scheduleAdapter: SalonScheduleAdapter

    var pathServerImage = ArrayList<SalonImage>()
    var pathLocalImage = ArrayList<SalonImage>()
    var allImage = ArrayList<SalonImage>()

    var firstTimeOpen = true

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val pathImage =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf<Uri>()
                pathLocalImage.clear()
                pathLocalImage.addAll(pathImage.map { pathUri -> SalonImage(path = pathUri.toString()) })
                allImage.clear()
                allImage.addAll(pathLocalImage)
//                allImage.addAll(pathServerImage)
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
                    activity?.onBackPressed()
                }
            )
        )

        with(binding) {
            etPhone.inputTypePhoneUS()
            scheduleAdapter = SalonScheduleAdapter(rcvSchedule)
            imageAdapter = UploadPhotoAdapter(rvImages).apply {
                onAddImagesAction = {
                    FishBun.with(this@UpdateSalonFragment)
                        .setImageAdapter(GlideAdapter())
                        .setMaxCount(20)
                        .setMinCount(1)
                        .setSelectedImages(ArrayList(allImage.map { it.path.toUri() }))
                        .setActionBarColor(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            true
                        )
                        .setActionBarTitleColor(Color.parseColor("#ffffff"))
                        .startAlbumWithActivityResultCallback(startForResult)
                }

                onCloseImageAction = {
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

            btSave.onClick {
                viewModel.salonForm.run {
                    id = 11
                    name = etSalonName.text.toString()
                    address = etAddress.text.toString()
                    phone = etPhone.text.toString()
                    images = allImage.toList()
                    description = etDescription.text.toString()
                    zoneID = TimeUtils.getZoneID()
                    offsetDisplay = "UTC ${TimeUtils.getTimeOffset().toNumericString()}"
                    schedules = viewModel.listSchedules.map {
                        ScheduleForm(
                            day = it.day,
                            startTime = it.startTime.convertTime(),
                            endTime = it.endTime.convertTime()
                        )
                    }
                }
                viewModel.updateSalon()
            }

            tvEditSchedule.onClick {
                val editScheduleArgs = EditScheduleArgs(
                    viewModel.listSchedules,
                    viewModel.salonDetail.value?.tzDisplay ?: ""
                )
                Router.run { redirectToEditScheduleSalon(editScheduleArgs) }
            }
            etAddress.setOnClickListener {
                appSettings.openPlaceAutoComplete("", ::onPlaceSelected)
            }
        }

        with(viewModel) {
            scheduleAdapter.submit(listSchedules)
            salonDetail.bind(::displays)
            updateSalonStatus.bind {
                success("Update Successfully")
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
        binding.etAddress.setText(place.address.toString())
        viewModel.salonForm.run {
            lat = place.latLng.latitude
            long = place.latLng.longitude
        }
    }

    private fun formatSalonSchedule(it: ISchedule): String {
        return if (it.startTimeFormat.isNullOrEmpty() || it.startTimeFormat.isNullOrEmpty()) "Not open"
        else "${it.startTimeFormat} - ${it.endTimeFormat}"
    }

    private fun displays(item: ISalonDetail) = with(binding) {
        etSalonName.setText(item.salonName)
        etPhone.setText(item.phoneNumber)
        etAddress.setText(item.address)
        tvBusinessHour.text = item.tzDisplay
        etDescription.setText(item.des)
        if (pathServerImage.isEmpty()) {
            pathServerImage.addAll(ArrayList(item.images))
            allImage.addAll(pathServerImage)
            imageAdapter.changePath(allImage)
        }
        scheduleAdapter.submit(item.schedules)
        viewModel.listSchedules = item.schedules?: listOf()
        viewModel.salonForm.run {
            lat = item.lat.toDouble()
            long = item.lng.toDouble()
        }
    }

    private fun refreshView() {
        viewModel.getDetailSalon()
    }
}

class UpdateSalonViewModel(
    private val profileRepository: ProfileRepository,
    private val updateSalonRepository: UpdateSalonRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val salonForm = SalonForm()

    val salonDetail = profileRepository.result
    val updateSalonStatus = updateSalonRepository.result
    var listSchedules = listOf<ISchedule>() /// user for send to EditSchedules Fragment

    fun getDetailSalon() = launch(loading, error) {
        profileRepository()
    }

    fun updateSalon() = launch(loading, error) {
        updateSalonRepository(salonForm)
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
    suspend operator fun invoke(salonForm: SalonForm) {
        val imageParts =
            salonForm.images.filter { !it.path.contains("http") }.mapIndexed { index, uriLink ->
                context.getFilePath(uriLink.path.toUri())!!.scalePhotoLibrary(context)
                    .toImagePart("images")
            }.toTypedArray()

        result.post(
            salonFactory.createDetail(meApi.updateSalon(
                RequestBodyBuilder()
                    .put("id", salonForm.id)
                    .put("name", salonForm.name)
                    .put("phone", textFormatter.formatPhoneNumber(salonForm.phone))
                    .put("email", "anhtran.it.dev@gmail.com")
                    .put("state", "NJ")
                    .put("city", "Sayreville")
                    .put("address", salonForm.address)
                    .put("zipcode", "80526")
                    .put("country", "US")
                    .put("description", salonForm.description)
                    .put("schedules", salonForm.schedules)
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

fun buildImagePart(name: String, photo: String): MultipartBody.Part {
    val file = File(photo)
    return MultipartBody.Part.createFormData(
        name,
        file.name,
        file.asRequestBody("image/${file.extension}".toMediaType())
    )
}

infix fun String.toImagePart(key: String) = createImagePart(key, this)

fun createImagePart(field: String, url: String?): MultipartBody.Part? {
    if (url == null) return null
    val file = File(url)
    if (!file.exists()) return null
    val type = getMimeType(file.path) ?: return null
    return MultipartBody.Part.createFormData(
        field, file.name,
        RequestBody.create(type.toMediaTypeOrNull(), file)
    )
}

fun getMimeType(url: String): String? {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase())
    }
    return type
}

fun RequestBodyBuilder.buildMultipart(): Map<String, RequestBody> {
    val multipart = HashMap<String, RequestBody>()
    build().forEach { multipart[it.key] = createValuePart(it.value) }
    return multipart
}

private fun createValuePart(value: String): RequestBody {
    return RequestBody.create(MultipartBody.FORM, value)
}