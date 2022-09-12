package com.app.inails.booking.admin.views.manage_staff

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentManageStaffBinding
import com.app.inails.booking.admin.datasource.remote.StaffApi
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.popup.PopUpStaffMore
import com.app.inails.booking.admin.model.ui.CreateStaffForm
import com.app.inails.booking.admin.model.ui.IStaff
import com.app.inails.booking.admin.model.ui.UpdateStaffForm
import com.app.inails.booking.admin.model.ui.UpdateStatusStaffForm
import com.app.inails.booking.admin.popups.PopupUserMoreOwner
import com.app.inails.booking.admin.repository.auth.StaffRepo
import com.app.inails.booking.admin.views.dialog.CreateUpdateStaffOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ManageStaffFragment : BaseRefreshFragment(R.layout.fragment_manage_staff), TopBarOwner,
    CreateUpdateStaffOwner, PopupUserMoreOwner {
    private val binding by viewBinding(FragmentManageStaffBinding::bind)
    private val viewModel by viewModel<ManageStaffViewModel>()
    private lateinit var mAdapter: StaffAdapter
    override fun onRefreshListener() {
        viewModel.refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.mn_manage_staff
            ) { activity?.onBackPressed() })

        with(binding) {
            mAdapter = StaffAdapter(binding.rvStaff)
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

        with(viewModel) {
            staffs.bind(mAdapter.apply {
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
                    popup.items = PopUpStaffMore.mockActive(requireContext())
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
                                comingSoon()
                            }
                            2 -> {
                                comingSoon()
                            }
                            3 -> {
                                comingSoon()
                            }
                            4 -> {
                                comingSoon()
                            }
                            5 -> {
                                comingSoon()
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
            }::submit)

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
        }
    }

}


class ManageStaffViewModel(
    private val staffRepo: StaffRepo,
    private val changeStatusRepo: ChangeStatusRepository,
    private val updateStaffRepo: UpdateStaffRepository,
    private val createStaffRepo: CreateStaffRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val staffs = staffRepo.results
    val staff = changeStatusRepo.results
    val staffCreated = createStaffRepo.results
    val staffUpdated = updateStaffRepo.results
    val form = UpdateStatusStaffForm()
    val updateForm = UpdateStaffForm()
    val createForm = CreateStaffForm()
    val success = SingleLiveEvent<Any>()

    init {
        refresh()
    }

    fun refresh() = launch(refreshLoading, error) {
        staffRepo()
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
}

@Inject(ShareScope.Fragment)
class ChangeStatusRepository(
    private val staffApi: StaffApi,
    private val bookingFactory: BookingFactory
) {
    val results = MutableLiveData<IStaff>()
    suspend operator fun invoke(form: UpdateStatusStaffForm) {
        results.post(
            bookingFactory
                .createAStaff(
                    staffApi.changeStatus(form).await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class UpdateStaffRepository(
    private val staffApi: StaffApi,
    private val bookingFactory: BookingFactory,
    private val textFormatter: TextFormatter
) {
    val results = MutableLiveData<IStaff>()
    suspend operator fun invoke(form: UpdateStaffForm) {
        form.validate()
        form.phone = textFormatter.formatPhoneNumber(form.phone)
        results.post(
            bookingFactory
                .createAStaff(
                    staffApi.updateStaff(form).await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class CreateStaffRepository(
    private val staffApi: StaffApi,
    private val bookingFactory: BookingFactory,
    val textFormatter: TextFormatter
) {
    val results = MutableLiveData<IStaff>()
    suspend operator fun invoke(form: CreateStaffForm) {
        form.validate()
        form.phone = textFormatter.formatPhoneNumber(form.phone)
        results.post(
            bookingFactory
                .createAStaff(
                    staffApi.createStaff(form).await()
                )
        )
    }
}



