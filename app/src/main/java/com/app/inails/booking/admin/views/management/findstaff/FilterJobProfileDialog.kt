package com.app.inails.booking.admin.views.management.findstaff

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogFilterJobProfileBinding
import com.app.inails.booking.admin.extention.configSpinner
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.IState
import com.app.inails.booking.admin.model.ui.JobFilterForm

class FilterJobProfileDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogFilterJobProfileBinding::inflate)
    private var jobFilter = JobFilterForm()
    var onClickStateFilter: (() -> Unit) = {}

    init {
        setUpView()
    }

    private fun setUpView() {
        with(binding) {
            btClose.onClick {
                dismiss()
            }
            etWorking.onClick {
                onClickStateFilter.invoke()
            }
            val listGender = context.resources.getStringArray(R.array.arr_gender_filter)
            spGender.configSpinner(true, listGender) { position ->
                jobFilter.setGender(position)
            }

//            val listSalary = context.resources.getStringArray(R.array.arr_salary_filter)
//            spSalary.configSpinner(true, listSalary) { position ->
//                jobFilter.setSalary(position)
//            }
//
//            val listExperience = context.resources.getStringArray(R.array.arr_experience_filter)
//            spExperience.configSpinner(true, listExperience) { position ->
//                jobFilter.setExperience(position)
//            }
        }
    }

    fun setWorkingArea(form: SearchCityStateForm) {
        jobFilter.state = form.stateSearch
        jobFilter.city = form.citySearch
        binding.etWorking.setText(form.format)
    }

    fun show(item: JobFilterForm, onResetClick: (()-> Unit),onSubmit: (JobFilterForm) -> Unit) {
        super.show()
        jobFilter = item
        with(binding) {
            btSubmit.onClick {
                onSubmit.invoke(jobFilter)
                dismiss()
            }
            tvReset.onClick {
                spGender.setSelection(0, true)
//                spSalary.setSelection(0, true)
//                spExperience.setSelection(0, true)
                onResetClick.invoke()
                etWorking.setText("")
                jobFilter.resetFilter()
                onSubmit.invoke(jobFilter)
            }
        }
    }
}

interface FilterJobProfileOwner : ViewScopeOwner {
    val filterJobDialog: FilterJobProfileDialog
        get() = with(viewScope) {
            getOr("filter:jobDialog") {
                FilterJobProfileDialog(context)
            }
        }
}
