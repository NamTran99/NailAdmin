package com.app.inails.booking.admin.views.management.staff

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.post
import android.support.core.route.BundleArgument
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentManageCheckInStaffBinding
import com.app.inails.booking.admin.datasource.remote.StaffApi
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.factory.CheckInOutFactory
import com.app.inails.booking.admin.model.ui.ICheckInOutByDate
import com.app.inails.booking.admin.model.ui.ISchedule
import com.app.inails.booking.admin.popups.PopupUserMoreOwner
import com.app.inails.booking.admin.utils.TimeUtils
import com.app.inails.booking.admin.views.management.staff.adapters.ManageCheckInByDateAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckInOutArg(
    val staffID: Int = 0,
) : BundleArgument

class CheckInOutFragment : BaseFragment(R.layout.fragment_manage_check_in_staff), TopBarOwner,
    CreateUpdateStaffOwner, PopupUserMoreOwner {
    private val binding by viewBinding(FragmentManageCheckInStaffBinding::bind)
    private val viewModel by viewModel<CheckInOutViewModel>()
    private val arg by lazy { BundleArgument.of(arguments) ?: CheckInOutArg() }
    private lateinit var mAdapter: ManageCheckInByDateAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.mn_manage_staff,
                onBackClick = {
                    activity?.onBackPressed()
                },
            )
        )

        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { refresh()}

            mAdapter = ManageCheckInByDateAdapter(rvCheckIn)
        }

        with(viewModel) {
           results.bind{
               it.isNullOrEmpty() show binding.emptyLayout.tvEmptyData
               binding.emptyLayout.tvEmptyData.setText(R.string.staff_not_have_attendance)
               mAdapter.submit(it)
           }
            loading.bind{
                binding.viewRefresh.isRefreshing = it
            }
        }
        refresh()
    }

    private fun refresh() {
        mAdapter.clear()
        viewModel.getHistoryCheckInOutByStaff(arg.staffID)
    }
}

class CheckInOutViewModel(
    private val fetchCheckInOutHistory: FetchCheckInOutHistory
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val results = fetchCheckInOutHistory.results

    fun getHistoryCheckInOutByStaff(staffID: Int) = launch (loading,error){
        fetchCheckInOutHistory.invoke(staffID)
    }
}

@Inject(ShareScope.Fragment)
class FetchCheckInOutHistory(
    private val staffApi: StaffApi,
    private val checkInOutFactory: CheckInOutFactory,
) {
    val results = MutableLiveData<List<ICheckInOutByDate>>()
    suspend operator fun invoke(staffID: Int) {
        results.post(
            checkInOutFactory
                .createListCheckInOutByDate(
                    staffApi.getHistoryCheckInOut(staffID, TimeUtils.getZoneID())
                        .await()
                )
        )
    }
}




