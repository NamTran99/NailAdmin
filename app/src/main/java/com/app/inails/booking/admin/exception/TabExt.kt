package com.app.inails.booking.admin.exception

import com.google.android.material.tabs.TabLayout

fun TabLayout.setOnSelected(func: ((Int) -> Unit)){
    this.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
        override fun onTabReselected(tab: TabLayout.Tab?) {

        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {

        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            func.invoke(tab!!.position)
        }

    })
}

fun TabLayout.setOnReSelected(func: ((Int) -> Unit)){
    this.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
        override fun onTabReselected(tab: TabLayout.Tab?) {
            func.invoke(tab!!.position)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {

        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
        }

    })
}