package com.app.inails.booking.admin.views.management.findstaff

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.route.argument
import android.support.core.route.close
import android.support.core.route.open
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentDetailCandidateBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.helper.firebase.FirebaseType
import com.app.inails.booking.admin.helper.firebase.generateSharingLink
import com.app.inails.booking.admin.model.ui.IJobProfile
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToDetailCandidate
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToLogin
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToLoginAndReturn
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToMain
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.popups.PopupUserMoreOwner
import com.app.inails.booking.admin.repository.management.RecruitmentRepo
import com.app.inails.booking.admin.views.base.AppImagesAdapter
import com.app.inails.booking.admin.views.extension.LocalImage
import com.app.inails.booking.admin.views.main.MainActivity
import com.app.inails.booking.admin.views.management.findstaff.adapter.DisplaySkillSetAdapter
import com.app.inails.booking.admin.views.management.staff.CreateUpdateStaffOwner
import com.app.inails.booking.admin.views.splash.SplashActivity
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class DetailCandidateFragment : BaseRefreshFragment(R.layout.fragment_detail_candidate),
    TopBarOwner,
    CreateUpdateStaffOwner, PopupUserMoreOwner {
    private val binding by viewBinding(FragmentDetailCandidateBinding::bind)
    private val vm by viewModel<DetailCandidateVM>()
    private val arg by lazy { argument<Routing.DetailCandidate>() }
    private lateinit var adapterImage: AppImagesAdapter
    private lateinit var displaySkillSetAdapter: DisplaySkillSetAdapter

    override fun onRefreshListener() {
        vm.getDetailProfile(arg.id)
    }

    override fun onResume() {
        super.onResume()
        if(!userLocalSource.isLogin()){
            notificationDialog.show(R.string.noti_please_login){
                redirectToLoginAndReturn()
            }
        }else{
            vm.getDetailProfile(arg.id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.mn_detail_candidate,
                onBackClick = {
                    if(arg.dynamic){
                        redirectToMain()
                    }else{
                        activity?.onBackPressed()
                    }
                },
            )
        )

        with(binding) {
            listOf(lv1,lv2).moveViewFromRight(mWindowManager, 500)
            lv3.moveViewFromBottom(mWindowManager, 500)
            displaySkillSetAdapter = DisplaySkillSetAdapter(rvSkillSet)
            rvSkillSet.layoutManager = FlexboxLayoutManager(requireContext() ).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
            }

            with(vm) {
                detailResult.bind(::display)
                loading.bind {
                    showLoadingRefresh(it)
                    loadingDialog.show(it)
                }
            }
        }
    }

    private fun display(item: IJobProfile) {
        binding.apply {
            if(!item.isPublish){
                notificationDialog.show(R.string.noti_job_profile_not_available){
                    redirectToMain()
                }
                return
            }

            btShare.onClick {
                generateSharingLink(
                    type = FirebaseType.staff,
                    id = item.id.toString(),
                    imageLink = (item.avatar.ifEmpty { null })?.toUri()
                ) {
                    shareDeepLink(
                        requireContext().getString(
                            R.string.job_profile_sender_format,
                            item.name,
                            item.experienceFormatEn,
                            item.skillsFormat,
                            item.description.displaySafe(requireContext(), R.string.no_information_en), it
                        )
                    )
                }
            }
            displaySkillSetAdapter.submit(item.skills)
            adapterImage = AppImagesAdapter(rvImages).apply {
                onItemImageClick = {
                    Router.run {
                        open(self, Routing.ShowListZoomImage(item.images.toLocalImage1(), it))
                    }
                }
                submit(item.images)
            }

            imgAvatar.onClick {
                Router.run {
                    open(self, Routing.ShowListZoomImage(listOf(LocalImage(path = item.avatar)), 0))
                }
            }
            btnCall.onClick {
                val intent = Intent(Intent.ACTION_VIEW);
                intent.data = Uri.parse("tel:${item.phone}")
                startActivity(intent)
            }
            btnSms.onClick {
                val intent = Intent(Intent.ACTION_VIEW);
                intent.data = Uri.parse("sms:${item.phone}")
                startActivity(intent)
            }
            imgAvatar.setImageUrl(item.avatar)
            tvPhone.text = item.phone
            tvGender.text = item.genderName
            tvExperience.text = item.experienceFormat
            tvDescription.text = item.description
            tvName.text = item.name
            tvAddress.text = item.workingArea
            tvWorkplaceType.text = item.workplace_type_format_2
            item.isShowDes show lvDes
        }
    }
}

class DetailCandidateVM(
    private val recruitmentRepo: RecruitmentRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val detailResult = recruitmentRepo.detailProfileResult

    fun getDetailProfile(id: Int) = launch(loading, error) {
        recruitmentRepo.getDetailProfile(id)
    }
}
