package com.app.inails.booking.admin.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.support.di.dependencies
import android.util.Log
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.app.appModule
import com.app.inails.booking.admin.helper.ResourceResolver
import com.google.android.libraries.places.api.Places
import com.google.android.youtube.player.internal.u

class MainApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: MainApplication? = null
        fun applicationContext(): Context {
            Log.d(
                "TAG", "applicationContext: ${
                    instance?.applicationContext?.resources?.configuration?.locale
                }"
            )

            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        Places.createClient(this)
        dependencies {
            modules(appModule, ResourceResolver.module(true))
        }
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT;
            }

            override fun onActivityStarted(p0: Activity) {
            }

            override fun onActivityResumed(p0: Activity) {
            }

            override fun onActivityPaused(p0: Activity) {
            }

            override fun onActivityStopped(p0: Activity) {
            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
            }

            override fun onActivityDestroyed(p0: Activity) {
            }

        })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectNonSdkApiUsage()
                    .penaltyLog()
                    .build()
            )
        }
    }
}
