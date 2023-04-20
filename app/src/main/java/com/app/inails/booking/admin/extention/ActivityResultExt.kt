package com.app.inails.booking.admin.extention

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.base.BaseFragment

fun ActivityResultLauncher<String>.launchImage() {
    this.launch("image/*")
}

fun BaseFragment.resultSuccess(function: () -> Unit): ActivityResultLauncher<Intent> {
    return this.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK)
            function.invoke()
    }
}

fun BaseActivity.registerForActivityResultGPS(onResult: (Boolean) -> Unit = {})
        : ActivityResultLauncher<IntentSenderRequest> {
    return this.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        onResult(result.resultCode == Activity.RESULT_OK)
    }
}

fun BaseFragment.registerForActivityResultGPS(onResult: (Boolean) -> Unit = {})
        : ActivityResultLauncher<IntentSenderRequest> {
    return this.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        onResult(result.resultCode == Activity.RESULT_OK)
    }
}