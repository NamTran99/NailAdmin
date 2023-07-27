//package com.app.inails.booking.admin.helper.location
//
//import android.app.Service
//import android.content.Intent
//import android.location.Location
//import android.os.IBinder
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.LifecycleRegistry
//
//abstract class LocationService : Service(), LifecycleOwner {
//    override fun getLifecycle(): Lifecycle {
//        return LifecycleRegistry(this)
//    }
//
//    override fun onBind(intent: Intent): IBinder? {
//        return null
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        myLocation.asLiveData().observe(this) { location: Location? -> onLocationUpdated(location) }
//    }
//
//    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        myLocation.requestUpdate()
//        return START_STICKY
//    }
//
//    override fun stopService(name: Intent): Boolean {
//        return super.stopService(name)
//    }
//
//    override fun onTaskRemoved(rootIntent: Intent) {
//        myLocation.removeUpdate()
//        super.onTaskRemoved(rootIntent)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//    }
//
//    protected abstract val myLocation: MyLocation
//    protected abstract fun onLocationUpdated(location: Location?)
//}