package com.app.inails.booking.admin.views.clients.notification

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
import android.view.Gravity
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.DataConst.NotifyFireBaseCloudType.OWNER_ACCOUNT_APPROVE
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentNotificationClientBinding
import com.app.inails.booking.admin.datasource.remote.clients.NotificationClientApi
import com.app.inails.booking.admin.extention.lock
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.factory.client.NotificationClientFactory
import com.app.inails.booking.admin.factory.client.NotificationOptionRepository
import com.app.inails.booking.admin.model.ui.client.INotificationClient
import com.app.inails.booking.admin.model.ui.client.popup.INotificationItemOption
import com.app.inails.booking.admin.model.ui.client.popup.INotificationOption
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.client.NotificationRepository
import com.app.inails.booking.admin.views.clients.popup.MenuPopupOwner
import com.app.inails.booking.admin.views.dialog.MessageDialogOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleWithMoreTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class NotificationClientFragment : BaseRefreshFragment(R.layout.fragment_notification_client),
    TopBarOwner,
    MenuPopupOwner,
    MessageDialogOwner {
    override fun onRefreshListener() {
        refresh()
    }

    private val binding by viewBinding(FragmentNotificationClientBinding::bind)
    private val viewModel by viewModel<NotificationVM>()
    private lateinit var mAdapter: NotificationClientAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(SimpleWithMoreTopBarState(R.string.title_notifications, onBackClick = {
            activity?.onBackPressed()
        }, onMoreClick = {}))
        with(binding) {
            mAdapter = NotificationClientAdapter(rcvNotification)
            mAdapter.onLoadMoreListener = { nexPage, _ ->
                with(viewModel) {
                    page = nexPage
                    fetch()
                }
            }
            mAdapter.onClickItemListener = { openDetails(it) }
            mAdapter.onClickMoreListener = {
                notificationItemPopup.setupWithViewsAutoClick(it.second, Gravity.START)
                viewModel.optionsItem(it.first.isRead)
                notificationItemPopup.setListener { option -> viewModel.itemSelected(option.id to it.first.id) }
            }
            notificationPopup.setupWithViews(
                topBar.state<SimpleWithMoreTopBarState>().getViewMore(),
                Gravity.START
            )
            notificationPopup.setListener { viewModel.generalSelected(it) }
            with(viewModel) {
                loadingCustom.bind {
                    mAdapter.isLoading = it
                    viewRefresh.isRefreshing = it
                }
                feedbacks.bind(::displays)
                totalRead.bind { topBar.state<SimpleWithMoreTopBarState>().setTitle(it) }
                options.bind { notificationPopup.items = it }
                itemOptions.bind { notificationItemPopup.items = it }
                confirmDeleteAll.bind { showDialogConfirmDelete(it) }
                confirmDelete.bind { showDialogConfirmDelete(it, false) }
            }
            appEvent.refreshNotifications.bind { viewModel.fetch() }
        }
    }

    private fun showDialogConfirmDelete(id: Long, hasAll: Boolean = true) {
        confirmDialog.showDelete(
            R.string.title_delete_notification,
            if (hasAll) R.string.msg_notification_delete_all
            else R.string.msg_notification_delete
        ) {
            if (hasAll)
                viewModel.deleteAll()
            else viewModel.delete(id)
        }
    }

    private fun displays(it: Pair<List<INotificationClient>, Boolean>) = lock(binding) {
        if (!it.second)//isMore
            mAdapter.clear()
        mAdapter.submit(it.first)
        viewNoData.show(it.first.isEmpty())
        rcvNotification.show(it.first.isNotEmpty())
    }

    private fun refresh() = with(viewModel) {
        mAdapter.clear()
        page = 1
        fetch()
    }

    private fun openDetails(it: INotificationClient) {
        if (it.type == OWNER_ACCOUNT_APPROVE) {
            messageDialog.show(R.string.title_approve_account, R.string.content_approve_account)
        } else {
            Router.navigate(self, Routing.AppointmentDetailClient(it.bookingID, it.id, it.salonID))
        }
    }
}

class NotificationVM(
    private val fetchAllRepo: FetchAllNotificationRepo,
    private val notificationRepo: NotificationRepository,
    private val notificationOptionRepo: NotificationOptionRepository
) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {
    var page = 1
    val loadingCustom: LoadingEvent = LoadingLiveData()
    val feedbacks = fetchAllRepo.results
    val totalRead = MutableLiveData<String>()
    val options = MutableLiveData<List<INotificationOption>>()
    val itemOptions = MutableLiveData<List<INotificationItemOption>>()
    val confirmDelete = SingleLiveEvent<Long>()
    val confirmDeleteAll = SingleLiveEvent<Long>()

    init {
        fetch()
        options()
    }

    fun fetch() = launch(loadingCustom, error) {
        fetchAllRepo(page)
        totalRead.post(notificationRepo.totalForTitleNotification())
    }

    private fun options() = launch(null, error) {
        options.post(notificationOptionRepo.general())
    }

    fun optionsItem(isRead: Boolean) = launch(null, error) {
        itemOptions.post(notificationOptionRepo.item(isRead))
    }

    fun generalSelected(it: INotificationOption) = launch(loading, error) {
        if (it.id == 1) {//Read all
            notificationOptionRepo.readAll()
            fetch()
        } else confirmDeleteAll.post(1L)
    }

    fun deleteAll() = launch(loading, error) {
        notificationOptionRepo.deleteAll()
        fetch()
    }

    fun itemSelected(it: Pair<Int, Long>) = launch(loading, error) {
        when (it.first) {
            1 -> {//read
                notificationOptionRepo.read(it.second)
                fetch()
            }
            2 -> {//unread
                notificationOptionRepo.unread(it.second)
                fetch()
            }
            3 -> {//delete
                confirmDelete.post(it.second)
            }
        }
    }

    fun delete(id: Long) = launch(loading, error) {
        notificationOptionRepo.delete(id)
        fetch()
    }

}


@Inject(ShareScope.Fragment)
class FetchAllNotificationRepo(
    private val notificationApi: NotificationClientApi,
    private val notificationFactory: NotificationClientFactory
) {
    val results = MutableLiveData<Pair<List<INotificationClient>, Boolean>>()

    suspend operator fun invoke(page: Int) {
        results.post(
            Pair(
                notificationFactory.createList(notificationApi.notifications(page).await()),
                page > 1
            )
        )
    }

}