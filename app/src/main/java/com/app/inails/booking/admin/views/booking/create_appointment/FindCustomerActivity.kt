package com.app.inails.booking.admin.views.booking.create_appointment

import FetchListCustomerRepo
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.combine1
import android.support.core.livedata.forceRefresh
import android.support.core.livedata.post
import android.support.core.route.BundleArgument
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivityFindCustomerBinding
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.ICustomer
import com.app.inails.booking.admin.views.booking.create_appointment.adapter.SelectCustomerAdapter
import com.app.inails.booking.admin.views.booking.create_appointment.dialog.CreateCustomerDialogOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.parcelize.Parcelize

@Parcelize
class FindCustomer(val phone: String) : BundleArgument

class FindCustomerActivity : BaseActivity(R.layout.activity_find_customer),
    CreateCustomerDialogOwner {
    private val binding by viewBinding(ActivityFindCustomerBinding::bind)
    private val viewModel by viewModel<FindCustomerVM>()
    val args by lazy { argument<FindCustomer>() }
    private lateinit var adapter: SelectCustomerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.apply {
            etSearch.bind {
                btClose.show(it.isNotEmpty())
                viewModel.searchCustomer(it.convertPhoneToNormalFormat())
            }
            etSearch.showKeyboard()
            etSearch.setText(args.phone)
            btAddCustomer.onClick {
                createCustomerDialog.show(
                    etSearch.text.toString().convertPhoneToNormalFormat()
                ) { phone, name ->
                    viewModel.addCustomer(phone, name)
                }
            }
            adapter = SelectCustomerAdapter(rvCustomers).apply {
                onClickItem = {
                    setResultBack(it)
                }
            }
            btBack.onClick {
                finish()
            }
            btClose.onClick {
                etSearch.setText("")
            }
            viewModel.apply {
                results.bind {
                    if (!it.second) {
                        tvEmptyData.show(it.first.isEmpty())
                        imageView3.show(it.first.isEmpty())
                        lvLoading.hide(it.first.isEmpty())
                    } else {
                        lvLoading.show()
                        tvEmptyData.hide()
                        imageView3.hide()
                    }
                    progressBar.visible(it.second)
                    adapter.submit(it.first)
                }
                addCustomerResult.bind {
                    Log.d("TAG", "onCreate: namTD8 name:${it.name}, phone: ${it.phone}")
                    setResultBack(it)
                }
                searchCustomer(etSearch.text.toString().convertPhoneToNormalFormat())
            }
        }
    }

    private fun setResultBack(item: ICustomer) {
        finish()
        appEvent.findingCustomer.post(
            item to false
        )
    }
}

class FindCustomerVM(
    private val fetchListCustomerRepo: FetchListCustomerRepo,
) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {
    init {
        (refreshLoading as MutableLiveData<Boolean>).value = true
    }

    private val resultItem = fetchListCustomerRepo.results
    val results =
        resultItem.combine1(refreshLoading as MutableLiveData<Boolean>) { items, loading ->
            items to loading
        }
    val addCustomerResult = fetchListCustomerRepo.addCustomerResult
    val searchScope = CoroutineScope(Dispatchers.Main)

    fun searchCustomer(search: String = "") {

        launch(refreshLoading, error, searchScope.coroutineContext, true) {
            resultItem.value =
                resultItem.value?.filter { it.phone.convertPhoneToNormalFormat().contains(search) }
            resultItem.forceRefresh()
            fetchListCustomerRepo(search)
        }
    }

    private fun checkValidateCustomer(phone: String, name: String) {
        if (phone.trim().isEmpty()) {
            resourceError(R.string.error_blank_phone)
        }
        if (phone.trim().convertPhoneToNormalFormat().length < 10) {
            resourceError(R.string.error_type_phone_not_enough)
        }
        if (name.isBlank()) {
            resourceError(R.string.error_blank_name)
        }
    }

    fun addCustomer(phone: String, name: String) = launch(loading, error) {
        checkValidateCustomer(phone, name)
        fetchListCustomerRepo.addCustomer(phone.convertPhoneToNormalFormat(), name)
    }
}