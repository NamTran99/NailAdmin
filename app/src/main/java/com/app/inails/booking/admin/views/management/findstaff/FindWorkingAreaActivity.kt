package com.app.inails.booking.admin.views.management.findstaff

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.core.event.LiveDataStatusOwner
import android.support.core.event.WindowStatusOwner
import android.support.core.livedata.SingleLiveEvent
import android.support.core.livedata.combine1
import android.support.core.livedata.post
import android.support.core.route.BundleArgument
import android.support.core.route.argument
import android.support.core.view.viewBinding
import android.support.viewmodel.launch
import android.support.viewmodel.viewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseActivity
import com.app.inails.booking.admin.databinding.ActivityFindStateBinding
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.model.ui.ICity
import com.app.inails.booking.admin.model.ui.IState
import com.app.inails.booking.admin.repository.management.RecruitmentRepo
import com.app.inails.booking.admin.views.booking.create_appointment.dialog.CreateCustomerDialogOwner
import com.app.inails.booking.admin.views.management.findstaff.adapter.SelectCityAdapter
import com.app.inails.booking.admin.views.management.findstaff.adapter.SelectStateAdapter
import com.app.inails.booking.admin.views.widget.setEnable
import kotlinx.parcelize.Parcelize

class FindWorkingAreaActivity : BaseActivity(R.layout.activity_find_state),
    CreateCustomerDialogOwner {
    enum class SearchState {
        STATE, CITY, COMPLETE
    }
    private val args by lazy {argument<SearchCityStateForm>()}
    private val binding by viewBinding(ActivityFindStateBinding::bind)
    private val viewModel by viewModel<FindWorkingAreaVM>()
    private lateinit var stateAdapter: SelectStateAdapter
    private lateinit var cityAdapter: SelectCityAdapter
    val form: SearchCityStateForm get() = viewModel.formSearch
    private var stateSearch: SearchState = SearchState.STATE
        set(value) {
            field = value

            binding.apply {
                lvChangeData.hide(value == SearchState.STATE)
                btChangeState.hide(value == SearchState.STATE)
                btChangeCity.show(value == SearchState.COMPLETE)
                etSearch.setEnable(value != SearchState.COMPLETE)
                dividerChangeData.show(value == SearchState.COMPLETE)
                rvStates.show(value == SearchState.STATE)
                rvCities.show(value == SearchState.CITY)
                btSubmit.hide(value == SearchState.STATE)
                tvReset.show(value == SearchState.COMPLETE)
                btClose.hide(value == SearchState.COMPLETE)

                when (value) {
                    SearchState.STATE -> {
                        form.clearAll()
                        etSearch.clearText()
                        etSearch.setHint(R.string.search_by_state)
                    }
                    SearchState.CITY -> {
                        form.clearCity()
                        etSearch.clearText()
                        etSearch.setHint(R.string.search_by_city)
                    }
                    SearchState.COMPLETE -> {
                        etSearch.setText(form.format)
                    }
                }
            }
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.formSearch = args

        binding.apply {
            stateSearch = args.getStateSearch()

            btChangeState.text = form.stateFormat
            btChangeCity.text = form.citySearch
            viewModel.getListCity()

            etSearch.bind {
                btClose.show(it.isNotEmpty() && stateSearch != SearchState.COMPLETE)
                viewModel.filter(it.trim())
            }
            stateAdapter = SelectStateAdapter(rvStates).apply {
                onClickItem = {
                    btChangeState.text = form.setSate(it)
                    viewModel.getListCity()
                    stateSearch = SearchState.CITY
                }
            }
            cityAdapter = SelectCityAdapter(rvCities).apply {
                onClickItem = {
                    btChangeCity.text = form.setCity(it)
                    stateSearch = SearchState.COMPLETE
                }
            }
            btBack.onClick {
                finish()
            }
            btClose.onClick {
                etSearch.setText("")
            }
            tvReset.onClick {
                form.clearAll()
                stateSearch = SearchState.STATE
            }
            btChangeState.onClick {
                form.clearAll()
                stateSearch = SearchState.STATE
            }
            btChangeCity.onClick {
                form.clearCity()
                stateSearch = SearchState.CITY
            }
            btSubmit.onClick {
                finish()
                appEvent.findingWorkingArea.post(
                    form
                )
            }
            viewModel.apply {
                litStateResult.bind {
                    stateAdapter.submit(it)
                    (it.isEmpty() && stateSearch == SearchState.STATE).show(
                        listOf(
                            lvEmptyData
                        )
                    )
                }

                listCityResult.bind {
                    cityAdapter.submit(it)
                    (it.isEmpty() && stateSearch == SearchState.CITY).show(
                        listOf(
                            lvEmptyData
                        )
                    )
                }
            }
        }
    }
}

@Parcelize
data class SearchCityStateForm(
    var stateSearch: String = "",
    var stateFormat: String = "",
    var citySearch: String = "",
    var format: String = ""
) : BundleArgument {
    fun setSate(item: IState): String {
        stateSearch = item.name
        stateFormat = "${item.fullName} (${item.name})"
        format = stateFormat
        return stateFormat
    }

    fun setCity(item: ICity): String {
        citySearch = item.name
        format = "$citySearch, $stateFormat"
        return citySearch
    }

    fun formatDisplay(): String {
        stateFormat = stateSearch
        format = "$citySearch, $stateSearch"
        return format
    }

    fun getStateSearch(): FindWorkingAreaActivity.SearchState{
        return if(stateSearch.isBlank()){
            FindWorkingAreaActivity.SearchState.STATE
        }else if(citySearch.isBlank()){
            FindWorkingAreaActivity.SearchState.CITY
        }else
            FindWorkingAreaActivity.SearchState.COMPLETE
    }

    fun clearCity(){
        citySearch = ""
        format = stateFormat
    }

    fun clearAll(){
        stateSearch = ""
        stateFormat = ""
        citySearch = ""
        format = ""
    }
}

class FindWorkingAreaVM(
    private val recruitmentRepo: RecruitmentRepo,
) : ViewModel(),
    WindowStatusOwner by LiveDataStatusOwner() {
    var formSearch = SearchCityStateForm()

    val liveFilter = MutableLiveData("")
    var listStateAllResult = SingleLiveEvent<List<IState>>()
    val listCityAllResult = recruitmentRepo.listCity

    val litStateResult = listStateAllResult.combine1(liveFilter) { list, filter ->
        list.filter { it.fullName.lowerCaseContain(filter) || it.name.lowerCaseContain(filter) }
    }
    val listCityResult = listCityAllResult.combine1(liveFilter) { list, filter ->
        list.filter { it.name.lowerCaseContain(filter) }
    }

    init {
        getListState()
    }

    fun filter(filter: String) {
        liveFilter.post(filter)
    }

    private fun getListState() = launch(loading, error) {
        listStateAllResult.postValue(recruitmentRepo.getListState())
    }

    fun getListCity() = launch(loading, error) {
        recruitmentRepo.getListCity(formSearch.stateSearch)
    }
}