package com.app.inails.booking.admin.views.me.signup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.shareViewModel
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentSignUpSupportBinding
import com.app.inails.booking.admin.datasource.remote.MeApi
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.exception.viewError
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.helper.RequestBodyBuilder
import com.app.inails.booking.admin.model.response.AppImage
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoForPaidAdapter
import com.app.inails.booking.admin.views.me.dialogs.SignUpSuccessDialogOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter

class SignUpSupportFragment : BaseFragment(R.layout.fragment_sign_up_support), TopBarOwner,
    MessageDialogOwner, SignUpSuccessDialogOwner {
    val binding by viewBinding(FragmentSignUpSupportBinding::bind)
    val vm by shareViewModel<SignUpSupportVM>()
    var paidMenuImage = ArrayList<String>()
    private lateinit var imagePaidAdapter: UploadPhotoForPaidAdapter
    private val paidImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val pathImage =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf<Uri>()
                imagePaidAdapter.submit(pathImage.map { pathUri -> AppImage(image = pathUri.toString()) }
                    .apply {
                        paidMenuImage.clear()
                        paidMenuImage.addAll(map { it.image })
                    })
            }
        }

    @SuppressLint("StringFormatInvalid")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_sign_up_support,
                onBackClick = {
                    confirmDialog.show(
                        title = getString(R.string.notice),
                        message = getString(R.string.message_exit),
                        function = {
                            activity?.onBackPressed()
                        }
                    )
                },
            )
        )


        binding.apply {
            imagePaidAdapter = UploadPhotoForPaidAdapter(rcImages).apply {
                onRemoveImageAction = {
                    paidMenuImage.remove(it.image)
                }
                onItemClickListener = {
                    Router.open(self, Routing.PhotoViewer(it))
                }
            }
            btAddImage.onClick {
                FishBun.with(requireActivity())
                    .setImageAdapter(GlideAdapter())
                    .setMaxCount(10)
                    .setMinCount(1)
                    .setSelectedImages(ArrayList(paidMenuImage.map { it.toUri() }))
                    .setActionBarColor(
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                        true
                    )
                    .setActionBarTitleColor(Color.parseColor("#ffffff"))
                    .startAlbumWithActivityResultCallback(paidImageResult)

            }
            btnSubmit.onClick {
                vm.submit(paidMenuImage, etContact.getTextString().trim())
            }
        }
        vm.apply {
            resultSuccess.bind {
                messageDialog.show(R.string.notice, R.string.support_signup_success) {
                    Router.run {
                        redirectToLogin()
                    }
                }
            }
        }
    }
}

class SignUpSupportVM(
    private val signUpRepo: SignUpSupportRepo,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val resultSuccess = signUpRepo.result

    fun submit(listImage: List<String>, contact: String) = launch(loading, error) {
        signUpRepo.signUpSupport(listImage, contact.trim())
    }
}

@Inject(ShareScope.Fragment)
class SignUpSupportRepo(
    val context: Context,
    private val meApi: MeApi,

    ) {
    val result = SingleLiveEvent<Any>()
    suspend fun signUpSupport(listImage: List<String>, contact: String) {
        if(contact.isNotBlank()){
            if(contact.isNumber()){
                if(contact.convertPhoneToNormalFormat().length < 10){
                    viewError(R.id.etContact, R.string.error_type_phone_not_enough)
                }
            }else{
                if(!contact.isEmail()){
                    viewError(R.id.etContact, R.string.error_not_correct_email)
                }
            }
        }

        if (listImage.isEmpty() && contact.isBlank()) {
            resourceError(R.string.error_data_signup_support_not_valid)
        }
        val imageParts =
            listImage.filter { !it.contains("http") }.mapIndexed { index, uriLink ->
                context.getFilePath(uriLink.toUri())!!.scalePhotoLibrary(context)
                    .toImagePart("images")
            }.toTypedArray()
        result.post(
            meApi.signUpSupport(
                RequestBodyBuilder()
                    .put("contact", contact).buildMultipart(),
                *imageParts
            ).await()
        )
    }
}