package com.app.inails.booking.admin.views.management.findstaff

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.core.net.toUri
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.BuildConfig
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentDetailRecruitmentBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.helper.firebase.FirebaseType
import com.app.inails.booking.admin.helper.firebase.generateSharingLink
import com.app.inails.booking.admin.model.ui.IRecruitment
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToLoginAndReturn
import com.app.inails.booking.admin.navigate.Router.Companion.redirectToMain
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.popups.PopupRecruitmentOwner
import com.app.inails.booking.admin.repository.management.RecruitmentRepo
import com.app.inails.booking.admin.views.base.ImagesViewPagerAdapter2
import com.app.inails.booking.admin.views.management.findstaff.adapter.DisplaySkillSetAdapter
import com.app.inails.booking.admin.views.widget.topbar.ExtensionButton
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.tabs.TabLayoutMediator

class DetailRecruitmentFragment : BaseFragment(R.layout.fragment_detail_recruitment), TopBarOwner,
    PopupRecruitmentOwner {
    private val binding by viewBinding(FragmentDetailRecruitmentBinding::bind)
    private val vm by viewModel<DetailRecruitmentVM>()
    val args by lazy { argument<Routing.DetailRecruitment>() }
    private lateinit var adapter: ImagesViewPagerAdapter2
    private lateinit var skillSetAdapter: DisplaySkillSetAdapter

    override fun onResume() {
        super.onResume()
        if(!userLocalSource.isLogin()){
            notificationDialog.show(R.string.noti_please_login){
                redirectToLoginAndReturn()
            }
        }else{
            vm.getDetailRecruitment(args.id)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.mn_detail_recruitment,
                onBackClick = {
                    if(args.dynamic){
                        redirectToMain()
                    }else{
                        activity?.onBackPressed()
                    }
                },
                extensionButton = ExtensionButton(
                    isShow = true,
                    onclick = {
                        with(binding) {
                            Router.run {
                                redirectToUpdateAds(args.id)
                            }
                        }
                    },
                    content = R.string.btn_edit
                )
            )
        )

        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { vm.getDetailRecruitment(args.id) }

            listOf(lv1,lv2).moveViewFromRight(mWindowManager, 500)
            lvViewPager.moveViewFromTop(mWindowManager, 500)
            imgDefault.moveViewFromTop(mWindowManager, 500)
            lvBot1.moveViewFromBottom(mWindowManager, 500)

            skillSetAdapter = DisplaySkillSetAdapter(rvSkillSet.apply {
                layoutManager = FlexboxLayoutManager(requireContext()).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.CENTER
                }
            })

            adapter = ImagesViewPagerAdapter2(vpImages).apply {
                onItemClick = {
                    if (!adapter.items.isNullOrEmpty()) {
                        Router.run {
                            open(
                                self,
                                Routing.ShowListZoomImage(
                                    (adapter.items as ArrayList<String>).toLocalImage(),
                                    it
                                )
                            )
                        }
                    }
                }
            }

            TabLayoutMediator(tabDots, vpImages) { _, _ ->
            }.attach()

            btPublic.onClick {
                vm.publishRecruitment(1, args.id)
            }

            btUnPublic.onClick {
                vm.publishRecruitment(0, args.id)
            }

            btDelete.onClick {
                confirmDialog.showDelete(R.string.title_delete_post, R.string.message_delete_post) {
                    vm.deleteRecruitment(args.id)
                }
            }

            vm.apply {
                getDetailRecruitment(args.id)
                detailResult.bind(::display)
                loadingCustom.bind {
                    binding.viewRefresh.isRefreshing = it
                }

                actionSuccessResult.bind {
                    success(it)
                }

                actionDeleteSuccess.bind {
                    success(it)
                    onBackPress()
                }
            }
        }
    }

    private fun display(item: IRecruitment) {
        binding.apply {
            if(!item.isPublish && !item.isMyRecruitment && args.dynamic){
                notificationDialog.show(R.string.noti_job_profile_not_available){
                    redirectToMain()
                }
                return
            }

            lvBot1.show(!item.isMyRecruitment)
            lvBtn2.show(item.isMyRecruitment)
            topBar.state<SimpleTopBarState>().showExtensionButton(item.isMyRecruitment)
            tvGender.text = item.genderFormat
            skillSetAdapter.submit(item.skillSet)
            item.skillSet.isNotEmpty() show listOf(rvSkillSet, tvSkillTittle)
            tvOperationTime.text = item.salonExistFormat
            tvAverageIncome.text = item.averageIncomeFormat
            tvPayrollMethod.text = item.payrollMethodFormat
            tvShuttleStaff.text = item.isShuttle
            tvHousing.text = item.isThereHouse
            tvSalonPhone.text = item.contactPhone
            tvSalonAddress2.text = item.work_place
            tvSalonName.text = item.salonName
            tvCustomerClass.text = item.customerSkinColorFormat
//            tvExperience.text = item.experience_format_2
            tvAddress.text = item.salon?.address
            tvOwnerName.text = item.salon?.ownerName
//            tvOwnerEmail.text = item.salon?.ownerEmail
//            tvOwnerPhone.text = item.salon?.ownerPhone
            btnCall.onClick {
                val intent = Intent(Intent.ACTION_VIEW);
                intent.data = Uri.parse("tel:${item.salon?.phoneNumber}")
                startActivity(intent)
            }
            btnSms.onClick {
                val intent = Intent(Intent.ACTION_VIEW);
                intent.data = Uri.parse("sms:${item.salon?.phoneNumber}")
                startActivity(intent)
            }
            imgDefault.show(item.images.isEmpty())
            lvViewPager.show(item.images.isNotEmpty())
            adapter.submit(item.images)
            tvTitle.text = item.title
            tvDate.text = item.create_at_2
            tvDes.text = item.description
            item.description.isNotBlank() show listOf(tvDes, tvDesTitle)
//            tvWorkingPosition.text = item.position_format
//            tvSalary.text = item.salary_format
//            tvWorkingTime.text = item.working_time_format
            item.isPublish.show(listOf(tvStatusPub, btUnPublic))
            item.isPublish.not().show(listOf(tvStatusUnPub, btPublic))
            btShare.onClick {
                generateSharingLink(
                    type = FirebaseType.job,
                    id = item.id.toString(),
                    imageLink = (if (item.images.isNullOrEmpty()) null else item.images!![0])?.toUri()
                ) {
                    shareDeepLink(
                        requireContext().getString(
                            R.string.recruitment_sender_format,
                            item.salonName,
                            item.title,
                            item.description,
                            item.salary_format_en,
                            item.work_place, it, BuildConfig.versionCustom
                        )
                    )
                }
            }
        }
    }
}

class DetailRecruitmentVM(
    private val recruitmentRepo: RecruitmentRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val loadingCustom: LoadingEvent = LoadingLiveData()
    val detailResult = MediatorLiveData<IRecruitment>().apply {
        addSource(recruitmentRepo.detailRecruitmentResult) { valuee -> value = valuee }
        addSource(recruitmentRepo.updateResult) { valuee -> value = valuee }
    }
    val actionSuccessResult = SingleLiveEvent<Int>()
    val actionDeleteSuccess = SingleLiveEvent<Int>()

    fun getDetailRecruitment(id: Int) = launch(loadingCustom, error) {
        recruitmentRepo.getDetailRecruitment(id)
    }

    //1 : publish || 0 : unpublish
    fun publishRecruitment(status: Int, idRecruitment: Int) = launch(loading, error) {
        recruitmentRepo.publishRecruitment(status, idRecruitment)
        actionSuccessResult.post(if (status == 1) R.string.success_show_recruitment else R.string.success_hide_recruitment)
    }

    fun deleteRecruitment(id: Int) = launch(loading, error) {
        recruitmentRepo.deleteRecruitment(id)
        actionDeleteSuccess.post(R.string.success_delete_recruitment)
    }
}