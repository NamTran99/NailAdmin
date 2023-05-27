package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.exception.viewError
import com.app.inails.booking.admin.exception.viewErrorCustom
import com.app.inails.booking.admin.extention.convertPhoneToNormalFormat
import com.app.inails.booking.admin.model.response.AppImage
import kotlinx.parcelize.Parcelize

interface IRecruitment {
    val id: Int get() = 0
    val title: String get() = ""
    val salary: Double get() = 0.0
    val position: Int get() = 1 // 1: manager, 2 master, 3 assistant
    val description: String get() = ""
    val working_form: Int get() = -1 // 1 short-term, 2 long-term
    val working_term: Int get() = 1 // số tuần
    val images: List<String> get() = listOf()
    val serverImages: List<AppImage> get() = listOf()
    val avatar: String get() = ""
    val create_at: String get() = "" // MM-dd-yyyy
    val create_at_2: String get() = "" // hh:ss am MM-dd-yyyy
    val isPublish: Boolean get() = true
    val working_time_format: String get() = ""
    val salary_format: String get() = ""
    val salary_format_en: String get() = ""
    val position_format: String get() = ""
    val otherPosition: String get() = ""
    val salon: ISalonDetail? get() = null
    val gender: Int get() = 1
    val experience: Double get() = 0.0
    val experience_format: String get() = "" // 2 years
    val experience_format_2: String get() = "" // + 2 years
    val genderFormat: String get() = "" // male, female
    val work_place: String get() = ""
    val salaryType: Int get() = 1 // 1 week, month, year
    val isShuttle: String get() = "" //yes, no
    val isShuttleId: Int get() = 0 //yes, no
    val isThereHouse: String get() = "" //yes, no
    val isThereHouseId: Int get() = 0 //yes, no
    val customerSkinColorFormat: String get() = "" //white, black, ...
    val customerSkinColor: Int get() = 0 //1, 2, ...
    val salonExistFormat: String get() = ""
    val salonExist: Int get() = 0
    val averageIncomeFormat: String get() = ""
    val averageIncome: String get() = ""
    val payrollMethodFormat: String get() = ""
    val payrollMethod: Int get() = 0
    val skillSet: List<ISkill> get() = listOf()
    val salonName: String get() = ""
    val contactPhone: String get() = ""
    val salonCity: String get() = ""
    val salonState: String get() = ""
    val isMyRecruitment: Boolean get() = false
}


data class RecruitmentFilterForm(
    var search: String = "",
    var state: String? = null,
    var city: String? = null,
){
    fun isShowFilter(): Boolean{
        return state != null || city != null
    }

    fun isNotHaveDataSearch(): Boolean{
        return state == null && city == null && search.isBlank()
    }

    fun resetFilter(){
        state = null
        city = null
    }
}

interface ICity {
    val id: Int get() = 0
    val name: String get() = ""
}

interface ISkill {
    val id: Int get() = -1
    val name: String get() = ""
    var selected: Boolean
    var isServer: Boolean
}


@Parcelize
data class CreateUpdateRecruitmentForm(
    var id: Int? = null,
//    var working_form: Int = -1,
//    var working_term: String = "",
//    var position: Int = -1,
    var salary: String = "", // dành cho option bao lương
    var title: String = "",
    var images: List<String> = listOf(),
    var description: String = "",
//    var position_other: String = "",
    var gender: Int = -1,
    var experience: Double = 0.0,
    val deleteImage: MutableList<Int> = mutableListOf(),
    var salary_type: Int = 1, // 1 : week || 2 : month || 3 : year
    var city: String = "",
    var state: String = "",
    var skillDelete: MutableList<Int> = mutableListOf(),
    var skillMain: List<Int> = listOf(),
    var skillCustom: List<String> = listOf(),
    var skillDefaultLength: Int = 0,
    var ownerName: String = "",
    var ownerPhone: String = "",
    var is_there_house: Int = -1, // 1||0
    var is_shuttle: Int = -1, // 1||0
    var salary_form: Int = -1, // 1 : salary || 2 : ăn chia 4/6 || 3 : thương lượng
//    var income_weekly: Double = 0.0,
    var salon_exists_time: Int = -1,
    var customer_skin_color: Int = -1, // 1 : white || 2 : black || 3 : xi || 4 : tong hop
) : Parcelable {

    fun validate() {
        if (ownerName.isBlank()) {
            viewError(R.id.etOwnerName, R.string.error_blank_admin_name)
        }

        if (ownerPhone.trim().isBlank()) {
            viewError(R.id.etOwnerPhone, R.string.error_blank_phone)
        }
        if (ownerPhone.trim().convertPhoneToNormalFormat().length < 10) {
            viewError(R.id.etOwnerPhone, R.string.error_type_phone_not_enough)
        }

        if (state.isBlank()) {
            resourceError(R.string.error_blank_state)
        }

        if (city.isBlank()) {
            resourceError(R.string.error_blank_city)
        }

        if (salon_exists_time == -1 ){
            viewErrorCustom(R.id.etYearExist, R.string.error_pls_type_exist)
        }

        if (customer_skin_color == -1) {
            resourceError(R.string.error_choose_customer_skin)
        }

        if (title.isBlank()) {
            viewError(R.id.edtTitleAds, R.string.error_pls_advertising_title)
        }

        if (salary.isBlank()) {
            viewError(R.id.etSalary, R.string.error_pls_type_price)
        }
        if (salary.toDouble() <= 0) {
            viewError(R.id.etSalary, R.string.error_type_price_greater_than_0)
        }

        if (salary_form == -1 ){
            resourceError(R.string.error_choose_payroll_method)
        }

        if(gender == -1){
            resourceError(R.string.error_pls_select_gender)
        }

        if(skillDefaultLength == 0){
            resourceError(R.string.error_skill_at_least_1)
        }
//        if (working_form == -1) {
//            resourceError(R.string.error_pls_select_working_time)
//        }
//        if (working_form == 1) {
//            if (working_term.isBlank()) {
//                viewErrorCustom(R.id.etNumberOfWeek, R.string.error_pls_type_week)
//            }
//            if (working_term.toInt() < 0) {
//                viewErrorCustom(R.id.etNumberOfWeek, R.string.error_pls_type_week)
//            }
//        }
//        if (position == -1 && position_other.isBlank()) {
//            resourceError(R.string.error_pls_select_position)
//        }
//        if (gender == -1) {
//            resourceError(R.string.error_pls_select_gender)
//        }
//        if (description.isBlank()) {
//            viewError(R.id.edtContentAds, R.string.error_pls_advertising_content)
//        }
    }
}
