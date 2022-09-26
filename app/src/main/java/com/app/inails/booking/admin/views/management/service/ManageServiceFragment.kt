package com.app.inails.booking.admin.views.management.service

import ChangeActiveServiceRepository
import CreateServiceRepository
import DeleteServiceRepository
import FetchListServiceRepo
import UpdateServiceRepository
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentManageServiceBinding
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.model.popup.PopUpServiceMore
import com.app.inails.booking.admin.model.ui.ServiceForm
import com.app.inails.booking.admin.popups.PopupServiceMoreOwner
import com.app.inails.booking.admin.views.management.service.adapters.DetailServiceDialogOwner
import com.app.inails.booking.admin.views.management.service.adapters.ManageServiceAdapter
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class ManageServiceFragment : BaseFragment(R.layout.fragment_manage_service), TopBarOwner,
    CreateUpdateServiceOwner, PopupServiceMoreOwner, DetailServiceDialogOwner {
    private val binding by viewBinding(FragmentManageServiceBinding::bind)
    private val viewModel by viewModel<ManageServiceViewModel>()
    private lateinit var mAdapter: ManageServiceAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topBar.setState(
            SimpleTopBarState(
                R.string.mn_manage_service,
                onBackClick = {
                    activity?.onBackPressed()
                },
            ) )

        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener { refreshView() }
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
            searchView.setOnSearchListener(
                onLoading = { viewRefresh.isRefreshing = true },
                onSearch = { refreshView(it) })
        }

        with(viewModel) {
            refreshLoading.bind{
                mAdapter.isLoading = it
                binding.viewRefresh.isRefreshing = it
            }
            // NamTD8: Need find the scheme of loading road
            loading.bind {
                mAdapter.isLoading = it
                binding.viewRefresh.isRefreshing = it
            }
            success.bind {
                createUpdateServiceDialog.dismiss()
                success("Success")
            }

            listService.bind(mAdapter::submit)
            serviceCreated.bind(mAdapter::insertItem)
            serviceUpdated.bind(mAdapter::updateItem)
            serviceDeleted.bind(mAdapter::removeItem)
            serviceChanged.bind(mAdapter::updateItem)
        }
    }

    private fun refreshView(key: String = "") {
        mAdapter.clear()
        viewModel.search(key)
    }

    private fun setUpManageServiceAdapter() {
        mAdapter = ManageServiceAdapter(binding.rvServices).apply {

            onLoadMoreListener = { nexPage, _ ->
                viewModel.search(page = nexPage)
            }

            onClickItemListener = { item ->
                detailServiceDialog.show(item)
            }

            onClickMenuListener = { view, item ->
                if (item.isActive == ACTIVE) {
                    popup.items = PopUpServiceMore.mockActive(requireContext())
                } else {
                    popup.items = PopUpServiceMore.mockInActive(requireContext())
                }

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
                        PopUpServiceMore.ACTIVE_ID -> {
                            viewModel.changeActive(item.id)
                        }
                        PopUpServiceMore.DELETE_ID -> {
                            confirmDialog.show(
                                getString(R.string.title_delete_service),
                                getString(R.string.content_delete_service)
                            ) {
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

    companion object {
        const val ACTIVE = 1
    }
}

class ManageServiceViewModel(
    private val getListServiceRepo: FetchListServiceRepo,
    private val createServiceRepo: CreateServiceRepository,
    private val updateServiceRepo: UpdateServiceRepository,
    private val deleteServiceRepo: DeleteServiceRepository,
    private val changeActiveServiceRepository: ChangeActiveServiceRepository,
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {

    val success = SingleLiveEvent<Any>()

    val serviceCreated = createServiceRepo.results
    val serviceUpdated = updateServiceRepo.results
    val serviceDeleted = deleteServiceRepo.results
    val serviceChanged = changeActiveServiceRepository.results
    val listService = getListServiceRepo.results

    val createForm = ServiceForm()
    val updateForm = ServiceForm()

    init {
        search()
    }

    fun search(keyword: String = "", page: Int = 1) = launch(refreshLoading, error) {
        getListServiceRepo(keyword, page)
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

    fun changeActive(id: Int) = launch(loading, error) {
        success.post(changeActiveServiceRepository(id))
    }
}