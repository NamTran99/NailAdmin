package com.app.inails.booking.admin.views.me

import android.content.Context
import android.graphics.Color
import android.net.Uri
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentUpdateSalonBinding
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.views.me.EditScheduleFragment.Companion.REQUEST_KEY
import com.app.inails.booking.admin.views.me.adapters.SalonScheduleAdapter
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
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
                pathLocalImage.addAll(pathImage.map { pathUri -> SalonImage(path = pathUri.toString()) })
                allImage.clear()
                allImage.addAll(pathLocalImage)
                allImage.addAll(pathServerImage)
                imageAdapter.changePath(allImage)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_update_salon_information,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        with(binding) {
            scheduleAdapter = SalonScheduleAdapter(rcvSchedule)
            imageAdapter = UploadPhotoAdapter(rvImages).apply {
                onAddImagesAction = {
                    FishBun.with(this@UpdateSalonFragment)
                        .setImageAdapter(GlideAdapter())
                        .setMaxCount(20)
                        .setMinCount(1)
                        .setSelectedImages(ArrayList(pathLocalImage.map { it.path.toUri() }))
                        .setActionBarColor(
                            Color.parseColor("#795548"),
                            Color.parseColor("#5D4037"),
                            false
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
                }
                viewModel.updateSalon()
            }

            tvEditSchedule.onClick {
                Router.run { redirectToEditScheduleSalon() }
            }
        }

        with(viewModel) {
            salonDetail.bind(::displays)
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
            viewModel.salonForm.schedules = listSchedule.map {
                ScheduleForm(
                    day = it.day,
                    startTime = it.startTime.convertTime(),
                    endTime = it.endTime.convertTime()
                )
            }
            scheduleAdapter.submit(listSchedule)
        }
        if (firstTimeOpen) {
            refreshView()
            firstTimeOpen = false
        }
    }

    private fun formatSalonSchedule(it: ISchedule): String {
        return if (it.startTimeFormat.isNullOrEmpty() || it.startTimeFormat.isNullOrEmpty()) "OFF"
        else "${it.startTimeFormat} - ${it.endTimeFormat}"
    }

    private fun displays(item: ISalonDetail) = with(binding) {
        etSalonName.setText(item.salonName)
        etPhone.setText(item.phoneNumber)
        etAddress.setText(item.address)
        etDescription.setText(item.des)
        if (pathServerImage.isEmpty()) {
            pathServerImage.addAll(ArrayList(item.images))
            allImage.addAll(pathServerImage)
            imageAdapter.changePath(allImage)
        }
        scheduleAdapter.submit(item.schedules)
    }

    private fun refreshView() {
        viewModel.getDetailSalon()
    }
}

class UpdateSalonViewModel(
    private val profileRepository: ProfileRepository,
    private val updateSalonRepository: UpdateSalonRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val salonDetail = profileRepository.result
    val salonForm = SalonForm()

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
    val context: Context
) {
    val result = MutableLiveData<Any>()
    suspend operator fun invoke(salonForm: SalonForm) {
        val imageParts = salonForm.images.filter { it.id == null }.mapIndexed { index, uriLink ->
            context.getFilePath(uriLink.path.toUri())!!.scalePhotoLibrary(context)
                .toImagePart("images")
        }.toTypedArray()

        result.post(
            meApi.updateSalon(
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
                    .buildMultipart(),
                images = imageParts
            ).await()
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