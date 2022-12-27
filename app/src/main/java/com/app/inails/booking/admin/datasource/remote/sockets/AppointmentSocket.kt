package com.app.inails.booking.admin.datasource.remote.sockets

import android.support.di.Inject
import android.support.di.ShareScope
import io.socket.emitter.Emitter

@Inject(ShareScope.Fragment)
class AppointmentSocket(private val client: SocketClient) {

    fun observeChangeStatus(fn: Emitter.Listener) {
        client.mSocket?.on(CHANGE_STATUS, fn)
    }

    companion object {
        private const val CHANGE_STATUS = "change-status-appointment"
    }

}