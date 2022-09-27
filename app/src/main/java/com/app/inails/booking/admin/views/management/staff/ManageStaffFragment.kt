package com.app.inails.booking.admin.views.management.staff

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.inails.booking.admin.DataConst
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentManageStaffBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.onSearchListener
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.popup.PopUpStaffMore
import com.app.inails.booking.admin.model.ui.CreateStaffForm
import com.app.inails.booking.admin.model.ui.UpdateStaffForm
import com.app.inails.booking.admin.model.ui.UpdateStatusStaffForm
import com.app.inails.booking.admin.popups.PopupUserMoreOwner
import com.app.inails.booking.admin.repository.auth.FetchAllStaffRepo
import com.app.inails.booking.admin.repository.auth.StaffRepo
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ManageStaffFragment : BaseFragment(R.layout.fragment_manage_staff), TopBarOwner,
    CreateUpdateStaffOwner, PopupUserMoreOwner {
    private val binding by viewBinding(FragmentManageStaffBinding::bind)
    private val viewModel by viewModel<ManageStaffViewModel>()
    private lateinit var mAdapter: ManageStaffAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.mn_manage_staff,
                onBackClick = {
                    activity?.onBackPressed()
                },
            ) )

        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { refresh(searchView.text.toString()) }
            mAdapter = ManageStaffAdapter(binding.rvStaff)
            rvStaff.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )

            btAddStaff.setOnClickListener {
                createUpdateStaffDialog.show(R.string.title_create_new_staff) { fn, ln, ph ->
                    viewModel.createForm.run {
                        firstName = fn
                        lastName = ln
                        phone = ph
                    }
                    viewModel.create()
                }
            }

        }

        setListeners()

        with(viewModel) {
            loadingCustom.bind {
                mAdapter.isLoading = it
                binding.viewRefresh.isRefreshing = it
            }

            staffs.bind {
                if (it.first == 1) {
                    mAdapter.clear()
                }
                mAdapter.submit(it.second)
                binding.emptyLayout.tvEmptyData.show(it.second.isEmpty() && it.first == 1)
            }

            success.bind {
                createUpdateStaffDialog.dismiss()
                success("Success")
            }

            staff.bind {
                mAdapter.updateItem(it)
            }
            staffRemove.bind {
                mAdapter.removeItem(it)
            }
        }

        appEvent.refreshData.observe(this){
            refresh(binding.searchView.text.toString())
        }
    }

    private fun refresh(key: String) {
        mAdapter.clear()
        viewModel.search(key)
    }

    private fun setListeners() {
        with(binding) {
            mAdapter.apply {
                onLoadMoreListener = { nexPage, _ ->
                    viewModel.search(searchView.text.toString(), nexPage)
                }

                onClickItemListener = {

                }

                onUpdateStatusListener = { staffID, stt ->
                    viewModel.form.run {
                        id = staffID
                        status = stt
                    }
                    viewModel.changeStatus()
                }

                onClickMenuListener = { view, it ->
                    if (it.active == 1 && it.status != DataConst.StaffStatus.STAFF_WORKING) {
                        popup.items = PopUpStaffMore.mockActive(requireContext())
                    } else if (it.active == 1 && it.status == DataConst.StaffStatus.STAFF_WORKING) {
                        popup.items = PopUpStaffMore.mockIsWorking(requireContext())
                    } else {
                        popup.items = PopUpStaffMore.mockInactive(requireContext())
                    }
                    popup.setListener {
                        when (it.id) {
                            0 -> {
                                createUpdateStaffDialog.show(
                                    R.string.title_update_staff,
                                    it.user
                                ) { fn, ln, ph ->
                                    viewModel.updateForm.run {
                                        id = it.user!!.id
                                        firstName = fn
                                        lastName = ln
                                        phone = ph
                                    }
                                    viewModel.update()
                                }
                            }
                            1 -> {
                                viewModel.changeActive(it.user!!.id)
                            }
                            2 -> {
                                viewModel.changeActive(it.user!!.id)
                            }
                            3 -> {
                                comingSoon()
                            }
                            4 -> {
                                comingSoon()
                            }
                            5 -> {
                                showConfirmDialog(
                                    getString(R.string.title_delete_staff),
                                    String.format(
                                        getString(R.string.message_delete_staff),
                                        it.user!!.name
                                    ),
                                ) {
                                    viewModel.delete(it.user!!.id)
                                }
                            }
                        }
                    }

                    popup.run {
                        items!![0].user = it
                        items!![1].user = it
                        items!![2].user = it
                        if (items!!.size > 3) {
                            items!![3].user = it
                            items!![4].user = it
                        }
                        setupWithViewLeft(view)
                    }
                }
            }

            searchView.onSearchListener {
                refresh(searchView.text.toString())
            }
        }
    }

    private fun showConfirmDialog(title: String, message: String, function: () -> Unit) {
        confirmDialog.show(
            title = title,
            message = message
        ) {
            function.invoke()
        }
    }
}


class ManageStaffViewModel(
    private val fetchAllStaffRepo: FetchAllStaffRepo,
    private val staffRepo: StaffRepo,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val loadingCustom: LoadingEvent = LoadingLiveData()
    val staffs = fetchAllStaffRepo.results
    val staff = staffRepo.result
    val staffRemove = staffRepo.resultRemove
    val form = UpdateStatusStaffForm()
    val updateForm = UpdateStaffForm()
    val createForm = CreateStaffForm()
    val success = SingleLiveEvent<Any>()

    init {
        search("")
    }

    fun search(keyword: String, page: Int = 1) = launch(loadingCustom, error) {
        fetchAllStaffRepo(keyword, page)
    }

    fun changeStatus() = launch(loading, error) {
        success.post(staffRepo.changeStatus(form))
    }

    fun create() = launch(loading, error) {
        success.post(staffRepo.create(createForm))
    }

    fun update() = launch(loading, error) {
        success.post(staffRepo.update(updateForm))
    }

    fun delete(id: Int) = launch(loading, error) {
        success.post(staffRepo.delete(id))
    }

    fun changeActive(id: Int) = launch(loading, error) {
        success.post(staffRepo.changeActive(id))
    }
}




