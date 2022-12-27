package com.app.inails.booking.admin.datasource.remote.sockets

import android.support.di.Inject
import android.support.di.ShareScope
import io.socket.emitter.Emitter

@Inject(ShareScope.FragmentOrActivity)
class AuthSocket(private val client: SocketClient) {

    fun observeCustomerDeleteAccount(fn: Emitter.Listener) {
        client.mSocket?.on(CUSTOMER_DELETE_ACCOUNT, fn)
    }

    fun observeSalonDeleteAccount(fn: Emitter.Listener) {
        client.mSocket?.on(SALON_DELETE_ACCOUNT, fn)
    }

    companion object {
        private const val CUSTOMER_DELETE_ACCOUNT = "customer-del-account"
        private const val SALON_DELETE_ACCOUNT = "salon-del-account"
    }
}