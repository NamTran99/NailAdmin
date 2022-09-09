package com.app.inails.booking.admin

import android.app.Application
import android.support.di.dependencies
import com.app.inails.booking.admin.app.appModule

@Suppress("unused")
class MainApplication : Application() {
		
		override fun onCreate() {
				super.onCreate()
				dependencies {
						modules(appModule)
				}
		}
}
