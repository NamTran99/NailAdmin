package com.app.inails.booking.admin.views.me

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.BuildConfig
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentJobProfileBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.helper.firebase.FirebaseType
import com.app.inails.booking.admin.helper.firebase.generateSharingLink
import com.app.inails.booking.admin.model.ui.IJobProfile
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.navigate
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToShowZoomImage1
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.job.JobRepository
import com.app.inails.booking.admin.views.base.AppImagesAdapter
import com.app.inails.booking.admin.views.extension.LocalImage
import com.app.inails.booking.admin.views.extension.ShowZoomImageArgs1
import com.app.inails.booking.admin.views.management.findstaff.adapter.DisplaySkillSetAdapter
import com.app.inails.booking.admin.views.widget.topbar.MainTopBarState
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager

class JobProfileFragment : BaseRefreshFragment(R.layout.fragment_job_profile), TopBarOwner {

    private val binding by viewBinding(FragmentJobProfileBinding::bind)
    private val viewModel by viewModel<JobProfileVM>()
    override fun onRefreshListener() {
        viewModel.getJobDetail()
    }

    var isStatusClick = false
    private lateinit var adapterImage: AppImagesAdapter
    private lateinit var displaySkillSetAdapter: DisplaySkillSetAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        topBar.setState(SimpleTopBarState(R.string.mn_my_cv, onBackClick = {
//            onBackPress()
//        }))
        topBar.state<MainTopBarState>().setTitle(R.string.mn_my_cv)
        with(binding) {
            btCreateJob.onClick {
                Router.run {
                    redirectToCreateJobProfile()
                }
            }
            binding.lvDetail.apply {
                displaySkillSetAdapter = DisplaySkillSetAdapter(rvSkillSet)
                rvSkillSet.layoutManager = FlexboxLayoutManager(requireContext()).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                }
                switchStatus.onClick {
                    isStatusClick = true
                }
                switchStatus.setOnCheckedChangeListener { _, isChecked ->
                    switchStatus.post {
                        if (isStatusClick) {
                            viewModel.changeStatus(if (isChecked) 1 else 0)
                            isStatusClick = false
                        }
                    }
                }
                btEdit.onClick {
                    Router.run {
                        redirectToEditJobProfile()
                    }
                }
            }

            with(viewModel) {
                loading.bind {
                    showLoadingRefresh(it)
                }
                jobDetail.bind {
                    lvDetail.layout.show(it != null)
                    lvEmptyData.show(it == null)
                    if (it == null) {
                        listOf(
                            tv1,
                            animationView,
                            tv2,
                            tv3,
                            tv4
                        ).moveViewFromRight(requireActivity().windowManager, duration = 500)
                        lv1.moveViewFromBottom(mWindowManager, duration = 500)
                    } else {
                        lvDetail.apply {
                            listOf(lv2, lv3).moveViewFromRight(
                                requireActivity().windowManager,
                                duration = 500
                            )
                            lv1.moveViewFromTop(mWindowManager, duration = 500)
                        }
                    }
                    it?.let {
                        display(it)
                    }
                }
                success.bind {
                    success(it)
                }
            }
        }
    }

    private fun display(item: IJobProfile) {
        binding.lvDetail.apply {
            displaySkillSetAdapter.submit(item.skills)
            lvSkillSet.show(item.skills.isNotEmpty())
            btShare.onClick {
                generateSharingLink(
                    FirebaseType.staff,
                    item.id.toString(),
                    imageLink = (item.avatar.ifEmpty { null })?.toUri(),
                    getShareableLink = {
                        shareDeepLink(
                            requireContext().getString(
                                R.string.job_profile_sender_format,
                                item.name,
                                item.experienceFormat,
                                item.skillsFormat,
                                item.description.displaySafe(requireContext()), it, BuildConfig.versionCustom
                            )
                        )
                    })
            }
            adapterImage = AppImagesAdapter(rvImages).apply {
                onItemImageClick = {
                    redirectToShowZoomImage1(
                        ShowZoomImageArgs1(item.images.toLocalImage1(), it)
                    )
                }
            }
            imgAvatar.onClick {
                redirectToShowZoomImage1(
                    ShowZoomImageArgs1(listOf(LocalImage(path = item.avatar)), 0)
                )
            }
            adapterImage.submit(item.images)
            rvImages.show(item.images.isNotEmpty())
            imgAvatar.setImageUrl(item.avatar)
            tvPhone.text = item.phone
            tvAddress.text = item.workingArea
//            tvWorkingArea.text = item.workingArea
            tvGender.text = item.genderName
            tvExperience.text = item.experienceFormat
            tvDescription.text = item.description
            tvWorkplaceType.text = item.workingPlaceTypeFormat
//            tvSalary.text = item.salaryDisplay
            tvName.text = item.name
            lvDes.show(item.isShowDes)
            switchStatus.isChecked = item.isStatusPublic
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getJobDetail()
    }
}

class JobProfileVM(private val jobRepo: JobRepository) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {

    val jobDetail = jobRepo.detailResult
    val success = SingleLiveEvent<Int>()

    fun getJobDetail() = launch(loading, error) {
        jobRepo.getDetailProfile()
    }

    fun changeStatus(status: Int) = launch(loading, error) {
        jobRepo.changeStatus(status)
        success.post(if (status == 1) R.string.success_public_job else R.string.success_unpublic_job)
    }
}