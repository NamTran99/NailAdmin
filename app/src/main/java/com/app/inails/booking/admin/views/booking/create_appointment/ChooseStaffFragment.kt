package com.app.inails.booking.admin.views.booking.create_appointment

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.livedata.post
import android.support.core.route.nullableArguments
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentChooseStaffBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.isCurrentDate
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.StaffForm
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.auth.StaffCheckInRepo
import com.app.inails.booking.admin.repository.auth.StaffRepo
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ChooseStaffFragment : BaseFragment(R.layout.fragment_choose_staff), TopBarOwner {
    private val binding by viewBinding(FragmentChooseStaffBinding::bind)
    private val viewModel by viewModel<ChooseStaffViewModel>()
    private lateinit var mAdapter: SelectStaffAdapter
    private val arg by lazy { nullableArguments<Routing.ChooseStaff>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.label_choose_staff,
                onBackClick = {
                    activity?.onBackPressed()
                },
            ))
        with(binding) {
            searchView.show(arg?.type != 1 && arg?.type != 2)
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener {
                if ((arg?.type == null) || ((arg?.type == 2 || arg?.type == 3) && !arg!!.dateTime.isCurrentDate()))
                    refresh(searchView.text)
                else viewModel.staffCheckIn()
            }
            mAdapter = SelectStaffAdapter(binding.rvStaff)
            rvStaff.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            mAdapter.apply {
                onLoadMoreListener = { nexPage, _ ->
                    viewModel.refresh(searchView.text.toString(), nexPage)
                }

                onClickItemListener = {
                    viewModel.form.run {
                        id = it.id
                        phone = it.phone
                        name = it.name
                    }
                    if (arg?.type == 3)
                        appEvent.chooseStaffInDetailAppointment.post(it)
                    else if (arg?.type != 1 && arg?.type != 2)
                        appEvent.chooseStaffInCreateAppointment.post(it)
                    else
                        appActivity.appEvent.chooseStaff.post(it)
                    appActivity.onBackPressed()
                }
            }

            searchView.onClickSearchAction = {
                refresh(searchView.text.toString())
            }

        }

        with(viewModel) {
            staffs.bind {
                mAdapter.submit(it)
                binding.emptyLayout.tvEmptyData.show(it.isNullOrEmpty())
                binding.rvStaff.show(!it.isNullOrEmpty())
            }

            staffCheckInList.bind {
                mAdapter.submit(it)
                binding.emptyLayout.tvEmptyData.show(it.isNullOrEmpty())
                binding.rvStaff.show(!it.isNullOrEmpty())
            }
            loadingCustom.bind {
                mAdapter.isLoading = it
                binding.viewRefresh.isRefreshing = it
            }
        }
    }

    private fun refresh(key: String) {
        mAdapter.clear()
        viewModel.refresh(key)
    }

    override fun onResume() {
        super.onResume()
        if ((arg?.type == null) || ((arg?.type == 2 || arg?.type == 3) && !arg!!.dateTime.isCurrentDate()))
            refresh(binding.searchView.text.toString())
        else viewModel.staffCheckIn()
    }
}


class ChooseStaffViewModel(
    private val staffRepo: StaffRepo,
    private val staffCheckInRepo: StaffCheckInRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val staffs = staffRepo.results
    val staffCheckInList = staffCheckInRepo.results
    val form = StaffForm()
    val loadingCustom: LoadingEvent = LoadingLiveData()

//    init {
//        refresh("")
//    }

    fun refresh(keyword: String, page: Int = 1) = launch(loadingCustom, error) {
        staffRepo(keyword, page)
    }

    fun staffCheckIn() = launch(loadingCustom, error) {
        staffCheckInRepo()
    }
}
