package com.app.inails.booking.admin.views.me.signup.steps

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.core.livedata.forceRefresh
import android.support.core.view.viewBinding
import android.support.viewmodel.shareViewModel
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentStepThreeBinding
import com.app.inails.booking.admin.extention.getTextString
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.response.AppImage

import com.app.inails.booking.admin.model.ui.SignUpForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoForPaidAdapter
import com.app.inails.booking.admin.views.me.dialogs.SignUpSuccessDialogOwner
import com.app.inails.booking.admin.views.me.signup.SignUpVM
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter

class StepThreeFragment : BaseFragment(R.layout.fragment_step_three), TopBarOwner,
    MessageDialogOwner, SignUpSuccessDialogOwner {
    val viewModel by shareViewModel<SignUpVM>()
    val binding by viewBinding(FragmentStepThreeBinding::bind)
    private lateinit var imageAdapter: UploadPhotoForPaidAdapter
    val signUpForm: SignUpForm
        get() = viewModel.salonForm

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val pathImage =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf<Uri>()
                imageAdapter.submit(pathImage.map { pathUri -> AppImage(image = pathUri.toString()) }
                    .apply {
                        signUpForm.images = this.toMutableList()
                    })
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_sign_up,
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
            btAddSalonImage.onClick{
                FishBun.with(requireActivity())
                    .setImageAdapter(GlideAdapter())
                    .setMaxCount(20)
                    .setMinCount(1)
                    .setSelectedImages(ArrayList(signUpForm.images.map { it.image.toUri() }))
                    .setActionBarColor(
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                        true
                    )
                    .setActionBarTitleColor(Color.parseColor("#ffffff"))
                    .startAlbumWithActivityResultCallback(startForResult)
            }

            imageAdapter = UploadPhotoForPaidAdapter(rvImages).apply {
                onItemClickListener = {
                    Router.open(self, Routing.PhotoViewer(it))
                }

                onRemoveImageAction = {
                    signUpForm.images.remove(it)
                }
            }

            btnContinue.onClick{
                signUpForm.salon_description = edtDescription.getTextString()
                viewModel.nextStep.forceRefresh()
            }
            btSkip.onClick{
                viewModel.nextStep.forceRefresh()
            }
            btnBack.onClick{
                viewModel.backStep.forceRefresh()
            }
        }

        viewModel.apply {
            imageAdapter.submit(signUpForm.images)
        }
    }
}
