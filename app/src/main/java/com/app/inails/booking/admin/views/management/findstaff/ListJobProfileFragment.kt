package com.app.inails.booking.admin.views.management.findstaff

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
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentFindStaffBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.JobFilterForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.popups.PopupUserMoreOwner
import com.app.inails.booking.admin.repository.management.RecruitmentRepo
import com.app.inails.booking.admin.views.management.findstaff.adapter.JobProfileAdapter
import com.app.inails.booking.admin.views.management.staff.CreateUpdateStaffOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ListJobProfileFragment : BaseRefreshFragment(R.layout.fragment_find_staff), TopBarOwner,
    CreateUpdateStaffOwner, PopupUserMoreOwner, FilterJobProfileOwner {
    private val binding by viewBinding(FragmentFindStaffBinding::bind)
    private val viewModel by viewModel<FindingStaff1VM>()
    private lateinit var adapter: JobProfileAdapter

    override fun onRefreshListener() {
        viewModel.getListJobProfile()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.des_search_worker,
                onBackClick = {
                    onBackPress()
                },
            )
        )

        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { viewModel.getListJobProfile() }
            rvCandidates.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    rvCandidates.handler.postDelayedLatest({
                        lvButton.show()
                        lvButton.moveViewFromBottom(requireActivity().windowManager)
                    }, 500)
                } else {
                    lvButton.hide()
                }
                return@setOnTouchListener false
            }
            appEvent.findingWorkingArea.bind {
                viewModel.searchFilterForm = it
                filterJobDialog.setWorkingArea(it)
            }
            filterJobDialog.onClickStateFilter = {
                open<FindWorkingAreaActivity>(viewModel.searchFilterForm)
            }
            searchView.apply {
                onClickSearchAction = {
                    viewModel.jobFilterForm.search = it
                    viewModel.getListJobProfile()
                }
                onLayoutFilterClick = {
                    filterJobDialog.show(viewModel.jobFilterForm, onResetClick ={
                        viewModel.searchFilterForm.clearAll()
                        viewModel.jobFilterForm.clear()
                    }) {
                        viewModel.jobFilterForm = it
                        viewModel.getListJobProfile()
                        searchView.showHideImgFilter(it.isShowFilter())
                    }
                }
            }

            adapter = JobProfileAdapter(rvCandidates).apply {
                onItemClick = { item ->
                    Router.run {
                        redirectToDetailCandidate(item.id)
                    }
                }
                onLoadMoreListener = { page, _ ->
                    viewModel.getListJobProfile(page)
                }
            }

            btAddAds.onClick {
                Router.run {
                    redirectToCreateAds()
                }
            }

            viewModel.apply {
                listJobProfile.bind {
                    if (it.first == 1) {
                        adapter.clear()
                    }
                    adapter.submit(it.second)
                    (adapter.itemCount == 0) show binding.emptyLayout.lvEmpty
                    (adapter.itemCount > 0) show binding.rvCandidates
                }
            }
        }
    }
}

class FindingStaff1VM(
    private val recruitmentRepo: RecruitmentRepo,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    var searchFilterForm = SearchCityStateForm()
    var jobFilterForm = JobFilterForm()
    val loadingCustom: LoadingEvent = LoadingLiveData()
    val listJobProfile = recruitmentRepo.listProfileResult
    private val searchScope = CoroutineScope(Dispatchers.Main)

    init {
        getListJobProfile()
    }

    fun getListJobProfile(page: Int = 1) =
        launch(refreshLoading, error, searchScope.coroutineContext, true) {
            recruitmentRepo.getListProfile(jobFilterForm, page)
        }
}
