package com.app.inails.booking.admin.views.clients.appointment

import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.post
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseRefreshFragment
import com.app.inails.booking.admin.databinding.FragmentAppointmentBinding
import com.app.inails.booking.admin.datasource.remote.sockets.SocketClient
import com.app.inails.booking.admin.extention.lock
import com.app.inails.booking.admin.extention.show
import com.app.inails.booking.admin.model.form.ApmCancelForm
import com.app.inails.booking.admin.model.ui.client.IAppointmentClient
import com.app.inails.booking.admin.navigate.Router
import com.app.inails.booking.admin.navigate.Router.Companion.navigate
import com.app.inails.booking.admin.navigate.Routing
import com.app.inails.booking.admin.repository.client.CancelAppointmentRepo
import com.app.inails.booking.admin.repository.client.FetchAllAppointment
import com.app.inails.booking.admin.repository.client.RemoveAppointmentRepo
import com.app.inails.booking.admin.repository.client.StatusAppointmentRepo
import com.app.inails.booking.admin.views.booking.AppointmentAdapter
import com.app.inails.booking.admin.views.clients.dialog.booking.ApmCancelDialogOwner
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarClientState
import com.app.inails.booking.admin.views.widget.topbar.SimpleTopBarState
import com.app.inails.booking.admin.views.widget.topbar.TopBarOwner

class AppointmentFragment : BaseRefreshFragment(R.layout.fragment_appointment),
    TopBarOwner,
    ApmCancelDialogOwner {

    private val binding by viewBinding(FragmentAppointmentBinding::bind)
    private val viewModel by viewModel<AppointmentVM>()
    private lateinit var mAdapter: AppointmentClientAdapter

    override fun onRefreshListener() {
        viewModel.refresh()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topBar.setState(SimpleTopBarClientState(R.string.title_appointment)
        { activity?.onBackPressed() })

        binding.apply {
            mAdapter = AppointmentClientAdapter(rcvAppointment)
            mAdapter.onCancelClickListener = { showCancelDialog(it) }
            mAdapter.onFeedbackClickListener =
                { Router.redirectToSubmitFeedback(self, it.first.id, it.first.salonID) }
            mAdapter.onDeleteClickListener = { showDeleteDialog(it) }
            mAdapter.onSalonClickListener = { Router.open(self, Routing.SalonDetail(it)) }
            mAdapter.onDirectSalonClickListener =
                { appSettings.navigateMyLocationWithGoogleMap(it.first, it.second) }
            mAdapter.onItemClickListener =
                {
                    Router.navigate(
                        self,
                        Routing.AppointmentDetailClient(it.first, salonID = it.second)
                    )
                }
            with(viewModel) {
                appointments.bind { displays(it) }
                cancelled.bind { mAdapter.notifyStatusCancel(it) }
                removed.bind { mAdapter.notifyRemoved(it) }
                checkIn.bind { viewModel.refresh() }
            }
        }
        appEvent.notifyForAppointment.bind { viewModel.refresh() }
    }

    private fun displays(it: List<IAppointmentClient>?) = lock(binding) {
        mAdapter.submit(it)
        rcvAppointment.show(!it.isNullOrEmpty())
        viewNoData.show(it.isNullOrEmpty())
    }

    private fun showDeleteDialog(it: Pair<IAppointmentClient, Int>) {
        confirmDialog.showDelete(
            R.string.title_delete_appointment,
            getString(R.string.message_delete_appointment, it.first.idDisplay)
        ) {
            viewModel.remove(it)
        }
    }

    private fun showCancelDialog(item: Pair<IAppointmentClient, Int>) {
        apmCancelDialog.show {
            viewModel.form.apply {
                id = item.first.id
                reason = it
            }
            viewModel.submit(item.second)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.disconnectSocket()
    }
}

class AppointmentVM(
    private val fetchAllAppointment: FetchAllAppointment,
    private val cancelAppointmentRepo: CancelAppointmentRepo,
    private val removeAppointmentRepo: RemoveAppointmentRepo,
    private val client: SocketClient,
    statusAppointmentRepo: StatusAppointmentRepo
) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {
    val appointments = fetchAllAppointment.results
    val cancelled = MutableLiveData<Pair<Int, String>>()
    val removed = MutableLiveData<Int>()
    val checkIn = statusAppointmentRepo.checkInStatus

    init {
        client.connectIfNeed()
    }

    fun disconnectSocket() = launch(null) {
        client.disconnect()
    }


    val form = ApmCancelForm()

    fun refresh() = launch(refreshLoading, error) {
        fetchAllAppointment()
    }

    fun submit(position: Int) = launch(loading, error) {
        cancelAppointmentRepo(form)
        cancelled.post(position to form.reason!!)
    }

    fun remove(it: Pair<IAppointmentClient, Int>) = launch(loading, error) {
        removeAppointmentRepo(it.first.id)
        removed.post(it.second)
    }
}


