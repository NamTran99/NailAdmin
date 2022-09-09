package com.app.inails.booking.admin.helper

import android.support.core.CoDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

object ApplicationScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + CoDispatcher.io
}