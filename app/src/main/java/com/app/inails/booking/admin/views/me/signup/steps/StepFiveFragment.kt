package com.app.inails.booking.admin.views.me.signup.steps

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.core.livedata.forceRefresh
import android.support.core.route.clear
import android.support.core.route.open
import android.support.core.view.viewBinding
import android.support.viewmodel.shareViewModel
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentStepFiveBinding
import com.app.inails.booking.admin.extention.hide
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.response.AppImage

import com.app.inails.booking.admin.model.ui.SalonService
import com.app.inails.booking.admin.model.ui.SalonStaff
import com.app.inails.booking.admin.model.ui.SignUpForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.main.MainActivity
import com.app.inails.booking.admin.views.me.adapters.UploadPhotoForPaidAdapter
import com.app.inails.booking.admin.views.me.dialogs.SignUpSuccessDialogOwner
import com.app.inails.booking.admin.views.me.signup.SignUpVM
import com.app.inails.booking.admin.views.widget.DisplayType
import com.app.inails.booking.admin.views.widget.SalonServiceView
import com.app.inails.booking.admin.views.widget.SalonStaffView
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter

class StepFiveFragment : BaseFragment(R.layout.fragment_step_five), TopBarOwner,
    MessageDialogOwner, SignUpSuccessDialogOwner {
    val viewModel by shareViewModel<SignUpVM>()
    val binding by viewBinding(FragmentStepFiveBinding::bind)
    val signUpForm: SignUpForm
        get() = viewModel.salonForm

    var staffImage = ArrayList<String>()
    var serviceImage = ArrayList<String>()

    private lateinit var imagePaidAdapter: UploadPhotoForPaidAdapter

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

    private val paidImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                val pathImage =
                    it.data?.getParcelableArrayListExtra(FishBun.INTENT_PATH) ?: arrayListOf<Uri>()
                imagePaidAdapter.submit(pathImage.map { pathUri -> AppImage(image = pathUri.toString()) }
                    .apply {
                        signUpForm.paidMenuImages = this.toMutableList()
                    })
            }
        }

    @SuppressLint("UseCompatLoadingForDrawables")
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
            tvShowMethod2.onClick {
                tvShowMethod2Hint.show()
                tvShowMethod2.hide()
                lvMethod2.show()
            }
            imagePaidAdapter = UploadPhotoForPaidAdapter(rcPaidImage).apply {
                onRemoveImageAction = {
                    signUpForm.paidMenuImages.remove(it)
                }
                onItemClickListener = {
                    Router.open(self, Routing.PhotoViewer(it))
                }
            }
            btSubmit.onClick {
                viewModel.submit()
            }
            btAddPaidImage.onClick {
                FishBun.with(requireActivity())
                    .setImageAdapter(GlideAdapter())
                    .setMaxCount(10)
                    .setMinCount(1)
                    .setSelectedImages(ArrayList(signUpForm.paidMenuImages.map { it.image.toUri() }))
                    .setActionBarColor(
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                        ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                        true
                    )
                    .setActionBarTitleColor(Color.parseColor("#ffffff"))
                    .startAlbumWithActivityResultCallback(paidImageResult)
            }

            salonStaffView.apply {
                onCLickImage = {
                    FishBun.with(requireActivity())
                        .setImageAdapter(GlideAdapter())
                        .setMaxCount(1)
                        .setSelectedImages(ArrayList(staffImage.map { it.toUri() }))
                        .setActionBarColor(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            true
                        )
                        .setActionBarTitleColor(Color.parseColor("#ffffff"))
                        .startAlbumWithActivityResultCallback(staffImageResult)
                }

                onCLickRemoveImage = {
                    staffImage.clear()
                }
            }

            salonServiceView.apply {
                onCLickImage = {
                    FishBun.with(requireActivity())
                        .setImageAdapter(GlideAdapter())
                        .setMaxCount(1)
                        .setSelectedImages(ArrayList(serviceImage.map { it.toUri() }))
                        .setActionBarColor(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                            true
                        )
                        .setActionBarTitleColor(Color.parseColor("#ffffff"))
                        .startAlbumWithActivityResultCallback(serviceImageResult)
                }
                onCLickRemoveImage = {
                    serviceImage.clear()
                }
            }
            btAddService.onClick {
                if (salonServiceView.checkValidate()) {
                    signUpForm.services.add(salonServiceView.service)
                    addService(salonServiceView.service)
                    serviceImage.clear()
                    salonServiceView.resetView()
                }
            }
            btAddStaff.onClick {
                if (salonStaffView.checkValidate()) {
                    signUpForm.staffs.add(salonStaffView.staffData)
                    addStaff(salonStaffView.staffData)
                    staffImage.clear()
                    salonStaffView.resetView()
                }
            }
            imagePaidAdapter = UploadPhotoForPaidAdapter(rcPaidImage).apply {
                onRemoveImageAction = {
                    signUpForm.paidMenuImages.remove(it)
                }
                onItemClickListener = {
                    Router.open(self, Routing.PhotoViewer(it))
                }
            }
            btnBack.onClick {
                viewModel.backStep.forceRefresh()
            }
        }
        viewModel.apply {
            salonForm.services.forEach {
                addService(it)
            }

            salonForm.staffs.forEach {
                addStaff(it)
            }
        }

        viewModel.apply {
            signUpResult.bind {
                signupDialog.show {
                    open<MainActivity>().clear()
                }
            }
        }


        binding.salonServiceView.postDelayed(
            {
                binding.salonServiceView.resetView()
                binding.salonStaffView.resetView()
            }, 50
        )
    }

    private fun addService(service: SalonService) {
        val manicuristService = SalonServiceView(requireContext())
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        binding.layoutAddService.addView(manicuristService, params)
        manicuristService.displayType = DisplayType.DisplayService
        manicuristService.service = service
        manicuristService.tag = service
        manicuristService.onCLickImage = {
            Router.open(self, Routing.PhotoViewer(it))
        }
        manicuristService.onClickDelete = { view: View? ->
            if (manicuristService.parent != null) {
                (manicuristService.parent as LinearLayout).removeView(manicuristService)
            }
            signUpForm.services.remove(manicuristService.tag as SalonService)
        }
        manicuristService.parent.requestChildFocus(manicuristService, manicuristService)
    }

    private fun addStaff(staff: SalonStaff) {
        val staffView = SalonStaffView(requireContext())
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        binding.layoutAddStaff.addView(staffView, params)
        staffView.displayType = DisplayType.DisplayService
        staffView.onCLickImage = {
            Router.open(self, Routing.PhotoViewer(it))
        }
        staffView.staffData = staff
        staffView.tag = staff
        staffView.onClickDelete = { view: View? ->
            if (staffView.parent != null) {
                (staffView.parent as LinearLayout).removeView(staffView)
            }
            signUpForm.staffs.remove(staffView.tag as SalonStaff)
        }
        staffView.parent.requestChildFocus(staffView, staffView)
    }

}
