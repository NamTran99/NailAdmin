package com.app.inails.booking.admin.base

import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.di.dependencies
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.app.appModule
import com.google.android.libraries.places.api.Places

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        Places.createClient(this)
        dependencies {
            modules(appModule)
        }
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks{
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
    }
}
