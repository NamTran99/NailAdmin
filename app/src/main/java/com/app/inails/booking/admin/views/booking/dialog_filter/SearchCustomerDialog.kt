package com.app.inails.booking.admin.views.booking.dialog_filter

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogSearchCustomerBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.ICustomer


class SearchCustomerDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogSearchCustomerBinding::inflate)
    var onLoadMoreListener: ((String, Int) -> Unit?)? = null
    var onSearchListener: ((String) -> Unit?)? = null
    private var mAdapter = SearchCustomerAdapter(binding.rvCustomers)

    fun show(
        list: List<ICustomer>,
        function: (ICustomer) -> Unit
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

        binding.searchView.onSearchListener {
            binding.searchView.setSelection(binding.searchView.text.toString().length)
            binding.btClear.show(binding.searchView.text.toString().isNotEmpty())
            mAdapter.clear()
            onSearchListener?.invoke(binding.searchView.text.toString())
            binding.searchView.showKeyboard(false)
        }
        binding.btClear.onClick {
            mAdapter.clear()
            binding.searchView.setText("")
            onSearchListener?.invoke(binding.searchView.text.toString())
            binding.searchView.showKeyboard(false)
            binding.btClear.hide()
        }

        binding.btClose.onClick {
            binding.searchView.setText("")
            binding.btClear.hide()
            dismiss()
        }
        super.show()
    }

    fun addList(list: List<ICustomer>) {
        mAdapter.submit(list)
    }
}

interface SearchCustomerOwner : ViewScopeOwner {
    val searchCustomerDialog: SearchCustomerDialog
        get() = with(viewScope) {
            getOr("search_customer:dialog") { SearchCustomerDialog(context) }
        }
}