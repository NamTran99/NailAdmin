package com.app.inails.booking.admin.repository.client

import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.call
import android.support.di.Inject
import android.support.di.ShareScope
import android.util.Log
import com.app.inails.booking.admin.datasource.remote.sockets.AuthSocket

@Inject(ShareScope.FragmentOrActivity)
class DeleteAccountClientRepo(authSocket: AuthSocket) {

    val deletedCustomerAccount = SingleLiveEvent<Any>()
    val deletedSalonAccount = SingleLiveEvent<Any>()

    init {
        authSocket.apply {
            observeCustomerDeleteAccount {
                Log.e("----------> ", it[0].toString())
                deletedCustomerAccount.call()
            }

            observeSalonDeleteAccount {
                Log.e("----------> ", it[0].toString())
                deletedSalonAccount.call()
            }
        }
    }
}