package com.app.inails.booking.admin.views.management.service

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
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentManageServiceBinding
import com.app.inails.booking.admin.datasource.remote.ServiceApi
import com.app.inails.booking.admin.factory.BookingFactory
import com.app.inails.booking.admin.model.popup.PopUpServiceMore
import com.app.inails.booking.admin.model.ui.IService
import com.app.inails.booking.admin.model.ui.ServiceForm
import com.app.inails.booking.admin.popups.PopupServiceMoreOwner
import com.app.inails.booking.admin.repository.auth.FetchListServiceRepo
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ManageServiceFragment : BaseRefreshFragment(R.layout.fragment_manage_service), TopBarOwner,
    CreateUpdateServiceOwner, PopupServiceMoreOwner {
    private val binding by viewBinding(FragmentManageServiceBinding::bind)
    private val viewModel by viewModel<ManageServiceViewModel>()
    private lateinit var mAdapter: ManageServiceAdapter

    override fun onRefreshListener() {
        viewModel.refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topBar.setState(
            SimpleTopBarState(
                R.string.mn_manage_service
            ) { activity?.onBackPressed() })

        with(binding) {
            setUpManageServiceAdapter()
            btAddService.setOnClickListener {
                createUpdateServiceDialog.show(R.string.title_create_new_service) { mName, mPrice ->
                    viewModel.createForm.apply {
                        name = mName
                        price_input = mPrice
                    }
                    viewModel.create()
                }
            }
        }

        with(viewModel) {
            success.bind {
                createUpdateServiceDialog.dismiss()
                success("Success")
            }

            listService.bind(mAdapter::submit)
            serviceCreated.bind(mAdapter::insertItem)
            serviceUpdated.bind(mAdapter::updateItem)
            serviceDeleted.bind(mAdapter::removeItem)
        }
    }

    private fun setUpManageServiceAdapter() {
        mAdapter = ManageServiceAdapter(binding.rvServices).apply {
            onClickItemListener = {
                comingSoon()
            }

            onClickMenuListener = { view, item ->
                popup.items = PopUpServiceMore.mockActive(requireContext())
                popup.setListener {
                    when (it.id) {
                        PopUpServiceMore.EDIT_ID -> {
                            createUpdateServiceDialog.show(
                                R.string.title_update_service, item
                            ) { mName, mPrice ->
                                viewModel.updateForm.apply {
                                    id = item.id
                                    name = mName
                                    price_input = mPrice
                                }
                                viewModel.update()
                            }
                        }
                        PopUpServiceMore.DELETE_ID -> {
                            confirmDialog.show(
                                getString(R.string.title_delete_service),
                                item.name
                            ){
                                viewModel.delete(item.id)
                            }
                        }
                    }
                }

                popup.run {
                    items!![0].service = item
                    items!![1].service = item
                    setupWithViewLeft(view)
                }
            }
        }
    }
}

class ManageServiceViewModel(
    private val getListServiceRepo: FetchListServiceRepo,
    private val createServiceRepo: CreateServiceRepository,
    private val updateServiceRepo: UpdateServiceRepository,
    private val deleteServiceRepo: DeleteServiceRepository,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val success = SingleLiveEvent<Any>()

    val serviceCreated = createServiceRepo.results
    val serviceUpdated = updateServiceRepo.results
    val serviceDeleted = deleteServiceRepo.results
    val listService = getListServiceRepo.results

    val createForm = ServiceForm()
    val updateForm = ServiceForm()

    init {
        refresh()
    }

    fun refresh() = launch(refreshLoading, error) {
        getListServiceRepo()
    }

    fun create() = launch(loading, error) {
        success.post(createServiceRepo(createForm))
    }

    fun update() = launch(loading, error) {
        success.post(updateServiceRepo(updateForm))
    }

    fun delete(serviceID: Int) = launch(loading, error) {
        success.post(deleteServiceRepo(serviceID))
    }
}

@Inject(ShareScope.Fragment)
class CreateServiceRepository(
    private val serviceApi: ServiceApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IService>()
    suspend operator fun invoke(form: ServiceForm) {
        form.validate()
        results.post(
            bookingFactory
                .createAService(
                    serviceApi.createService(form).await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class UpdateServiceRepository(
    private val serviceApi: ServiceApi,
    private val bookingFactory: BookingFactory,
) {
    val results = MutableLiveData<IService>()
    suspend operator fun invoke(form: ServiceForm) {
        form.validate()
        results.post(
            bookingFactory
                .createAService(
                    serviceApi.updateService(form).await()
                )
        )
    }
}

@Inject(ShareScope.Fragment)
class DeleteServiceRepository(
    private val serviceApi: ServiceApi,
) {
    val results = MutableLiveData<Int>()
    suspend operator fun invoke(serviceID: Int) {
        serviceApi.deleteService(serviceID).await()
        results.post(
            serviceID
        )
    }
}
