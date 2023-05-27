package com.app.inails.booking.admin.views.recruitment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.route.open
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentMyRecruitmentAdsBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.RecruitmentFilterForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.popups.PopupRecruitmentOwner
import com.app.inails.booking.admin.repository.management.RecruitmentRepo
import com.app.inails.booking.admin.views.management.findstaff.FilterRecruitmentProfileDialogOwner
import com.app.inails.booking.admin.views.management.findstaff.FindWorkingAreaActivity
import com.app.inails.booking.admin.views.management.findstaff.SearchCityStateForm
import com.app.inails.booking.admin.views.management.findstaff.adapter.RecruitmentAdsAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class RecruitmentFragment : BaseFragment(R.layout.fragment_my_recruitment_ads), TopBarOwner,
    PopupRecruitmentOwner, FilterRecruitmentProfileDialogOwner {

    private val binding by viewBinding(FragmentMyRecruitmentAdsBinding::bind)
    private val vm by viewModel<MyRecruitmentVM>()
    private lateinit var recruitmentAdsAdapter: RecruitmentAdsAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()
        binding.apply {
            lvButton.show(!jobAndRecruitmentSource.isHaveJobProfile())
            val onTouchListener = View.OnTouchListener { v, event ->
                if (jobAndRecruitmentSource.isHaveJobProfile()) return@OnTouchListener false
                if (event.action == MotionEvent.ACTION_UP) {
                    rvRecruitment.handler.postDelayedLatest({
                        lvButton.show()
                        lvButton.moveViewFromBottom(requireActivity().windowManager)
                    }, 500)
                } else {
                    lvButton.hide()
                }
                return@OnTouchListener false
            }
            if (!jobAndRecruitmentSource.isHaveJobProfile()) {
                rvRecruitment.setOnTouchListener(onTouchListener)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.mn_recruitment,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )
        vm.fetchRecruitmentList()

        with(binding) {
            searchView.apply {
                onClickSearchAction = {
                    vm.recruitmentFilterForm.search = it
                    vm.fetchRecruitmentList()
                }

                onLayoutFilterClick = {
                    filterRecruitmentDialog.show(
                        vm.recruitmentFilterForm,
                        resetOnclick = { vm.stateFilterForm.clearAll() }) {
                        vm.recruitmentFilterForm = it
                        vm.fetchRecruitmentList()
                        searchView.showHideImgFilter(it.isShowFilter())
                    }
                }
            }

            filterRecruitmentDialog.onClickStateFilter = {
                open<FindWorkingAreaActivity>(vm.stateFilterForm)
            }

            appEvent.findingWorkingArea.bind {
                vm.stateFilterForm = it
                filterRecruitmentDialog.setWorkingArea(it)
            }

            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener {
                vm.fetchRecruitmentList()
            }

            btAddAds.onClick {
                Router.open(self, Routing.MyCv)
            }

            recruitmentAdsAdapter = RecruitmentAdsAdapter(rvRecruitment).apply {
                onItemClick = { item ->
                    Router.run {
                        redirectToDetailRecruitment(item.id, false)
                    }
                }
                onLoadMoreListener = { page, _ ->
                    vm.fetchRecruitmentList(
                        page
                    )
                }
            }
            vm.apply {
                loadingCustom.bind {
                    recruitmentAdsAdapter.isLoading = it
                    binding.viewRefresh.isRefreshing = it
                }
                fetchListResult.bind {
                    if (it.first == 1) {
                        recruitmentAdsAdapter.clear()
                    }
                    recruitmentAdsAdapter.submit(it.second)
                    (recruitmentAdsAdapter.itemCount == 0) show binding.emptyLayout.lvEmpty
                    (recruitmentAdsAdapter.itemCount > 0) show listOf(
                        binding.rvRecruitment,
                        binding.searchView
                    )
                    (recruitmentAdsAdapter.itemCount > 0 || !recruitmentFilterForm.isNotHaveDataSearch()) show listOf(
                        binding.searchView
                    )
                }
            }
        }
    }
}

class MyRecruitmentVM(
    private val recruitmentRepo: RecruitmentRepo,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val loadingCustom: LoadingEvent = LoadingLiveData()
    val fetchListResult = recruitmentRepo.fetchListResult
    var recruitmentFilterForm = RecruitmentFilterForm()
    var stateFilterForm = SearchCityStateForm()

    init {
        fetchRecruitmentList()
    }

    fun fetchRecruitmentList(page: Int = 1) =
        launch(loadingCustom, error) {
            recruitmentRepo.fetchListRecruitment(recruitmentFilterForm, page)
        }
}
