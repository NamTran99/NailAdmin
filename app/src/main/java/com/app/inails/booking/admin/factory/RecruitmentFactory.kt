package com.app.inails.booking.admin.factory

import android.support.di.Inject
import android.support.di.ShareScope
import com.app.inails.booking.admin.datasource.local.UserLocalSource
import com.app.inails.booking.admin.extention.*
import com.app.inails.booking.admin.factory.helper.FactoryHelper
import com.app.inails.booking.admin.formatter.TextFormatter
import com.app.inails.booking.admin.model.response.*
import com.app.inails.booking.admin.model.ui.*

@Inject(ShareScope.Singleton)
class RecruitmentFactory(textFormatter: TextFormatter,val userLocalSource: UserLocalSource,val salonFactory: SalonFactory) :
    FactoryHelper(textFormatter) {
    private fun createRecruitment(recruitmentDTO: RecruitmentDTO): IRecruitment {
        return object : IRecruitment {
            override val avatar: String
                get() = if (images.isNotEmpty()) recruitmentDTO.images[0].image else ""
            override val images: List<String>
                get() = recruitmentDTO.images.map { it.image }
            override val serverImages: List<AppImage>
                get() = recruitmentDTO.images?: listOf()
            override val create_at: String
                get() = recruitmentDTO.created_at.safe().convertFromServerToLocalTime(
                    DateTimeFormat.server_type_2,
                    DateTimeFormat.format5
                )
            override val title: String
                get() = recruitmentDTO.title.safe()
            override val description: String
                get() = recruitmentDTO.description.safe()
            override val isPublish: Boolean
                get() = recruitmentDTO.status == 1 // 1: publish, 2 unpublish
            override val id: Int
                get() = recruitmentDTO.id.safe()
            override val working_time_format: String
                get() = textFormatter.formatWorkingTime(working_form, working_term)

            override val position_format: String
                get() = textFormatter.formatPositionRecruitment(
                    recruitmentDTO.position.safe(),
                    recruitmentDTO.position_other.safe()
                )
            override val otherPosition: String
                get() = recruitmentDTO.position_other.safe()
            override val salary: Double
                get() = recruitmentDTO.salary.safe()
            override val working_form: Int
                get() = recruitmentDTO.working_form.safe()
            override val working_term: Int
                get() = recruitmentDTO.working_term.safe()
            override val create_at_2: String
                get() = recruitmentDTO.created_at.safe().convertFromServerToLocalTime(
                    DateTimeFormat.server_type_2,
                    DateTimeFormat.format6
                )
            override val salon: ISalonDetail
                get() = salonFactory.createDetail(recruitmentDTO.salon)
            override val position: Int
                get() = recruitmentDTO.position.safe()
            override val gender: Int
                get() = recruitmentDTO.gender.safe()
            override val genderFormat: String
                get() = textFormatter.formatGender(recruitmentDTO.gender.safe())
            override val experience: Double
                get() = recruitmentDTO.experience_required.safe()
            override val experience_format: String
                get() = textFormatter.formatExperienceJob(experience)
            override val experience_format_2: String
                get() = textFormatter.formatExperienceRecruitment(experience)
            override val work_place: String
                get() = "${recruitmentDTO.salon_city}, ${recruitmentDTO.salon_state}"
            override val salaryType: Int
                get() = recruitmentDTO.type_salary.safe()
            override val salary_format: String
                get() = textFormatter.formatSalary(salary, salaryType)
            override val salary_format_en: String
                get() = textFormatter.formatSalaryRecruitmentEn(salary, salaryType)
            override val isShuttleId: Int
                get() = if (recruitmentDTO.is_shuttle == 0) 1 else 0
            override val isShuttle: String
                get() = textFormatter.yesNoFormat(recruitmentDTO.is_shuttle.safe())
            override val isThereHouseId: Int
                get() = if (recruitmentDTO.is_there_house == 0) 1 else 0
            override val isThereHouse: String
                get() = textFormatter.yesNoFormat(recruitmentDTO.is_there_house.safe())
            override val customerSkinColorFormat: String
                get() = textFormatter.customerSkinColorFormat(recruitmentDTO.customer_skin_color.safe())
            override val salonExist: Int
                get() = recruitmentDTO.salon_exists_time.safe()
            override val salonExistFormat: String
                get() = textFormatter.formatExperienceRecruitment(recruitmentDTO.salon_exists_time.toDouble())
            override val averageIncome: String
                get() = recruitmentDTO.salary.safe().display()
            override val averageIncomeFormat: String
                get() = textFormatter.formatSalary(
                    recruitmentDTO.salary.safe(),
                    recruitmentDTO.type_salary.safe()
                )
            override val payrollMethod: Int
                get() = recruitmentDTO.salary_form.safe()
            override val payrollMethodFormat: String
                get() = textFormatter.formatPayrollType(recruitmentDTO.salary_form)
            override val skillSet: List<ISkill>
                get() = createListItemSkill(recruitmentDTO.skills ?: listOf())
            override val salonName: String
                get() = recruitmentDTO.salon_name.safe()
            override val contactPhone: String
                get() = recruitmentDTO.contact_phone.safe().formatPhoneUSCustom()
            override val salonCity: String
                get() = recruitmentDTO.salon_city.safe()
            override val salonState: String
                get() = recruitmentDTO.salon_state.safe()
            override val customerSkinColor: Int
                get() = recruitmentDTO.customer_skin_color.safe()
            override val isMyRecruitment: Boolean
                get() = recruitmentDTO.salon_id == userLocalSource.getSalonID()
        }
    }

    fun createListRecruitment(list: List<RecruitmentDTO>): List<IRecruitment> {
        return list.map(this::createRecruitment)
    }

    fun createARecruitment(item: RecruitmentDTO): IRecruitment {
        return createRecruitment(item)
    }

    private fun createJobProfileItem(itemDTO: JobProfileDTO): IJobProfile {
        return object : IJobProfile {
            override val avatar: String
                get() = itemDTO.avatar.safe()
            override val images: List<AppImage>
                get() = itemDTO.images
            override val phone: String
                get() = itemDTO.phone.safe().formatPhoneUSCustom()
            override val address: String
                get() = itemDTO.address.safe()
            override val workingArea: String
                get() = "${itemDTO.city}, ${itemDTO.state}"
            override val genderName: String
                get() = textFormatter.formatGender(itemDTO.gender.safe())
            override val email: String
                get() = itemDTO.email.safe()
            override val experienceFormat: String
                get() = textFormatter.formatExperienceJob(itemDTO.experience)
            override val experienceFormatEn: String
                get() = textFormatter.formatExperienceJobEn(itemDTO.experience)
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
            override val id: Int
                get() = itemDTO.id
            override val distance_format_1: String
                get() = textFormatter.formatDistance(itemDTO.distance.safe())
            override val format_gender: String
                get() = textFormatter.formatGender2(itemDTO.gender.safe())
            override val salaryType: Int
                get() = itemDTO.type_salary.safe()
            override val salaryDisplay: String
                get() = textFormatter.formatSalary(salary, salaryType)
            override val workplace_type_format: String
                get() = textFormatter.yesNoFormat(itemDTO.workplace_type.safe(1) - 1)
            override val skills: List<ISkill>
                get() = createListItemSkill(itemDTO.skills)
            override val skillsFormat: String
                get() = skills.joinToString(", ", transform = {it.name})
            override val workplace_type_format_2: String
                get() = textFormatter.formatWorkplaceType(itemDTO.workplace_type.safe())
            override val isPublish: Boolean
                get() = itemDTO.status == 1
        }
    }

    fun createJobProfileList(feedbacks: List<JobProfileDTO>): List<IJobProfile> {
        return feedbacks.map(::createJobProfileItem)
    }

    fun createAJobProfile(itemDTO: JobProfileDTO): IJobProfile {
        return createJobProfileItem(itemDTO)
    }

    private fun createStateItem(state: StateDTO): IState {
        return object : IState {
            override val id: Int
                get() = state.id.safe()
            override val fullName: String
                get() = state.full_name.safe()
            override val name: String
                get() = state.name.safe()
        }
    }

    fun createStateList(state: List<StateDTO>): List<IState> {
        return state.map(::createStateItem)
    }

    // city
    fun createItemCity(itemDTO: CityDTO): ICity {
        return object : ICity {
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
    private fun createItemSkill(itemDTO: SkillDTO): ISkill {
        return object : ISkill {
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