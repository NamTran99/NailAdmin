package com.app.inails.booking.admin.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.app.inails.booking.admin.BuildConfig
import com.app.inails.booking.admin.R
import com.esafirm.imagepicker.helper.LocaleManager

object Utils {

    fun checkAppInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.runningAppProcesses
            val componentInfo = taskInfo[0].importanceReasonComponent
            if (componentInfo.packageName == context.packageName) {
                isInBackground = false
            }
        }
        return isInBackground
    }

    fun getDisplayBuildConfig(context: Context, lan: String): String {
        val listItems = BuildConfig.VERSION_NAME.split("_")

        val display = LocaleManager.updateResources(context, lan).getString(R.string.version_format,listItems[0] )
        return if(listItems.size > 1) "${display}\n(${listItems[1]})" else display
    }
}