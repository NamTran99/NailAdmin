package com.app.inails.booking.admin.views.booking.create_appointment

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.route.BundleArgument
import android.support.core.view.viewBinding
import android.support.navigation.findNavigator
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
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.ui.StaffForm
import com.app.inails.booking.admin.repository.auth.StaffRepo
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChooseStaffArg(
    val idSelected: Int? = 0
) : BundleArgument

class ChooseStaffFragment : BaseFragment(R.layout.fragment_choose_staff), TopBarOwner {
    private val binding by viewBinding(FragmentChooseStaffBinding::bind)
    private val viewModel by viewModel<ChooseStaffViewModel>()
    private lateinit var mAdapter: SelectStaffAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.label_choose_staff
            ) { findNavigator().navigateUp() })
        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { refresh(searchView.text.toString()) }
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
                    findNavigator().navigateUp(
                        result = viewModel.form.toBundle()
                    )
                }
            }

            searchView.setOnSearchListener(
                onLoading = { viewRefresh.isRefreshing = true },
                onSearch = { refresh(it) })

        }

        with(viewModel) {
            staffs.bind {
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
}


class ChooseStaffViewModel(
    private val staffRepo: StaffRepo
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val staffs = staffRepo.results
    val form = StaffForm()
    val loadingCustom: LoadingEvent = LoadingLiveData()

    init {
        refresh("")
    }

    fun refresh(keyword: String, page: Int = 1) = launch(loadingCustom, error) {
        staffRepo(keyword, page)
    }
}
