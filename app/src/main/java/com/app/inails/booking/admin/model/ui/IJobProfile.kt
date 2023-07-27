package com.app.inails.booking.admin.model.ui

import android.os.Parcelable
import com.app.inails.booking.admin.R
import com.app.inails.booking.admin.exception.resourceError
import com.app.inails.booking.admin.exception.viewError
import com.app.inails.booking.admin.exception.viewErrorCustom
import com.app.inails.booking.admin.extention.convertPhoneToNormalFormat
import com.app.inails.booking.admin.model.response.AppImage
import com.app.inails.booking.admin.views.management.findstaff.SearchCityStateForm
import kotlinx.parcelize.Parcelize

interface IJobProfile {
    val id: Int get() = 0
    val avatar: String get() = ""
    val images: List<AppImage> get() = listOf()
    val phone: String get() = ""
    val address: String get() = ""
    val workingArea: String get() = ""
    val genderName: String get() = "" // male, female
    val format_gender: String get() = "" // gender: male
    val gender: Int get() = 1 // 1:Male, 2 Female
    val email: String get() = ""
    val experienceFormat: String get() = ""
    val experienceFormatEn: String get() = ""
    val experience: Double get() = 0.0
    val description: String get() = ""
    val salaryDisplay: String get() = ""
    val salary: Double get() = 0.0
    val isStatusPublic: Boolean get() = false
    val isShowDes: Boolean get() = false
    val name: String get() = ""
    val distance_format_1: String get() = ""
    val salaryType: Int get() = 1
    val workplace_type_format: String get() = "" // yes no
    val workplace_type_format_2: String get() = "" // local, relocate
    val skills: List<ISkill> get() = listOf()
    val skillsFormat :String get() = ""  // abc, 213
    val isPublish: Boolean get() = true
    val city: String get() = ""
    val state: String get() = ""
    val workingPlaceType: Int get() = 1 // 1: local, 2 relocation
    val workingPlaceTypeFormat: String get() = "" // 1: local, 2 relocation
}

data class JobFilterForm(
    var search: String = "",
    var state: String? = null,
    var city: String? = null,
    var gender: Int? = null,
){
    fun clear(){
        state = null
        city = null
        gender = null
    }

    fun isShowFilter(): Boolean{
        return !(state == null && gender == null)
    }

    fun resetFilter(){
        state = null
        gender = null
    }

    fun setGender(index: Int){
        gender = when(index){
            0,3 -> null
            else -> index
        }
    }

}


@Parcelize
data class CreateUpdateJobForm(
    val deleteImage: MutableList<Int> = mutableListOf(),
    var name: String = "",
    var avatar: String = "",
    var images: List<String> = listOf(),
    var phone: String = "",
    var email: String = "",
    var gender: Int = 0, //1 male, 2 female
    var birthdate: String = "",
    var experience: String = "",
    var working_type: Int = 0, // 1: local, 2: relocate
    var city: String = "",
    var state: String = "",
    var des: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var salary: Double = 0.0,
    var zipCode: String = "",
    var salary_type: Int = 1,
    var skillDelete: MutableList<Int> = mutableListOf(),
    var skillMain: List<Int> = listOf(),
    var skillCustom: List<String> = listOf(),
    var skillDefaultLength: Int = 0,
    var stateFilterForm: SearchCityStateForm = SearchCityStateForm()
) : Parcelable {

    fun validate() {
        if (name.isBlank()) {
            viewError(R.id.etName, R.string.error_blank_name)
        }

        if (phone.isBlank()) {
            viewError(R.id.etPhone, R.string.error_blank_phone)
        }

        if (phone.trim().convertPhoneToNormalFormat().length < 10) {
            viewError(R.id.etPhone, R.string.error_type_phone_not_enough)
        }

        if (gender == 0) {
            resourceError(R.string.error_select_gender)
        }

        if(stateFilterForm.stateSearch.isBlank()){
            viewError(R.id.etWorking,R.string.error_blank_state)
        }

        if(stateFilterForm.citySearch.isBlank()){
            viewError(R.id.etWorking,R.string.error_blank_city)
        }

        if(working_type == 0){
            resourceError(R.string.error_blank_working_place_type)
        }

        if (experience.isBlank()) {
            viewErrorCustom(R.id.etExperience, R.string.error_blank_experience)
        }

        if(skillDefaultLength == 0){
            resourceError(R.string.error_skill_at_least_1)
        }
//        if (des.isBlank()) {
//            viewError(R.id.etDes, R.string.error_blank_description)
//        }
    }
}