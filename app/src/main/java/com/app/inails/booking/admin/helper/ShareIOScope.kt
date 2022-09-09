package com.app.inails.booking.admin.helper

import android.support.core.CoDispatcher
import android.support.di.Inject
import android.support.di.ShareScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

@Inject(ShareScope.FragmentOrActivity)
class ShareIOScope : CoroutineScope, Closeable {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + CoDispatcher.io + CoroutineExceptionHandler { _, _ -> }

    override fun close() {
        cancel()
    }

}