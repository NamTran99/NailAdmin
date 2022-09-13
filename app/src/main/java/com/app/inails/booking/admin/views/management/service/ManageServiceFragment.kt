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
import com.app.inails.booking.admin.model.ui.*
import com.app.inails.booking.admin.repository.auth.ServiceRepo
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ManageServiceFragment : BaseRefreshFragment(R.layout.fragment_manage_service), TopBarOwner, CreateUpdateServiceOwner {
    private val binding by viewBinding(FragmentManageServiceBinding::bind)
    private val viewModel by viewModel<ManageServiceViewModel>()
    private lateinit var adapter: ManageServiceAdapter

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
            adapter = ManageServiceAdapter(rvServices)

            btAddService.setOnClickListener {
                createUpdateServiceDialog.show(R.string.title_create_new_service){name , price ->

                }
            }
        }
    }
}

class ManageServiceViewModel(
    private val getListServiceRepo: ServiceRepo,
    private val createServiceRepo: CreateServiceRepository,
    private val updateServiceRepo: UpdateServiceRepository,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val success = SingleLiveEvent<Any>()
    val serviceCreated = createServiceRepo.results
    val serviceUpdated = updateServiceRepo.results
    val listService = getListServiceRepo.results
    val createForm = ServiceForm()
    val updateForm = ServiceForm()

    init {
        refresh()
    }

    fun refresh() = launch(refreshLoading, error) {
        getListServiceRepo()
    }

    fun create() = launch(loading, error){
        success.post(createServiceRepo(createForm))
    }

    fun update() = launch(loading, error) {
        success.post(updateServiceRepo(updateForm))
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
