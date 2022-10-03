package com.app.inails.booking.admin.views.booking.dialog_filter

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogSearchStaffBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.extention.onSearchListener
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.model.ui.IStaff


class SearchStaffDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogSearchStaffBinding::inflate)
    var onLoadMoreListener: ((String, Int) -> Unit?)? = null
    var onSearchListener: ((String) -> Unit?)? = null
    private var mAdapter = SearchStaffAdapter(binding.rvStaff)

    fun show(
        list: List<IStaff>,
        function: (IStaff) -> Unit
    ) {
        mAdapter.clear()
        mAdapter.submit(list)
        mAdapter.onClickItemListener = {
            dismiss()
            function.invoke(it)
        }

        mAdapter.onLoadMoreListener = { page, _ ->
            onLoadMoreListener?.invoke(binding.searchView.text.toString(), page)
        }

        binding.searchView.onClickSearchAction ={
            mAdapter.clear()
            onSearchListener?.invoke(binding.searchView.text.toString())
        }

        binding.btClose.onClick {
            dismiss()
        }
        super.show()
    }

    fun addList(list: List<IStaff>) {
        mAdapter.submit(list)
    }
}

interface SearchStaffOwner : ViewScopeOwner {
    val searchStaffDialog: SearchStaffDialog
        get() = with(viewScope) {
            getOr("search_staff:dialog") { SearchStaffDialog(context) }
        }
}