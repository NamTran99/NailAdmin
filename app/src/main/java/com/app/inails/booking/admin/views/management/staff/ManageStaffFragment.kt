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
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.popup.PopUpStaffMore
import com.app.inails.booking.admin.model.ui.CreateStaffForm
import com.app.inails.booking.admin.model.ui.UpdateStaffForm
import com.app.inails.booking.admin.model.ui.UpdateStatusStaffForm
import com.app.inails.booking.admin.popups.PopupUserMoreOwner
import com.app.inails.booking.admin.repository.auth.*
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
                R.string.mn_manage_staff
            ) { activity?.onBackPressed() })

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
                mAdapter.submit(it)
                binding.emptyLayout.tvEmptyData.show(it.isNullOrEmpty())
                binding.rvStaff.show(!it.isNullOrEmpty())
            }

            success.bind {
                createUpdateStaffDialog.dismiss()
                success("Success")
            }

            staff.bind {
                mAdapter.updateItem(it)
            }

            staffCreated.bind {
                mAdapter.insertItem(it)
            }

            staffUpdated.bind {
                mAdapter.updateItem(it)
            }

            staffDelete.bind {
                mAdapter.removeItem(it)
            }

            staffChange.bind {
                mAdapter.updateItem(it)
            }
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
                                        id = it.id
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
                        items!![3].user = it
                        items!![4].user = it
                        setupWithViewLeft(view)
                    }
                }
            }
            searchView.setOnSearchListener(
                onLoading = { viewRefresh.isRefreshing = true },
                onSearch = { refresh(it) })
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
    private val changeStatusRepo: ChangeStatusRepository,
    private val updateStaffRepo: UpdateStaffRepository,
    private val createStaffRepo: CreateStaffRepository,
    private val fetchAllStaffRepo: FetchAllStaffRepo,
    private val deleteStaffRepo: DeleteStaffRepository,
    private val changeActiveStaffRepo : ChangeActiveStaffRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val loadingCustom: LoadingEvent = LoadingLiveData()
    val staffs = fetchAllStaffRepo.results
    val staff = changeStatusRepo.results
    val staffCreated = createStaffRepo.results
    val staffUpdated = updateStaffRepo.results
    val staffDelete = deleteStaffRepo.results
    val staffChange = changeActiveStaffRepo.results
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
        success.post(changeStatusRepo(form))
    }

    fun create() = launch(loading, error) {
        success.post(createStaffRepo(createForm))
    }

    fun update() = launch(loading, error) {
        success.post(updateStaffRepo(updateForm))
    }

    fun delete(id: Int) = launch(loading, error) {
        success.post(deleteStaffRepo(id))
    }

    fun changeActive(id: Int) = launch(loading, error) {
        success.post(changeActiveStaffRepo(id))
    }
}




