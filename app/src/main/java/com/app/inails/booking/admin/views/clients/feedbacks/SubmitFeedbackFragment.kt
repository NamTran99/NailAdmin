package com.app.inails.booking.admin.views.clients.feedbacks

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.argument
import android.support.core.route.close
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import android.widget.RatingBar
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentSubmitFeedbackBinding
import com.app.inails.booking.admin.datasource.local.dao.SalonDAO
import com.app.inails.booking.admin.datasource.remote.clients.FeedbackApi
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.model.form.FeedBackForm
import com.app.inails.booking.admin.navigate.Routing
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.registerImagePicker
import com.esafirm.imagepicker.model.Image
import okhttp3.MultipartBody

class SubmitFeedbackFragment : BaseFragment(R.layout.fragment_submit_feedback) {

    private val binding by viewBinding(FragmentSubmitFeedbackBinding::bind)
    private val viewModel by viewModel<FeedBackVM>()
    private val arg by lazy { argument<Routing.FeedBack>() }
    private lateinit var mAdapter: AddImageFeedbackAdapter
    private val imageList = arrayListOf<Image>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.sentSuccess.bind {
            success(it)
            close()
        }
        with(binding) {
            btnFbClose.onClick { close() }
            btnSendFeedback.onClick {
                viewModel.form.apply {
                    rating = ratingBar.rating.toInt()
                    content = edtFbContents.text.toString()
                    appointmentID = arg.bookingID
                    images = mAdapter.getImageSelected()
                }
                viewModel.sendFeedback(arg.salonID)
            }
            ratingBar.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    if (rating < 1.0f)
                        ratingBar.rating = 1.0f
                }

            mAdapter = AddImageFeedbackAdapter(rcvImages)
            val list = arrayListOf<Uri>()
            list.add(Uri.parse(""))
            mAdapter.submit(list)

            val abc = appPermission.accessReadStorage {
                //save sagdsajhdg
            }


            val permissionCamera = appPermission.accessCamera {
                val config = ImagePickerConfig {
                    mode = ImagePickerMode.MULTIPLE // default is multi image mode
                    language = "en" // Set image picker language
                    // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
                    isIncludeVideo = false // include video (false by default)
                    arrowColor = Color.RED // set toolbar arrow up color
                    folderTitle = "Folder" // folder selection title
                    imageTitle = "Tap to select" // image selection title
                    doneButtonText = "DONE" // done button text
                    limit = 10 // max images can be selected (99 by default)
                    isShowCamera = true // show camera or not (true by default)
                    selectedImages = imageList  // original selected images, used in multi mode
                }
                launcher.launch(config)
            }


            mAdapter.onImageSelectedListener = {
                if (it == 0) {
                    permissionCamera.request()

                }
            }

            mAdapter.onRemoveListener = { uri ->
                val index = imageList.findIndex { it.uri == uri }
                if (index > -1) imageList.removeAt(index)
            }
        }
    }

    val launcher = registerImagePicker { result: List<Image> ->
        imageList.clear()
        imageList.addAll(result)
        result.forEach { image ->
            println(image.uri)
            val index = mAdapter.items().getData().findIndex { it == image.uri }
            if (index == -1)
                mAdapter.items().getData().add(image.uri)
        }
        mAdapter.notifyDataSetChanged()
    }
}

class FeedBackVM(private val feedbackRepo: FeedBackRepository) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {

    val form = FeedBackForm()
    val sentSuccess = SingleLiveEvent<Int>()
    fun sendFeedback(salonID: Long) = launch(loading, error) {
        feedbackRepo.invoke(form, salonID)
        sentSuccess.post(R.string.msg_sent_feedback)
    }
}

@Inject(ShareScope.Fragment)
class FeedBackRepository(
    private val feedbackAPI: FeedbackApi,
    private val salonDao: SalonDAO,
    val context: Context
) {

    suspend operator fun invoke(
        form: FeedBackForm,
        salonID: Long
    ) {
        form.validate()
        var imageParts: Array<MultipartBody.Part?>? = null
        if (form.images.size != 0) {

            var list: MutableList<String> = mutableListOf()
            form.images.forEach { element ->
                list.add(context.getFilePath(element)!!)
            }

            list =
                list.mapIndexed { index, s -> s.scalePhotoLibrary(context).path }
                    .toMutableList()

            imageParts =
                list.mapIndexed { index, s -> s.toImagePart("images") }.toTypedArray()

        }

        feedbackAPI.sendFeedback(
            RequestBodyBuilder()
                .put("appointment_id", form.appointmentID.toString())
                .put("rating", form.rating.toString())
                .put("content", form.content)
                .buildMultipart(), imageParts
        ).await()
        salonDao.removeByID(salonID)
    }
}
