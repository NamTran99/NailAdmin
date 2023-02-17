package com.app.inails.booking.admin.datasource.local.dao

import android.content.Context
import android.support.di.Inject
import android.support.di.ShareScope
import android.support.persistent.disk.DiskStorageFactory
import android.support.persistent.disk.SearchStrategy
import android.support.persistent.disk.StorageOptions
import com.app.inails.booking.admin.model.response.SalonDTO

@Inject(ShareScope.Singleton)
class DaoProvider(private val context: Context) {
    private val factory = DiskStorageFactory(context, version = 2, debug = true)

    val salonDAO by lazy {
        factory.create(
            StorageOptions(
                "salons", SalonDTO::class,
                keyOf = { id.toString() },
                searchStrategy = SearchStrategy.Text()
            )
        )
    }
}