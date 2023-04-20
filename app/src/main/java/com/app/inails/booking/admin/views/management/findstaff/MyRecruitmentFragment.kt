package com.app.inails.booking.admin.views.management.findstaff

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.route.open
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentMyRecruitmentAdsBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.popup.PopUpRecruitmentMore
import com.app.inails.booking.admin.model.ui.RecruitmentFilterForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.popups.PopupRecruitmentOwner
import com.app.inails.booking.admin.repository.management.RecruitmentRepo
import com.app.inails.booking.admin.views.management.findstaff.adapter.RecruitmentAdsAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner


class MyRecruitmentFragment :
    BaseFragment(com.app.inails.booking.admin.R.layout.fragment_my_recruitment_ads), TopBarOwner,
    PopupRecruitmentOwner, FilterRecruitmentProfileDialogOwner {
    private val binding by viewBinding(FragmentMyRecruitmentAdsBinding::bind)
    private val vm by viewModel<MyRecruitmentVM>()
    private lateinit var recruitmentAdsAdapter: RecruitmentAdsAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                com.app.inails.booking.admin.R.string.mn_my_recruitment,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        with(binding) {
            searchView.apply {
                onClickSearchAction = {
                    vm.recruitmentFilterForm.search = it
                    vm.fetchRecruitmentList()
                }

                onLayoutFilterClick = {
                    filterRecruitmentDialog.show(vm.recruitmentFilterForm, resetOnclick = {vm.stateFilterForm.clearAll()}) {
                        vm.recruitmentFilterForm = it
                        vm.fetchRecruitmentList()
                        searchView.showHideImgFilter(it.isShowFilter())
                    }
                }

            }
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { vm.fetchRecruitmentList() }
            appEvent.findingWorkingArea.bind {
                vm.stateFilterForm = it
                filterRecruitmentDialog.setWorkingArea(it)
            }
            filterRecruitmentDialog.onClickStateFilter = {
                open<FindWorkingAreaActivity>(vm.stateFilterForm)
            }

            recruitmentAdsAdapter = RecruitmentAdsAdapter(rvRecruitment).apply {
                onItemClick = { item ->
                    Router.run {
                        redirectToDetailRecruitment(item.id)
                    }
                }
                onClickMenuListener = { view, item ->
                    popup.items =
                        PopUpRecruitmentMore.mockRecruitmentMenu(requireContext(), item.isPublish)
                    popup.setListener {
                        when (it.id) {
                            PopUpRecruitmentMore.ID_EDIT -> {
                                Router.run {
                                    redirectToUpdateAds(item.id)
                                }
                            }
                            PopUpRecruitmentMore.ID_HIDE -> {
                                vm.publishRecruitment(0, item.id)
                            }
                            PopUpRecruitmentMore.ID_PUBLISH -> {
                                vm.publishRecruitment(1, item.id)
                            }
                            PopUpRecruitmentMore.ID_DELETE -> {
                                confirmDialog.showDelete(
                                    com.app.inails.booking.admin.R.string.title_delete_post,
                                    com.app.inails.booking.admin.R.string.message_delete_post
                                ) {
                                    vm.deleteRecruitment(item.id)
                                }
                            }
                        }
                    }
                    popup.run {
                        setupWithViewLeft(view)
                    }
                }
                onLoadMoreListener = { page, _ ->
                    vm.fetchRecruitmentList(page)
                }
            }
            btAddAds.onClick {
                Router.run {
                    redirectToCreateAds()
                }
            }

            btAddAds2.onClick {
                Router.run {
                    redirectToCreateAds()
                }
            }

            rvRecruitment.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    rvRecruitment.handler.postDelayedLatest({
                        lvButton.moveViewFromBottom(requireActivity().windowManager)
                        lvButton.show()
                    } , 500)
                } else {
                    lvButton.hide()
                }
                return@setOnTouchListener false
            }

            vm.apply {
                updateResult.bind {
                    recruitmentAdsAdapter.updateItem(it)
                }
                loadingCustom.bind {
                    recruitmentAdsAdapter.isLoading = it
                    binding.viewRefresh.isRefreshing = it

                }
                fetchListResult.bind {
                    if (it.first == 1) {
                        recruitmentAdsAdapter.clear()
                    }
                    recruitmentAdsAdapter.submit(it.second)
                    (recruitmentAdsAdapter.itemCount == 0 && !vm.recruitmentFilterForm.isNotHaveDataSearch()) show binding.emptyLayout.lvEmpty
                    (recruitmentAdsAdapter.itemCount == 0 && vm.recruitmentFilterForm.isNotHaveDataSearch()) show binding.lvEmptyData
                    runAnimation(recruitmentAdsAdapter.itemCount == 0)
                    (recruitmentAdsAdapter.itemCount == 0) hide binding.lvButton
                    (recruitmentAdsAdapter.itemCount > 0  || !vm.recruitmentFilterForm.isNotHaveDataSearch()) show listOf(
                        binding.rvRecruitment,
                        binding.searchView,
                        btAddAds
                    )

                }
                actionSuccessResult.bind {
                    success(it)
                }
                idRemove.bind {
                    recruitmentAdsAdapter.removeItem(it)
                    (recruitmentAdsAdapter.items().getData().isEmpty() && vm.recruitmentFilterForm.isNotHaveDataSearch()).show(binding.lvEmptyData)
                    runAnimation( recruitmentAdsAdapter.items().getData().isEmpty())
                    recruitmentAdsAdapter.items().getData().isEmpty().hide(binding.btAddAds)
                    recruitmentAdsAdapter.items().getData().isEmpty().hide(binding.lvButton)
                }
            }
        }
    }
    private fun runAnimation(isRun: Boolean){
        if(!isRun) return
        binding.apply {
            listOf(tv1,animationView,tv2,tv3,tv4).moveViewFromRight(requireActivity().windowManager, duration = 500)
            lv1.moveViewFromBottom(mWindowManager, duration = 500)
        }
    }

}

class MyRecruitmentVM(
    private val recruitmentRepo: RecruitmentRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    var recruitmentFilterForm= RecruitmentFilterForm()
    var stateFilterForm= SearchCityStateForm()

    val loadingCustom: LoadingEvent = LoadingLiveData()
    val fetchListResult = recruitmentRepo.fetchListResult
    val actionSuccessResult = SingleLiveEvent<Int>()
    val idRemove = recruitmentRepo.idRemove
    val updateResult = recruitmentRepo.updateResult

    init {
        refreshFragment()
    }

    fun refreshFragment() {
        fetchRecruitmentList()
    }

    fun fetchRecruitmentList( page: Int = 1) = launch(loadingCustom, error) {
        recruitmentRepo.fetchListRecruitment(recruitmentFilterForm,page)
    }

    fun deleteRecruitment(id: Int) = launch(loading, error) {
        recruitmentRepo.deleteRecruitment(id)
        actionSuccessResult.post(com.app.inails.booking.admin.R.string.success_delete_recruitment)
    }

    //1 : publish || 0 : unpublish
    fun publishRecruitment(status: Int, idRecruitment: Int) = launch(loading, error) {
        recruitmentRepo.publishRecruitment(status, idRecruitment)
        actionSuccessResult.post(if (status == 1) com.app.inails.booking.admin.R.string.success_show_recruitment else com.app.inails.booking.admin.R.string.success_hide_recruitment)
    }
}
