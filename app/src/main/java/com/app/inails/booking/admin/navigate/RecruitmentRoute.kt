package com.app.inails.booking.admin.navigate

import android.support.navigation.NavOptions
import android.support.navigation.findNavigator
import com.app.inails.booking.admin.base.BaseFragment
import com.app.inails.booking.admin.views.management.findstaff.CreateUpdatePostRecruitmentFragment
import com.app.inails.booking.admin.views.management.findstaff.DetailCandidateFragment
import com.app.inails.booking.admin.views.management.findstaff.DetailRecruitmentFragment
import com.app.inails.booking.admin.views.management.findstaff.MyRecruitmentFragment

interface RecruitmentRoute {
    fun BaseFragment.redirectToUpdateAds(id: Int)
    fun BaseFragment.redirectToDetailRecruitment(id: Int)
    fun BaseFragment.redirectToDetailCandidate(id: Int)
    fun BaseFragment.redirectToCreateAds()
    fun BaseFragment.redirectToMyRecruitment()
}

class RecruitmentRouteImpl : RecruitmentRoute {

    override fun BaseFragment.redirectToUpdateAds(id: Int) {
        findNavigator().navigate(
            CreateUpdatePostRecruitmentFragment::class,
            Routing.UpdateRecruitment(id).toBundle()
        )
    }


    override fun BaseFragment.redirectToDetailRecruitment(id: Int) {
        findNavigator().navigate(
            DetailRecruitmentFragment::class,
            Routing.DetailRecruitment(id).toBundle()
        )
    }

    override fun BaseFragment.redirectToDetailCandidate(id: Int) {
        findNavigator().navigate(
            DetailCandidateFragment::class,
            Routing.DetailCandidate(id).toBundle()
        )
    }

    override fun BaseFragment.redirectToCreateAds() {
        findNavigator().navigate(CreateUpdatePostRecruitmentFragment::class)
    }

    override fun BaseFragment.redirectToMyRecruitment() {
        findNavigator().navigate(
            MyRecruitmentFragment::class, null, NavOptions(
                popupTo = MyRecruitmentFragment::class,
                reuseInstance = true,
                inclusive = true
            )
        )
    }
}