package com.app.inails.booking.admin.base

import android.os.Bundle
import android.support.core.event.WindowStatusOwner
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.extention.colorResource
import com.app.inails.booking.admin.extention.colorSchemeDefault

abstract class BaseRefreshFragment(contentLayoutId: Int) : BaseFragment(contentLayoutId) {
    protected abstract fun onRefreshListener()
    private var mLoadingRefreshView: SwipeRefreshLayout? = null

    override fun onRegistryViewModel(viewModel: ViewModel) {
        if (viewModel is WindowStatusOwner) {
            viewModel.refreshLoading.bind { showLoadingRefresh(it) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLoadingRefreshView = view.findViewById(R.id.viewRefresh)
        mLoadingRefreshView?.colorSchemeDefault()
        mLoadingRefreshView?.setOnRefreshListener { onRefreshListener() }
    }

    fun showLoadingRefresh(it: Boolean?) {
        mLoadingRefreshView?.isRefreshing = it!!
    }
}