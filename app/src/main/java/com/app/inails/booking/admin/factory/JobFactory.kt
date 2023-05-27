package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.extention.formatUsNumberCustom
import com.app.inails.booking.admin.extention.safe
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.*
import com.app.inails.booking.admin.model.ui.ICity
import com.app.inails.booking.admin.model.ui.IJobProfile
import com.app.inails.booking.admin.model.ui.ISkill
import com.app.inails.booking.admin.model.ui.IState

@Inject(ShareScope.Singleton)
class JobFactory(private val textFormatter: TextFormatter) {

    private fun createItem(itemDTO: JobProfileDTO): IJobProfile {
        return object : IJobProfile {
            override val avatar: String
                get() = itemDTO.avatar.safe()
            override val images: List<AppImage>
                get() = itemDTO.images
            override val phone: String
                get() = itemDTO.phone.safe().formatUsNumberCustom()
            override val address: String
                get() = itemDTO.address.safe()
            override val workingArea: String
                get() = itemDTO.workplace.safe()
            override val genderName: String
                get() = itemDTO.gender_name.safe()
            override val email: String
                get() = itemDTO.email.safe()
            override val experienceFormat: String
                get() = textFormatter.formatExperienceJob(itemDTO.experience)
            override val experienceFormatEn: String
                get() = textFormatter.formatExperienceJob(itemDTO.experience)
            override val isShowDes: Boolean
                get() = itemDTO.description.safe().isNotBlank()
            override val description: String
                get() = itemDTO.description.safe()
            override val isStatusPublic: Boolean
                get() = itemDTO.status == 1
            override val name: String
                get() = itemDTO.name.safe()
            override val gender: Int
                get() = itemDTO.gender.safe()
            override val salary: Double
                get() = itemDTO.salary.safe()
            override val experience: Double
                get() = itemDTO.experience
            override val city: String
                get() = itemDTO.city.safe()
            override val state: String
                get() = itemDTO.state.safe()
            override val id: Int
                get() = itemDTO.id.safe()
            override val salaryType: Int
                get() = itemDTO.type_salary.safe()
            override val salaryDisplay: String
                get() = textFormatter.formatSalary(itemDTO.salary, salaryType)
            override val workingPlaceType: Int
                get() = itemDTO.workplace_type.safe()
            override val skills: List<ISkill>
                get() = createListItemSkill(itemDTO.skills)
            override val workingPlaceTypeFormat: String
                get() = textFormatter.formatWorkplaceType(itemDTO.workplace_type.safe())
            override val skillsFormat: String
                get() = skills.joinToString(", ", transform = {it.name})
        }
    }

    private fun createItemList(feedbacks: List<JobProfileDTO>): List<IJobProfile> {
        return feedbacks.map(::createItem)
    }

    fun create(itemDTO: JobProfileDTO): IJobProfile {
        return createItem(itemDTO)
    }

    // state
    private fun createItemState(itemDTO: StateDTO): IState {
        return  object: IState{
            override val name: String
                get() = itemDTO.name.safe()
            override val fullName: String
                get() = itemDTO.full_name.safe()
            override val id: Int
                get() = itemDTO.id.safe()
        }
    }

     fun createItemListState(feedbacks: List<StateDTO>): List<IState> {
        return feedbacks.map(::createItemState)
    }

    // city
    fun createItemCity(itemDTO: CityDTO): ICity {
        return  object: ICity{
            override val name: String
                get() = itemDTO.name
            override val id: Int
                get() = itemDTO.id
        }
    }

    fun createItemListCity(feedbacks: List<CityDTO>): List<ICity> {
        return feedbacks.map(::createItemCity)
    }

    // skill
    private fun createItemSkill(itemDTO: SkillDTO): ISkill{
        return  object: ISkill{
            override val name: String
                get() = itemDTO.name
            override var selected: Boolean = false
            override var isServer: Boolean = false
            override val id: Int
                get() = itemDTO.id
        }
    }

    fun createListItemSkill(feedbacks: List<SkillDTO>): List<ISkill> {
        return feedbacks.map(::createItemSkill)
    }
}
