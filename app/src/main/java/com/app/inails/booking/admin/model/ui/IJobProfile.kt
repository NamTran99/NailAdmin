package com.app.inails.booking.admin.model.ui

import com.app.inails.booking.admin.model.response.AppImage

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