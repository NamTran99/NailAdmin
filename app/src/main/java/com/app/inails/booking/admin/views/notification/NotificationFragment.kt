package com.app.inails.booking.admin.views.notification

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.LoadingEvent
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.LoadingLiveData
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
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.databinding.FragmentNotificationBinding
import com.app.inails.booking.admin.datasource.remote.NotificationApi
import com.app.inails.booking.admin.extention.colorSchemeDefault
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.factory.NotificationFactory
import com.app.inails.booking.admin.model.popup.PopUpNotificationItemMore
import com.app.inails.booking.admin.model.popup.PopUpNotificationMore
import com.app.inails.booking.admin.model.ui.INotification
import com.app.inails.booking.admin.model.ui.NotificationForm
import com.app.inails.booking.admin.model.ui.NotificationIDForm
import com.app.inails.booking.admin.model.ui.NotificationIDsForm
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.popups.PopupNotiItemMoreOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class NotificationFragment : BaseFragment(R.layout.fragment_notification), TopBarOwner,
    PopupNotiItemMoreOwner {
    val viewModel by viewModel<NotificationViewModel>()
    val binding by viewBinding(FragmentNotificationBinding::bind)
    private lateinit var mAdapter: NotificationAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(
            SimpleTopBarState(
                R.string.title_notification,
                onBackClick = {
                    activity?.onBackPressed()
                },
                onSettingClick = {
                    if (mAdapter.isShowSelect)
                        popupNoti.items = PopUpNotificationMore.mockSelect(requireContext())
                    else
                        popupNoti.items = PopUpNotificationMore.mockUnselect(requireContext())
                    popupNoti.setListener {
                        when (it.id) {
                            0 -> {
                                viewModel.readAll()
                            }
                            1 -> {
                                confirmDialog.show(
                                    title = R.string.title_delete_all_notifications,
                                    message = R.string.message_delete_all_notification
                                ) {
                                    viewModel.deleteAll()
                                }
                            }
                            2 -> {
                                mAdapter.showSelect(true)
                            }
                            3 -> {
                                mAdapter.showSelect(false)
                            }
                            4 -> {
                                confirmDialog.show(
                                    title = R.string.title_delete_all_selected_notifications,
                                    message = R.string.message_delete_all_selected_notification
                                ) {
                                    viewModel.idsForm.idList =
                                        mAdapter.selectedItems as MutableList<Int>
                                    viewModel.delete()
                                }
                            }
                        }
                    }

                    popupNoti.setupWithViewLeft(it)
                }
            )
        )

        with(binding) {
            viewRefresh.colorSchemeDefault()
            viewRefresh.setOnRefreshListener {
                refresh(1)
            }
            mAdapter = NotificationAdapter(binding.rvNotification)
        }


        with(viewModel) {
            loadingCustom.bind {
                mAdapter.isLoading = it
                binding.viewRefresh.isRefreshing = it
            }

            list.bind {
                if (it.first == 1) {
                    mAdapter.clear()
                }
                mAdapter.submit(it.second)
                binding.emptyLayout.tvEmptyData.show(it.second.isEmpty() && it.first == 1)
            }

            notification.bind {
                mAdapter.updateItem(it)
            }

            deleteID.bind {
                mAdapter.removeItem(it)
            }

            success.bind {
                success(it)
            }

            goToDetail.bind {
                it?.let {
                    Router.redirectToAppointmentDetail(this@NotificationFragment, it)
                    goToDetail.post(null)
                }
            }
        }

        appEvent.refreshData.observe(this) {
            refresh()
        }

        setListeners()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh(page: Int = 1) {
        if (page == 1) mAdapter.clear()
        viewModel.listForm.page = page
        viewModel.refresh()
    }

    private fun setListeners() {
        with(binding) {
            mAdapter.apply {
                onLoadMoreListener = { nextPage, _ ->
                    refresh(nextPage)
                }

                onClickItemListener = {
                    if (it.isRead)
                        Router.redirectToAppointmentDetail(this@NotificationFragment, it.dataId)
                    else {
                        viewModel.idForm.id = it.id
                        viewModel.idForm.dataId = it.dataId
                        viewModel.read(true)
                    }
                }

                onClickMenuListener = { view, it ->
                    if (!it.isRead) {
                        popupItem.items = PopUpNotificationItemMore.mockUnread(requireContext())
                    } else
                        popupItem.items = PopUpNotificationItemMore.mockRead(requireContext())
                    popupItem.setListener {
                        when (it.id) {
                            0 -> {
                                viewModel.idForm.id = it.notification!!.id
                                viewModel.read()
                            }
                            1 -> {
                                viewModel.idForm.id = it.notification!!.id
                                viewModel.markAsUnread()
                            }
                            2 -> {
                                confirmDialog.show(
                                    title = R.string.title_delete_notification,
                                    message = R.string.message_delete_notification
                                ) {
                                    viewModel.idsForm.idList.clear()
                                    viewModel.idsForm.idList.add(it.notification!!.id)
                                    viewModel.delete()
                                }
                            }

                        }
                    }

                    popupItem.run {
                        items!![0].notification = it
                        items!![1].notification = it
                        if (items!!.size > 2) {
                            items!![2].notification = it
                        }
                        setupWithViewLeft(view)
                    }
                }
            }

        }
    }
}

class NotificationViewModel(
    private val notificationRepo: NotificationRepository
) : ViewModel(), WindowStatusOwner by LiveDataStatusOwner() {
    val success = SingleLiveEvent<String>()
    val goToDetail = SingleLiveEvent<Int?>()
    val listForm = NotificationForm()
    val idForm = NotificationIDForm()
    val idsForm = NotificationIDsForm()
    val loadingCustom: LoadingEvent = LoadingLiveData()
    val list = notificationRepo.results
    val notification = notificationRepo.result
    val deleteID = notificationRepo.resultID

    fun refresh() = launch(loadingCustom, error) {
        notificationRepo(listForm)
    }

    fun read(viewDetail: Boolean = false) = launch(loading, error) {
        notificationRepo.read(idForm)
        if (viewDetail) goToDetail.post(idForm.dataId)
    }

    fun markAsUnread() = launch(loading, error) {
        notificationRepo.unread(idForm)
    }

    fun delete() = launch(loading, error) {
        notificationRepo.delete(idsForm)
        success.post("Delete selected notification successfully")
    }

    fun deleteAll() = launch(loading, error) {
        notificationRepo.deleteAll()
        success.post("Delete all notifications successfully")
        listForm.page = 1
        refresh()
    }

    fun readAll() = launch(loading, error) {
        notificationRepo.readAll()
        success.post("Read all notifications successfully")
        listForm.page = 1
        refresh()
    }
}

@Inject(ShareScope.Fragment)
class NotificationRepository(
    private val notificationApi: NotificationApi,
    private val notificationFactory: NotificationFactory
) {
    val results = MutableLiveData<Pair<Int, List<INotification>>>()
    val result = MutableLiveData<INotification>()
    val resultID = MutableLiveData<MutableList<Int>>()
    val count = MutableLiveData<Int>()
    suspend operator fun invoke(form: NotificationForm) {
        results.post(
            form.page to
                    notificationFactory.createNotificationList(notificationApi.list(form).await())
        )
    }

    suspend fun read(form: NotificationIDForm) {
        result.post(
            notificationFactory.createANotification(notificationApi.read(form).await())
        )
    }

    suspend fun unread(form: NotificationIDForm) {
        result.post(
            notificationFactory.createANotification(notificationApi.markAsUnread(form).await())
        )
    }

    suspend fun delete(form: NotificationIDsForm) {
        form.ids = form.idList.toString()
        notificationApi.delete(form).await()
        resultID.post(form.idList)
    }

    suspend fun deleteAll() {
        notificationApi.deleteAll().await()
    }

    suspend fun readAll() {
        notificationApi.readAll().await()
    }

    suspend fun numberNotificationSalonUnread() {
        count.post(notificationApi.numberNotificationSalonUnread().await())
    }
}