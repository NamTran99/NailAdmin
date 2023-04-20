package com.app.inails.booking.admin.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class JobProfileDTO(
    val address: String,
    val avatar: String? = "",
    val birthdate: String,
    val city: String,
    val created_at: String,
    val customer_id: Int,
    val description: String? = "",
    val email: String,
    val experience: Double,
    val gender: Int,
    val gender_name: String,
    val id: Int,
    val images: List<AppImage>,
    val lat: String,
    val lng: String,
    val name: String,
    val phone: String,
    val salary: Double,
    val state: String,
    val status: Int? = 1,
    val updated_at: String,
    val workplace: Any,
    val zipcode: Any,
    val distance: Double,
    val type_salary: Int = 1,
    val workplace_type_format: Int? = 1,
    val skills: List<SkillDTO> = listOf(),
    val workplace_type: Int? = 1
)

@Parcelize
class AppImage(
    val id: Int? = null,
    val image: String = ""
) : Parcelable