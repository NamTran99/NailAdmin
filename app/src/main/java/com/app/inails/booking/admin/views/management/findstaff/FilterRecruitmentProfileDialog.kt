package com.app.inails.booking.admin.views.management.findstaff

import android.content.Context
import android.support.core.view.ViewScopeOwner
import com.app.inails.booking.admin.base.BaseDialog
import com.app.inails.booking.admin.databinding.DialogFilterRecruitmentProfileBinding
import com.app.inails.booking.admin.extention.onClick
import com.app.inails.booking.admin.model.ui.JobFilterForm
import com.app.inails.booking.admin.model.ui.RecruitmentFilterForm

class FilterRecruitmentProfileDialog(context: Context) : BaseDialog(context) {
    private val binding = viewBinding(DialogFilterRecruitmentProfileBinding::inflate)
    private var jobFilter = RecruitmentFilterForm()
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
        }
    }

    fun setWorkingArea(form: SearchCityStateForm) {
        jobFilter.state = form.stateSearch
        jobFilter.city = form.citySearch
        binding.etWorking.setText(form.format)
    }

    fun show(item: RecruitmentFilterForm, resetOnclick: (()-> Unit), onSubmit: (RecruitmentFilterForm) -> Unit) {
        super.show()
        jobFilter = item
        with(binding) {
            btSubmit.onClick {
                onSubmit.invoke(jobFilter)
                dismiss()
            }
            tvReset.onClick {
                resetOnclick.invoke()
                etWorking.setText("")
                jobFilter.resetFilter()
            }
        }
    }
}

interface FilterRecruitmentProfileDialogOwner : ViewScopeOwner {
    val filterRecruitmentDialog: FilterRecruitmentProfileDialog
        get() = with(viewScope) {
            getOr("filter:recruitmentDialog") {
                FilterRecruitmentProfileDialog(context)
            }
        }
}
