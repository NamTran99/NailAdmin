package com.app.inails.booking.admin.views.booking.create_appointment

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.post
import android.support.core.route.BundleArgument
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.navigation.findNavigator
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentChooseStaffBinding
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.datasource.remote.BookingApi
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.model.ui.StaffForm
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChooseStaffArg(
    val idSelected: Int? = 0
) : BundleArgument

class ChooseStaffFragment : BaseRefreshFragment(R.layout.fragment_choose_staff), TopBarOwner {
    private val binding by viewBinding(FragmentChooseStaffBinding::bind)
    private val viewModel by viewModel<ChooseStaffViewModel>()
    private lateinit var mStaffAdapter: SelectStaffAdapter
    override fun onRefreshListener() {
        viewModel.refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.label_choose_staff
            ) { findNavigator().navigateUp() })
        mStaffAdapter = SelectStaffAdapter(binding.rvStaff)
        with(binding) {
            rvStaff.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        with(viewModel) {
            staffs.bind(SelectStaffAdapter(binding.rvStaff).apply {
                onClickItemListener = {
                    form.run {
                        id = it.id
                        phone = it.phone
                        name = it.name
                    }
                    findNavigator().navigateUp(
                        result = form.toBundle()
                    )
                }
            }::submit)
        }
    }

}


class ChooseStaffViewModel(
    private val staffRepo: StaffRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val staffs = staffRepo.results
    val form = StaffForm()

    init {
        refresh()
    }

    fun refresh() = launch(refreshLoading, error) {
        staffRepo()
    }
}


@Inject(ShareScope.Fragment)
class StaffRepository(
    private val bookingApi: BookingApi,
    private val bookingFactory: BookingFactory,
    private val userLocalSource: UserLocalSource
) {
    val results = MutableLiveData<List<IStaff>>()
    suspend operator fun invoke() {
        results.post(
            bookingFactory
                .createStaffList(
                    bookingApi.getAllStaffList(userLocalSource.getSalonID().toString())
                        .await()
                )
        )
    }
}

