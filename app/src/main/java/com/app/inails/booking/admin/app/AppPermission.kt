package com.app.inails.booking.admin.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.core.permission.*
import android.support.core.view.ViewScopeOwner
import androidx.activity.result.ActivityResultCaller
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.atomic.AtomicInteger

interface AppPermissionOwner : ViewScopeOwner {
    val appPermission
        get() = viewScope.getOr("app_permission") {
            when (this) {
                is Fragment -> AppPermission(this)
                is FragmentActivity -> AppPermission(this)
                else -> error("Not support get permission for ${this.javaClass.name} ")
            }
        }
}
class AppPermission {
    companion object {
        val PERMISSION_CAMERA = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val PERMISSION_NOTIFICATION = arrayOf(
            android.Manifest.permission.POST_NOTIFICATIONS)

        val PERMISSION_WRITE_STORAGE = arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val PERMISSION_LOCATION = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        val PERMISSION_CALL_WHATS_APP = arrayOf(
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.CALL_PHONE
        )

        val PERMISSION_ACCESS_STORAGE = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    private var mChecker: PermissionAccessible
    private val mNextLocalRequestCode = AtomicInteger()

    constructor(activity: FragmentActivity) {
        mChecker = PermissionAccessibleImpl().apply {
            setDispatcher(ActivityDispatcher(activity))
        }
    }

    constructor(fragment: Fragment) {
        mChecker = PermissionAccessibleImpl().apply {
            setDispatcher(FragmentDispatcher(fragment))
        }
    }

    fun accessCamera(function: () -> Unit): PermissionRequest {
        return mChecker.access(
            mNextLocalRequestCode.getAndIncrement(),
            *PERMISSION_CAMERA,
            onPermission = function
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun accessNotification(function: () -> Unit): PermissionRequest {
        return mChecker.access(
            mNextLocalRequestCode.getAndIncrement(),
            *PERMISSION_NOTIFICATION,
            onPermission = function
        )
    }

    fun checkWrite(function: (Boolean) -> Unit): PermissionRequest {
        return mChecker.check(
            mNextLocalRequestCode.getAndIncrement(),
            *PERMISSION_WRITE_STORAGE,
            onPermission = function
        )
    }

    fun forceAccessLocation(function: () -> Unit): PermissionRequest {
        return mChecker.forceAccess(
            mNextLocalRequestCode.getAndIncrement(),
            *PERMISSION_LOCATION,
            onPermission = function
        )
    }

    fun accessLocation(function: (Boolean) -> Unit): PermissionRequest {
        return mChecker.check(
            mNextLocalRequestCode.getAndIncrement(),
            *PERMISSION_LOCATION,
            onPermission = function
        )
    }

    fun allowLocation(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )) == PackageManager.PERMISSION_GRANTED
                && (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )) == PackageManager.PERMISSION_GRANTED
    }

    fun checkLocation(function: (Boolean) -> Unit): PermissionRequest {
        return mChecker.check(
            mNextLocalRequestCode.getAndIncrement(),
            *PERMISSION_LOCATION,
            onPermission = function
        )
    }

    fun accessCallWhatApp(function: () -> Unit): PermissionRequest {
        return mChecker.access(
            mNextLocalRequestCode.getAndIncrement(),
            *PERMISSION_CALL_WHATS_APP,
            onPermission = function
        )
    }

    fun accessCall(function: () -> Unit): PermissionRequest {
        return mChecker.access(
            mNextLocalRequestCode.getAndIncrement(),
            android.Manifest.permission.CALL_PHONE,
            onPermission = function
        )
    }

    fun accessReadStorage(function: () -> Unit): PermissionRequest {
        return mChecker.access(
            mNextLocalRequestCode.getAndIncrement(),
           *PERMISSION_ACCESS_STORAGE,
            onPermission = function
        )
    }

    fun accessWriteStorage(function: () -> Unit): PermissionRequest {
        return mChecker.access(
            mNextLocalRequestCode.getAndIncrement(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            onPermission = function
        )
    }

    fun accessPhoneCall(function: () -> Unit): PermissionRequest {
        return mChecker.access(
            mNextLocalRequestCode.getAndIncrement(),
            Manifest.permission.CALL_PHONE,
            onPermission = function
        )
    }
}